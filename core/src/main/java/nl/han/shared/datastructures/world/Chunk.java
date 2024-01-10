package nl.han.shared.datastructures.world;

import lombok.*;
import nl.han.ISavable;
import nl.han.shared.enums.BiomeType;
import nl.han.shared.enums.TileType;
import nl.han.shared.utils.random.COCRandom;
import nl.han.shared.utils.random.ICOCRandom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Represents a class that describes a Chunk which consists of tiles, BiomeType and possible Structure.
 *
 * @author Jordan Geurtsen
 * @see Tile
 * @see BiomeType
 * @see Structure
 */
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Chunk implements ISavable<Chunk> {
    private final ICOCRandom random;
    private final UUID id;
    private final UUID gameId;
    private final Coordinate coordinate;
    private final Tile[][] tiles;
    private BiomeType biomeType;
    private List<Structure> structures = new ArrayList<>();
    private int upConnectionX;
    private int downConnectionX;
    private int leftConnectionY;
    private int rightConnectionY;
    private boolean fullyGenerated = false;
    public static final int CHUNK_WIDTH = 80;
    public static final int CHUNK_HEIGHT = 24;

    /**
     * Constructor for the Chunk class. When constructing a chunk the random object that should be used for
     * every generation step is created. Because of this chunks in the same world at the same coordinate will
     * always be generated in the same way.
     *
     * @param tiles           The tiles that this chunk contains.
     * @param chunkCoordinate The coordinates of this chunk.
     * @param worldSeed       The seed of the world that this chunk is contained in.
     * @param gameId          The UUID of the game that this chunk is contained in.
     * @author Lucas van Steveninck
     */
    public Chunk(Tile[][] tiles, Coordinate chunkCoordinate, long worldSeed, UUID gameId) {
        id = UUID.randomUUID();
        this.gameId = gameId;
        random = new COCRandom(worldSeed ^ chunkCoordinate.x() ^ chunkCoordinate.y() ^ chunkCoordinate.z());
        this.tiles = tiles;
        this.coordinate = chunkCoordinate;
    }

    public Chunk(Chunk original) {
        random = original.random;
        tiles = new Tile[CHUNK_HEIGHT][CHUNK_WIDTH];
        for (int y = 0; y < CHUNK_HEIGHT; y++) {
            tiles[y] = Arrays.copyOf(original.getTiles()[y], CHUNK_WIDTH);
        }
        biomeType = original.biomeType;
        structures = original.structures;
        coordinate = original.coordinate;
        fullyGenerated = original.fullyGenerated;
        id = original.id;
        gameId = original.gameId;
    }

    /**
     * {@inheritDoc}
     * Creates the chunk table.
     * <br/>
     * This table has the following columns:
     * <ul>
     *     <li>chunk_id</li>
     *     <li>x_coordinate</li>
     *     <li>y_coordinate</li>
     * </ul>
     * <br/>
     * The chunk_id is the primary key of the table and is an INT.
     *
     * @param connection The connection.
     * @throws SQLException If the query fails.
     * @author Jordan Geurtsen, Rieke Jansen
     */
    @Override
    public void tableInit(Connection connection) throws SQLException {
        String creationString = "CREATE TABLE IF NOT EXISTS chunk (" +
                "chunk_id UUID PRIMARY KEY NOT NULL," +
                "game_id UUID FOREIGN KEY REFERENCES game(game_id) ON DELETE CASCADE NULL," +
                "x_coordinate int NULL," +
                "y_coordinate int NULL," +
                "z_coordinate int NULL);";
        try (PreparedStatement statement = connection.prepareStatement(creationString)) {
            statement.execute();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws SQLException If the query fails.
     * @author Rieke Jansen
     */
    @Override
    public void insert(Connection connection) throws SQLException {
        String query = "INSERT INTO chunk(chunk_id,game_id,x_coordinate,y_coordinate,z_coordinate) VALUES(?,?,?,?,?);";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, id);
            statement.setObject(2, gameId);
            statement.setInt(3, coordinate.x());
            statement.setInt(4, coordinate.y());
            statement.setInt(5, coordinate.z());
            statement.execute();
        }
    }

    /**
     * checks if the chunk exists in the database.
     *
     * @param connection The {@link Connection} to the database.
     * @return true if the config exists.
     * @throws SQLException if the query fails.
     * @author Rieke Jansen
     */
    @Override
    public boolean exists(Connection connection) throws SQLException {
        String sql = "SELECT 1 FROM chunk WHERE chunk_id=?";

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
     * @author Rieke Jansen
     */
    @Override
    public void update(Connection connection) throws SQLException {
        String query = "UPDATE chunk SET x_coordinate=?,y_coordinate=?,z_coordinate=? WHERE chunk_id=?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, coordinate.x());
            statement.setInt(2, coordinate.y());
            statement.setInt(3, coordinate.z());
            statement.setString(4, String.valueOf(id));
            statement.execute();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws SQLException If the query fails.
     * @author Rieke Jansen
     */
    @Override
    public ResultSet getLoad(Connection connection, String id) throws SQLException {
        String query = "SELECT chunk_id,x_coordinate,y_coordinate,z_coordinate FROM chunk WHERE chunk_id=?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, id);
            return statement.executeQuery();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param resultSet  The {@link ResultSet} to map to an object.
     * @param connection The {@link Connection} to the database.
     * @return List of chunks.
     * @author Rieke Jansen
     */
    @Override
    public ArrayList<Chunk> mapList(ResultSet resultSet, Connection connection) {
        return new ArrayList<>();
    }

    /**
     * Retrieves a {@link Tile} based on a given coordinate.
     * <br/>
     *
     * @param coordinate The coordinate to compare with the coordinates of all tiles. This is not NULL.
     * @return {@link Tile} object if a matching coordinate was found, NULL otherwise.
     * @author Jordan Geurtsen & Djurre Tieman
     * @see Tile
     */
    public Tile getTile(Coordinate coordinate) {
        return tiles[coordinate.y()][coordinate.x()];
    }

    /**
     * Sets a specific {@link Tile} at the given coordinate in the object's {@link Tile} collection.
     * <br/>
     * This method finds the index of the tile currently at the provided coordinate,
     * and replaces that {@link Tile} with the provided one.
     *
     * @param tile the new {@link Tile} object that's going to replace the existing tile
     * @author Jordan Geurtsen & Djurre Tieman
     * @see Tile
     * @see Coordinate
     */
    public void setTile(Tile tile) {
        Coordinate tileCoordinate = tile.getCoordinate();
        tiles[tileCoordinate.y()][tileCoordinate.x()] = tile;
    }

    /**
     * Adds a structure to this chunk.
     *
     * @param structure The structure that should be added to the chunk.
     * @author Lucas van Steveninck
     */
    public void addStructure(Structure structure) {
        this.structures.add(structure);
    }

    /**
     * Gets the closest item to the given coordinate.
     *
     * @param coordinate The coordinate to compare with the coordinates of all items.
     * @return Item The item closest to the given coordinate.
     * @author Justin Slijkhuis
     */
    public Tile getClosestTileWithItem(Coordinate coordinate) {
        Tile closestTileWithItem = null;
        double closestDistance = Double.MAX_VALUE;
        for (Tile[] tileRow : tiles) {
            for (Tile tile : tileRow) {
                if (tile.hasItems()) {
                    Coordinate tileCoordinate = tile.getCoordinate();
                    double distance = coordinate.calculateDistance(tileCoordinate);
                    if (distance < closestDistance) {
                        closestTileWithItem = tile;
                        closestDistance = distance;
                    }
                }
            }
        }
        return closestTileWithItem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Tile[] tileRow : tiles) {
            for (Tile tile : tileRow) {
                sb.append(tile.getCharacter());
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Method to convert a {@link Chunk chunk} to a JSON string
     *
     * @return The JSON string
     */
    public String toJson() {
        return "{" +
                "\"id\":\"" + getId() + "\"," +
                "\"coordinate\":" + coordinate.toJson() +
                "}";
    }

    /**
     * UpdateTile updates the tile on a given coordinate with a given TileType.
     *
     * @param coord   The coordinate of the Tile to be updated
     * @param newType The Type to which the tile should be updated.
     * @author Julian van Kuijk
     */
    public void updateTile(Coordinate coord, TileType newType) {
        getTile(coord).setType(newType);
    }

    /**
     * Checks if the given chunk has the same coordinate as this chunk.
     *
     * @param chunk The chunk to compare with this chunk.
     * @return True if the chunks have the same coordinate, false otherwise.
     */
    public boolean isSameChunk(Chunk chunk) {
        Coordinate otherChunkCoordinate = chunk.getCoordinate();
        Coordinate thisChunkCoordinate = this.getCoordinate();
        return otherChunkCoordinate.x() == thisChunkCoordinate.x()
                && otherChunkCoordinate.y() == thisChunkCoordinate.y()
                && otherChunkCoordinate.z() == thisChunkCoordinate.z();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Chunk chunk)) return false;
        return Arrays.deepEquals(tiles, chunk.tiles) && biomeType == chunk.biomeType && Objects.equals(structures, chunk.structures) && Objects.equals(coordinate, chunk.coordinate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = Objects.hash(biomeType, structures, coordinate);
        result = 31 * result + Arrays.deepHashCode(tiles);
        return result;
    }

    /**
     * Returns a random passable tile from this chunk.
     *
     * @return A random passable tile from this chunk.
     */
    public Tile getRandomPassableTile() {
        Tile tile;
        do {
            tile = tiles[random.nextInt(CHUNK_HEIGHT)][random.nextInt(CHUNK_WIDTH)];
        } while (!tile.isPassable());
        return tile;
    }
}
