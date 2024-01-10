package nl.han.shared.datastructures.creature;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;
import nl.han.ISavable;
import nl.han.shared.datastructures.BoundedValue;
import nl.han.shared.datastructures.Config;
import nl.han.shared.datastructures.Item;
import nl.han.shared.datastructures.world.Coordinate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Player class representing a player in the game.
 *
 * @author Jordan Geurtsen
 */
@Log
@Getter
@Setter
@NoArgsConstructor(force = true)
public class Player extends Creature {

    private String name;
    private final String ipAddress;
    private final List<Item> inventory = new ArrayList<>();

    /**
     * Creates a new Player with the specified id, name and coordinates.
     *
     * @param id         The unique id for this player.
     * @param name       The name of this player.
     * @param coordinate The location of this player on the map.
     * @param gameId     The id of the game of this player.
     * @param config     The config of the agent of this player.
     * @param health     The health points of this player.
     * @param power      The power points of this player.
     * @param stamina    The amount of stamina of this player.
     * @author Jordan Geurtsen
     */
    public Player(UUID id, String name, Coordinate coordinate, UUID gameId, Config config,
                  BoundedValue health, BoundedValue stamina, BoundedValue power, String ipAddress) {
        super(id, coordinate, gameId, config, health, stamina, power);
        this.name = name;
        this.ipAddress = ipAddress;
    }

    /**
     * Adds items to the players inventory.
     *
     * @param items ArrayList of the items to be added.
     * @author Rieke Jansen
     */
    public void addItems(List<Item> items) {
        for (Item item : items) {
            item.setCreatureId(getId());
            this.inventory.add(item);
        }
    }


    /**
     * Removes the item from the players inventory.
     *
     * @param itemID the item to be removed.
     * @author Rieke Jansen, Thomas Droppert
     */
    public void removeInventoryItem(UUID itemID) {
        inventory.removeIf(item -> Objects.equals(item.getId(), itemID));
    }


    /**
     * Returns the items of a player.
     *
     * @throws SQLException If the query fails.
     * @author Rieke Jansen
     * @see ISavable
     * @see Connection
     */
    public ResultSet getItemsStatement(Connection connection) throws SQLException {
        String query = "SELECT item_id,creature_id,item_type FROM item WHERE game_id = ? AND creature_id=?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, String.valueOf(getGameId()));
            statement.setString(2, String.valueOf(getId()));
            return statement.executeQuery();
        }
    }


    /**
     * {@inheritDoc}
     * Creates the player table.
     * <br/>
     * This table has the following columns:
     * <ul>
     *     <li>player_id</li>
     *     <li>player_name</li>
     *     <li>game_id</li>
     *     <li>team_id</li>
     *     <li>entity_id</li>
     *     <li>config_id</li>
     *     <li>health_id</li>
     *     <li>stamina_id</li>
     *     <li>power_id</li>
     * </ul>
     * <br/>
     * The player_id is the primary key of the table and is an INT.
     *
     * @param connection The connection.
     * @throws SQLException If the query fails.
     * @author Jordan Geurtsen, Rieke Jansen
     */
    @Override
    public void tableInit(Connection connection) throws SQLException {
        super.tableInit(connection);
        String creationString = "CREATE TABLE IF NOT EXISTS player (" +
                "creature_id UUID PRIMARY KEY FOREIGN KEY REFERENCES creature(creature_id) ON DELETE CASCADE," +
                "player_name varchar(255) NOT NULL," +
                "team_id UUID FOREIGN KEY REFERENCES team(team_id) ON DELETE CASCADE NULL);";
        try (PreparedStatement statement = connection.prepareStatement(creationString)) {
            statement.execute();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws SQLException If the query fails.
     * @author Jordan Geurtsen, Rieke Jansen
     * @see Connection
     */
    @Override
    public void insert(Connection connection) throws SQLException {
        super.insert(connection);

        String query = "INSERT INTO player(creature_id,player_name,team_id) VALUES(?,?,?);";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, String.valueOf(getId()));
            statement.setString(2, name);
            statement.setString(3, null);
            statement.execute();
        }

        for (Item item : inventory) {
            item.insert(connection);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws SQLException If the query fails.
     * @author Jordan Geurtsen, Rieke Jansen
     * @see Connection
     */
    @Override
    public void update(Connection connection) throws SQLException {
        super.update(connection);
        String query = "UPDATE player SET player_name=?,team_id=? WHERE creature_id=?;";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, null);
            statement.setString(3, String.valueOf(getId()));
            statement.execute();
        }
        for (Item item : inventory) {
            item.save(connection);
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
        String sql = "SELECT 1 FROM player WHERE creature_id=?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, String.valueOf(getId()));
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws SQLException If the query fails.
     * @author Rieke Jansen
     * @see ISavable
     * @see Connection
     */
    @Override
    public ResultSet getLoad(Connection connection, String id) throws SQLException {
        String query = "SELECT P.PLAYER_NAME, " +
                "CR.CREATURE_ID," +
                "CR.X_COORDINATE, " +
                "CR.Y_COORDINATE, " +
                "CR.GAME_ID, " +
                "CR.CHUNK_ID, " +
                "CH.X_COORDINATE AS CHUNK_X," +
                "CH.Y_COORDINATE AS CHUNK_Y," +
                "CO.CONFIG_ID, " +
                "CO.CONFIG_RULES, " +
                "HP.VALUE_ID    AS HEALTH_ID, " +
                "HP.VALUE       AS HEALTH_VALUE, " +
                "HP.UPPERBOUND  AS HEALTH_UPPERBOUND, " +
                "HP.LOWERBOUND  AS HEALTH_LOWERBOUND, " +
                "POW.VALUE_ID   AS POWER_ID, " +
                "POW.VALUE      AS POWER_VALUE, " +
                "POW.UPPERBOUND AS POWER_UPPERBOUND, " +
                "POW.LOWERBOUND AS POWER_LOWERBOUND, " +
                "ST.VALUE_ID    AS STAMINA_ID, " +
                "ST.VALUE       AS STAMINA_VALUE, " +
                "ST.UPPERBOUND  AS STAMINA_UPPERBOUND, " +
                "ST.LOWERBOUND  AS STAMINA_LOWERBOUND " +
                "FROM PLAYER P " +
                "JOIN CREATURE CR ON CR.CREATURE_ID = P.CREATURE_ID " +
                "JOIN CONFIG CO ON CO.CONFIG_ID = CR.CONFIG_ID " +
                "JOIN BOUNDED_VALUE HP ON HP.VALUE_ID = CR.HEALTH_ID " +
                "JOIN BOUNDED_VALUE POW ON POW.VALUE_ID = CR.POWER_ID " +
                "JOIN BOUNDED_VALUE ST ON ST.VALUE_ID = CR.STAMINA_ID " +
                "JOIN CHUNK CH ON CR.CHUNK_ID = CH.CHUNK_ID " +
                "WHERE CR.GAME_ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, id);
            return statement.executeQuery();
        }
    }

    /**
     * Maps a ResultSet to an ArrayList<Item>
     *
     * @param playerSet set of all the players in the game
     * @return an ArrayList of players
     * @throws SQLException If the query fails.
     * @author Rieke Jansen
     */
    @Override
    public List<Creature> mapList(ResultSet playerSet, Connection connection) throws SQLException {
        ArrayList<Creature> mapped = new ArrayList<>();
        while (playerSet.next()) {
            UUID id = UUID.fromString(playerSet.getString("player_id"));
            String playerName = playerSet.getString("player_name");
            Coordinate coordinate = new Coordinate(playerSet.getInt("x_coordinate"), playerSet.getInt("y_coordinate"));
            UUID gameId = UUID.fromString(playerSet.getString("game_id"));
            Config config = new Config(
                    UUID.fromString(playerSet.getString("config_id")),
                    playerSet.getString("config_rules"),
                    "player"
            );
            BoundedValue health = new BoundedValue(
                    UUID.fromString(playerSet.getString("health_id")),
                    playerSet.getInt("health_value"),
                    playerSet.getInt("health_upperbound"),
                    playerSet.getInt("health_lowerbound")
            );
            BoundedValue stamina = new BoundedValue(
                    UUID.fromString(playerSet.getString("stamina_id")),
                    playerSet.getInt("stamina_value"),
                    playerSet.getInt("stamina_upperbound"),
                    playerSet.getInt("stamina_lowerbound")
            );
            BoundedValue power = new BoundedValue(
                    UUID.fromString(playerSet.getString("power_id")),
                    playerSet.getInt("power_value"),
                    playerSet.getInt("power_upperbound"),
                    playerSet.getInt("power_lowerbound")
            );
            Player player = new Player(id, playerName, coordinate, gameId, config, health, stamina, power, "");
            mapped.add(player);
        }
        return mapped;
    }

    /**
     * Method to add an {@link Item item} to a {@link Player player's} inventory
     *
     * @param item The {@link Item item} to add
     */
    public void addItemToInventory(Item item) {
        inventory.add(item);
    }

    @Override
    public String toString() {
        return "Player{" + "name='" + name + '\'' + ", id=" + getId() + ", coordinate=" + getCoordinate() + ", health=" + getHealth() + ", power=" + getPower() + ", stamina=" + getStamina() + '}';
    }

    /**
     * Method to convert a {@link Player player} to a JSON string
     *
     * @return The JSON string
     */
    public String toJson() {
        return "{" +
                "\"id\":\"" + getId() + "\"," +
                "\"name\":\"" + name + "\"," +
                "\"coordinate\":" + getCoordinate().toJson() + "," +
                "\"health\":" + getHealth().toJson() + "," +
                "\"power\":" + getPower().toJson() + "," +
                "\"stamina\":" + getStamina().toJson() + "," +
                "\"chunk\":" + getChunk().toJson() + "," +
                "\"config\":" + getConfig().toJson() +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(getId(), player.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, ipAddress, inventory);
    }
}