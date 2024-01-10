package nl.han.shared.datastructures;

import lombok.*;
import nl.han.ISavable;
import nl.han.shared.datastructures.world.Coordinate;
import nl.han.shared.enums.ItemData;
import nl.han.shared.enums.ItemType;
import org.hsqldb.types.Types;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * The Item class represents a game item with data and properties. <br/>
 * This abstract class implements the ISavable interface, providing methods for
 * database interactions associated with items.
 *
 * @author Jordan Geurtsen
 * @see ItemData
 * @see ISavable
 */
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Item implements ISavable<Item> {

    private final UUID id;
    private final ItemData itemData;
    private Coordinate coordinate;
    private UUID creatureId;
    private UUID gameId;

    /**
     * Provides the value of the item.
     *
     * @return an integer representing the value of the item.
     * @author Jordan Geurtsen
     */
    public int getValue() {
        return itemData.getValue();
    }

    /**
     * Provides the spawn chance of the item in the game.
     *
     * @return a float representing the spawn chance of the item.
     * @author Jordan Geurtsen
     */
    public float spawnChance() {
        return itemData.getSpawnWeight();
    }

    /**
     * Provides the type of the item.
     *
     * @return a value of ItemType representing the type of the item.
     * @author Jordan Geurtsen
     */
    public ItemType getType() {
        return itemData.getType();
    }

    /**
     * {@inheritDoc}
     * Creates the item table.
     * <br/>
     * This table has the following columns:
     * <ul>
     * <li>item_id</li>
     * <li>chunk_id</li>
     * <li>x_coordinate</li>
     * <li>y_coordinate</li>
     * <li>item_type</li>
     * <li>creature_id</li>
     * <li>game_id</li>
     * </ul>
     * <br/>
     * The item_id is the primary key of the table and is an INT.
     *
     * @param connection The connection.
     * @throws SQLException If the query fails.
     * @author Jordan Geurtsen, Rieke Jansen
     */
    @Override
    public void tableInit(Connection connection) throws SQLException {
        String creationString = "CREATE TABLE IF NOT EXISTS item (" +
                "item_id UUID PRIMARY KEY NOT NULL," +
                "chunk_id UUID FOREIGN KEY REFERENCES chunk(chunk_id) NULL," +
                "x_coordinate int NULL," +
                "y_coordinate int NULL," +
                "item_type int NOT NULL," +
                "creature_id UUID FOREIGN KEY REFERENCES creature(creature_id) ON DELETE CASCADE NULL," +
                "game_id UUID FOREIGN KEY REFERENCES game(game_id) ON DELETE CASCADE NULL)";
        try (PreparedStatement statement = connection.prepareStatement(creationString)) {
            statement.execute();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @throws SQLException If the query fails.
     * @author Jordan Geurtsen, Rieke Jansen
     */
    @Override
    public void insert(Connection connection) throws SQLException {
        String query = "INSERT INTO item(item_id,x_coordinate,y_coordinate,item_type,creature_id,game_id)" +
                "VALUES(?,?,?,?,?,?); ";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, id);
            if (coordinate != null) {
                statement.setInt(2, coordinate.x());
                statement.setInt(3, coordinate.y());
            } else {
                // TODO: Fix de error die hier gegooit wordt wanneer er een item in de inventory
                // van de speler zit.
                // Volgens links online is dit hoe het moet maar toch werkt het niet.
                // https://www.baeldung.com/jdbc-insert-null-into-integer-column
                statement.setNull(2, Types.INTEGER);
                statement.setNull(3, Types.INTEGER);
            }
            statement.setInt(4, itemData.getType().ordinal());
            statement.setString(5, String.valueOf(creatureId));
            statement.setObject(6, gameId);
            statement.execute();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @param connection
     * @return
     * @author Rieke Jansen
     */
    @Override
    public boolean exists(Connection connection) throws SQLException {
        String sql = "SELECT 1 FROM item WHERE item_id=?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, String.valueOf(id));
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @throws SQLException If the query fails.
     * @author Jordan Geurtsen, Rieke Jansen
     */
    @Override
    public void update(Connection connection) throws SQLException {
        String query = "UPDATE item SET x_coordinate=?,y_coordinate=?,item_type=?,creature_id=? " +
                "WHERE item_id=?;";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, coordinate.x());
            statement.setInt(2, coordinate.y());
            statement.setInt(3, itemData.getType().ordinal());
            statement.setObject(4, creatureId);
            statement.setObject(5, id);
            statement.execute();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @param connection The {@link Connection} to the database.
     * @param id
     * @return ResultSet of all items in the game.
     * @throws SQLException
     * @author Rieke Jansen
     */
    @Override
    public ResultSet getLoad(Connection connection, String id) throws SQLException {
        String query = "SELECT item_id,creature_id,item_type,x_coordinate,y_coordinate FROM item WHERE game_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, id);
            return statement.executeQuery();
        }
    }

    /**
     * {@inheritDoc}
     * Maps a ResultSet to an ArrayList<Item>
     *
     * @param itemSet set of all the items in the game
     * @return an ArrayList of items
     * @throws SQLException If the query fails.
     * @author Rieke Jansen
     */
    @Override
    public ArrayList<Item> mapList(ResultSet itemSet, Connection connection) throws SQLException {
        ArrayList<Item> mapped = new ArrayList<>();
        while (itemSet.next()) {
            UUID itemId = UUID.fromString(itemSet.getString("item_id"));
            Coordinate itemCoordinate = new Coordinate(itemSet.getInt("x_coordinate"), itemSet.getInt("y_coordinate"));
            itemSet.getInt("chunk_id");
            UUID itemOwner = UUID.fromString(itemSet.getString("creature_id"));
            ItemData dataItem;
            int itemType = itemSet.getInt("item_type");
            UUID loadedGameId = UUID.fromString(itemSet.getString("game_id"));
            if (itemType == 0) {
                dataItem = ItemData.SMALL_HEALTH_POTION;
            } else {
                dataItem = ItemData.MEDIUM_HEALTH_POTION;
            }
            Item item = new Item(itemId, dataItem, itemCoordinate, itemOwner, loadedGameId);
            mapped.add(item);
        }
        return mapped;
    }

    /**
     * Returns the character representing the data of the item.
     *
     * @return Character representation of the Item.
     * @author Fabian van Os
     * @see ItemData
     */
    public Character getCharacter() {
        return itemData.getCharacter();
    }

    /**
     * Returns the color of the character representing the item's data.
     *
     * @return Color of the character
     * @author Fabian van Os
     * @see ItemData
     */
    public Color getCharacterColor() {
        return itemData.getCharacterColor();
    }

    /**
     * Returns the background color of the item based on its data.
     *
     * @return Color for the background
     * @author Fabian van Os
     * @see ItemData
     */
    public Color getBackgroundColor() {
        return itemData.getBackgroundColor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return itemData.getName();
    }

    /**
     * Get the item's name.
     * 
     * @return the item's name
     * @author Jasper Kooy
     */
    public String getName() {
        return itemData.getCharacter() + " " + itemData.getName();
    }
}
