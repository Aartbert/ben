package nl.han.shared.datastructures.creature;

import lombok.*;
import nl.han.ISavable;
import nl.han.shared.datastructures.BoundedValue;
import nl.han.shared.datastructures.Config;
import nl.han.shared.datastructures.world.Chunk;
import nl.han.shared.datastructures.world.Coordinate;
import nl.han.shared.enums.TileType;
import nl.han.shared.datastructures.world.Tile;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * This abstract class represents a generic creature with various characteristics and behaviors.
 *
 * @author Jordan Geurtsen
 * @see Coordinate
 * @see BoundedValue
 */
@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@RequiredArgsConstructor
public abstract class Creature implements ISavable<Creature> {
    private final UUID id;
    private Coordinate coordinate;
    private BoundedValue health;
    private BoundedValue power;
    private BoundedValue stamina;
    private final UUID gameId;
    private Config config;
    private Chunk chunk;
    private long lastAudioPlayback;
    private boolean activeAudioPlayback;
    private Clip clip;
    private int attackStamina = 4;

    protected Creature(UUID id, Coordinate coordinate, UUID gameId, Config config,
                       BoundedValue health, BoundedValue stamina, BoundedValue power) {
        this.id = id;
        this.coordinate = coordinate;
        this.gameId = gameId;
        this.config = config;
        this.health = health;
        this.stamina = stamina;
        this.power = power;
        activeAudioPlayback = false;
        try {
            clip = AudioSystem.getClip();
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                    System.out.println("stop playing");
                    activeAudioPlayback = false;
                }
            });
        } catch (Exception e) {
            //TODO
        }
    }

    /**
     * Moves the creature up by altering its y-coordinate.
     *
     * @author Jordan Geurtsen, Jasper Kooy
     * @see Coordinate
     */
    public void moveUp() {
        if (stamina.getValue().intValue() > 0) {
            decreaseStamina(chunk.getTile(coordinate));
            setCoordinate(new Coordinate(coordinate.x(), coordinate.y() - 1, coordinate.z()));
        }
    }

    /**
     * Moves the creature down by altering its y-coordinate.
     *
     * @author Jordan Geurtsen, Jasper Kooy
     * @see Coordinate
     */
    public void moveDown() {
        if (stamina.getValue().intValue() > 0) {
            decreaseStamina(chunk.getTile(coordinate));
            setCoordinate(new Coordinate(coordinate.x(), coordinate.y() + 1, coordinate.z()));
        }
    }

    /**
     * Moves the creature left by altering its x-coordinate.
     *
     * @author Jordan Geurtsen, Jasper Kooy
     * @see Coordinate
     */
    public void moveLeft() {
        if (stamina.getValue().intValue() > 0) {
            decreaseStamina(chunk.getTile(coordinate));
            setCoordinate(new Coordinate(coordinate.x() - 1, coordinate.y(), coordinate.z()));
        }
    }

    /**
     * Moves the creature right by altering its x-coordinate.
     *
     * @author Jordan Geurtsen, Jasper Kooy
     * @see Coordinate
     */
    public void moveRight() {
        if (stamina.getValue().intValue() > 0) {
            decreaseStamina(chunk.getTile(coordinate));
            setCoordinate(new Coordinate(coordinate.x() + 1, coordinate.y(), coordinate.z()));
        }
    }

    /**
     * The creature ascends, altering its z-coordinate
     *
     * @author Lucas van Steveninck
     * @see Coordinate
     */
    public void ascend() {
        setCoordinate(new Coordinate(coordinate.x(), coordinate.y(), coordinate.z() - 1));
    }

    /**
     * The creature descends, altering its z-coordinate
     *
     * @author Lucas van Steveninck
     * @see Coordinate
     */
    public void descend() {
        setCoordinate(new Coordinate(coordinate.x(), coordinate.y(), coordinate.z() + 1));
    }

    /**
     * The creature uses a staircase, causing it to ascend or descend.
     *
     * @author Lucas van Steveninck
     * @see Coordinate
     */
    public void useStaircase() {
        if (chunk.getTile(coordinate).getType() == TileType.STAIRS_UP) ascend();
        if (chunk.getTile(coordinate).getType() == TileType.STAIRS_DOWN) descend();
    }

    /**
     * Increases the creature's health by a specified value.
     *
     * @param value the amount to increase the creature's health by.
     * @author Jordan Geurtsen
     * @see BoundedValue
     */
    public void increaseHealth(Integer value) {
        health.increaseValue(value);
    }

    /**
     * Decreases the creature's health by a specified value.
     *
     * @param value the amount to decrease the creature's health by.
     * @author Jordan Geurtsen
     * @see BoundedValue
     */
    public void decreaseHealth(Integer value) {
        health.decreaseValue(value);
        if(health.getValue().intValue()<=0){
            power.setValue(0);
            stamina.setValue(0);
        }
    }

    /**
     * Increases the creature's power by a specified value.
     *
     * @param value the amount to increase the creature's power by.
     * @author Jordan Geurtsen
     * @see BoundedValue
     */
    public void increasePower(Integer value) {
        power.increaseValue(value);
    }

    /**
     * Decreases the creature's power by a specified value.
     *
     * @param value the amount to decrease the creature's power by.
     * @author Jordan Geurtsen
     * @see BoundedValue
     */
    public void decreasePower(Integer value) {
        power.decreaseValue(value);
    }

    /**
     * Increases the creature's stamina by a specified value.
     *
     * @param value the amount to increase the creature's stamina by.
     * @author Jordan Geurtsen
     * @see BoundedValue
     */
    public void increaseStamina(Integer value) {
        stamina.increaseValue(value);
    }

    /**
     * Decreases the creature's stamina by a specified value, depending on the tile walking into.
     *
     * @param tile the tile to move into.
     * @author Jordan Geurtsen, Jasper Kooy
     * @see BoundedValue
     */
    public void decreaseStamina(Tile tile) {
        stamina.decreaseValue(tile.getMovementCost());
    }

    /**
     * Get the percentage of the creature's health.
     *
     * @return the percentage of the creature's health.
     * @author Jordan Geurtsen
     * @see BoundedValue
     */
    public double getHealthPercentage() {
        return health.convertToPercentage();
    }

    /**
     * Get the percentage of the creature's power.
     *
     * @return the percentage of the creature's power.
     * @author Jordan Geurtsen
     * @see BoundedValue
     */
    public double getPowerPercentage() {
        return power.convertToPercentage();
    }

    /**
     * Get the distance between this creature and another creature.
     * @param creature creature to get the distance to.
     * @author Adil Sadiki
     * @return the distance between this creature and another creature.
     */
    public double getDistance(Creature creature) {
        Coordinate creatureCoordinate = creature.getCoordinate();
        return coordinate.calculateDistance(creatureCoordinate);
    }

    /**
     * Returns the tile the creature is currently standing on.
     *
     * @return Tile the creature is currently standing on.
     *
     * @author Justin Slijkhuis
     */
    public Tile getTile() {
        return chunk.getTile(coordinate);
    }

    /**
     * {@inheritDoc}
     * Creates the creature_type table.
     * <br/>
     * This table has the following columns:
     * <ul>
     *     <li>creature_id</li>
     *     <li>creature_type</li>
     *     <li>entity_id</li>
     *     <li>health_id</li>
     *     <li>stamina_id</li>
     *     <li>power_id</li>
     * </ul>
     * The value_id, creature_id and value_type are the primary key of the table and is an INT, INT and VARCHAR(255).
     *
     * @param connection The connection.
     * @throws SQLException If the query fails.
     * @author Rieke Jansen
     */
    @Override
    public void tableInit(Connection connection) throws SQLException {
        String query = "CREATE TABLE IF NOT EXISTS creature (" +
                "creature_id UUID PRIMARY KEY NOT NULL," +
                "chunk_id UUID FOREIGN KEY REFERENCES chunk(chunk_id) NULL," +
                "x_coordinate int NOT NULL," +
                "y_coordinate int NOT NULL," +
                "game_id UUID FOREIGN KEY REFERENCES game(game_id) ON DELETE CASCADE NULL," +
                "health_id UUID FOREIGN KEY REFERENCES bounded_value(value_id) ON DELETE CASCADE NULL," +
                "stamina_id UUID FOREIGN KEY REFERENCES bounded_value(value_id) ON DELETE CASCADE NULL," +
                "power_id UUID FOREIGN KEY REFERENCES bounded_value(value_id) ON DELETE CASCADE NULL," +
                "config_id UUID FOREIGN KEY REFERENCES config(config_id) ON DELETE CASCADE NULL);";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.execute();
        }
    }

    /**
     * {@inheritDoc}
     * @param connection The {@link Connection} to the database.
     * @throws SQLException
     * @author Djurre Tieman
     */
    @Override
    public void insert(Connection connection) throws SQLException {
        String insertQuery = "INSERT INTO creature(creature_id,chunk_id,x_coordinate,y_coordinate,game_id,health_id,stamina_id,power_id,config_id) " +
                "VALUES(?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
            statement.setString(1, String.valueOf(getId()));
            statement.setString(2, chunk == null ? null : String.valueOf(chunk.getId()));
            statement.setInt(3, getCoordinate().x());
            statement.setInt(4, getCoordinate().y());
            statement.setString(5, String.valueOf(gameId));
            statement.setString(6, String.valueOf(getHealth().getId()));
            statement.setString(7, String.valueOf(getStamina().getId()));
            statement.setString(8, String.valueOf(getPower().getId()));
            statement.setString(9, String.valueOf(getConfig().getId()));
            statement.execute();
        }
    }

    @Override
    public void save(Connection connection) throws SQLException {
        health.save(connection);
        power.save(connection);
        stamina.save(connection);
        config.save(connection);

        if(exists(connection)){
            update(connection);
        } else {
            insert(connection);
        }
    }

    /**
     * {@inheritDoc}
     * @param connection The {@link Connection} to the database.
     * @throws SQLException
     * @author Rieke Jansen
     */
    @Override
    public void update(Connection connection) throws SQLException {
        String query = "UPDATE creature SET chunk_id=?,x_coordinate=?,y_coordinate=?,config_id=? WHERE creature_id=?;";
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setObject(1, chunk == null ? null : String.valueOf(chunk.getId()));
            statement.setInt(2, coordinate.x());
            statement.setInt(3, coordinate.y());
            statement.setString(4, String.valueOf(config.getId()));
            statement.setString(5, String.valueOf(id));
            statement.execute();
        }
    }

    /**
     * {@inheritDoc}
     * @param connection
     * @return
     * @author Rieke Jansen
     */
    @Override
    public boolean exists(Connection connection) throws SQLException {
        String sql = "SELECT 1 FROM creature WHERE creature_id=?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, String.valueOf(id));
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    @Override
    public ResultSet getLoad(Connection connection, String id) throws SQLException {
        return null;
    }

    /**
     * This method is responsible for attacking another creature.
     * @param creatureToAttack the creature to attack.
     * @author Jasper Kooy
     */
    public void attack(Creature creatureToAttack) {
        if (creatureToAttack != null) {
            if(stamina.getValue().intValue()>=attackStamina) {
                increaseStamina(-attackStamina);
                creatureToAttack.decreaseHealth(power.getIntValue() / 10);
            }
        }
    }
}