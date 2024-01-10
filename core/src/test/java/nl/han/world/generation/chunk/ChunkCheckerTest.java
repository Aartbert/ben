package nl.han.world.generation.chunk;

import nl.han.shared.datastructures.world.Chunk;
import nl.han.shared.datastructures.world.Coordinate;
import nl.han.shared.datastructures.world.Tile;
import nl.han.shared.enums.TileType;
import nl.han.world.generation.post_processing.ChunkChecker;
import nl.han.world.generation.post_processing.Cluster;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static nl.han.shared.datastructures.world.Chunk.CHUNK_HEIGHT;
import static nl.han.shared.datastructures.world.Chunk.CHUNK_WIDTH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This class tests the ChunkChecker class.
 *
 * @author Julian van Kuijk
 * @see <a href="https://confluenceasd.aimsites.nl/x/PALjGQ">Testrapport</a>
 */
class ChunkCheckerTest {
    Chunk chunk;
    private final TileType impassable = TileType.WATER;
    private ChunkChecker sut;

    /**
     * Test code WGT1.1
     */
    @Test
    @DisplayName("test if the visited map gets filled correctly")
    void testVisitedMap() {
        Tile[][] tiles = generateChunk(impassable);
        tiles[1][1] = new Tile(TileType.GRASS, new Coordinate(1, 1));
        tiles[2][1] = new Tile(TileType.GRASS, new Coordinate(1, 2));
        tiles[3][1] = new Tile(TileType.GRASS, new Coordinate(1, 3));
        tiles[4][1] = new Tile(TileType.GRASS, new Coordinate(1, 4));

        chunk = new Chunk(tiles, new Coordinate(0, 0, 0), 0L, UUID.randomUUID());
        sut = new ChunkChecker();

        // Act
        sut.checkChunk(chunk);

        // Assert
        assertEquals(4, sut.getVisited().size());
        assertTrue(sut.getVisited().containsValue(true));
    }

    /**
     * Test code WGT1.2
     */
    @Test
    @DisplayName("test if the cluster is filled correctly")
    void testClusterCollection() {
        Tile[][] tiles = generateChunk(impassable);
        tiles[1][1] = new Tile(TileType.GRASS, new Coordinate(1, 1));
        tiles[2][1] = new Tile(TileType.GRASS, new Coordinate(1, 2));
        tiles[3][1] = new Tile(TileType.GRASS, new Coordinate(1, 3));
        tiles[4][1] = new Tile(TileType.GRASS, new Coordinate(1, 4));

        chunk = new Chunk(tiles, new Coordinate(0, 0, 0), 0L, UUID.randomUUID());
        sut = new ChunkChecker();

        // Act
        sut.checkChunk(chunk);

        Cluster cluster = sut.getClusterList().get(0);
        // Assert
        assertEquals(4, cluster.getCoordinates().size());
    }

    /**
     * Test code WGT1.3
     */
    @Test
    @DisplayName("test if multiple clusters are found")
    void testClusterListCollection() {
        Tile[][] tiles = generateChunk(impassable);
        tiles[1][1] = new Tile(TileType.GRASS, new Coordinate(1, 1));
        tiles[2][1] = new Tile(TileType.GRASS, new Coordinate(1, 2));
        tiles[3][1] = new Tile(TileType.GRASS, new Coordinate(1, 3));
        tiles[4][1] = new Tile(TileType.GRASS, new Coordinate(1, 4));

        tiles[10][1] = new Tile(TileType.GRASS, new Coordinate(1, 10));
        tiles[10][2] = new Tile(TileType.GRASS, new Coordinate(2, 10));
        tiles[10][3] = new Tile(TileType.GRASS, new Coordinate(3, 10));
        tiles[10][4] = new Tile(TileType.GRASS, new Coordinate(4, 10));
        tiles[10][5] = new Tile(TileType.GRASS, new Coordinate(5, 10));

        chunk = new Chunk(tiles, new Coordinate(0, 0, 0), 0L, UUID.randomUUID());
        sut = new ChunkChecker();

        // Act
        sut.checkChunk(chunk);

        // Assert
        assertEquals(2, sut.getClusterList().size());
    }

    /**
     * Test code WGT1.4
     */
    @Test
    @DisplayName("test if a cluster is filled correctly")
    void testClusterFill() {
        Tile[][] tiles = generateChunk(impassable);
        tiles[0][1] = new Tile(TileType.GRASS, new Coordinate(1, 0));
        tiles[1][1] = new Tile(TileType.GRASS, new Coordinate(1, 1));
        tiles[2][1] = new Tile(TileType.GRASS, new Coordinate(1, 2));
        tiles[3][1] = new Tile(TileType.GRASS, new Coordinate(1, 3));
        tiles[4][1] = new Tile(TileType.GRASS, new Coordinate(1, 4));

        tiles[10][1] = new Tile(TileType.GRASS, new Coordinate(1, 10));
        tiles[10][2] = new Tile(TileType.GRASS, new Coordinate(2, 10));
        tiles[10][3] = new Tile(TileType.GRASS, new Coordinate(3, 10));
        tiles[10][4] = new Tile(TileType.GRASS, new Coordinate(4, 10));
        tiles[10][5] = new Tile(TileType.GRASS, new Coordinate(5, 10));

        chunk = new Chunk(tiles, new Coordinate(0, 0, 0), 0L, UUID.randomUUID());
        sut = new ChunkChecker();

        // Act
        sut.checkChunk(chunk);

        // Assert
        assertEquals(1, sut.getClustersFilled().size());


        ChunkChecker sut2 = new ChunkChecker();
        // Act 2
        sut2.checkChunk(chunk);
        // Assert 2
        assertEquals(1, sut2.getClusterList().size());
    }

    /**
     * Test code WGT1.5
     */
    @Test
    @DisplayName("test if a cluster of 3x3 gets filled")
    void testReplacingFullySurroundedPassableTiles() {
        Tile[][] tiles = generateChunk(impassable);
        tiles[1][1] = new Tile(TileType.GRASS, new Coordinate(1, 1));
        tiles[1][2] = new Tile(TileType.GRASS, new Coordinate(2, 1));
        tiles[1][3] = new Tile(TileType.GRASS, new Coordinate(3, 1));
        tiles[2][1] = new Tile(TileType.GRASS, new Coordinate(1, 2));
        tiles[2][2] = new Tile(TileType.GRASS, new Coordinate(2, 2));
        tiles[2][3] = new Tile(TileType.GRASS, new Coordinate(3, 2));
        tiles[3][1] = new Tile(TileType.GRASS, new Coordinate(1, 3));
        tiles[3][2] = new Tile(TileType.GRASS, new Coordinate(2, 3));
        tiles[3][3] = new Tile(TileType.GRASS, new Coordinate(3, 3));

        chunk = new Chunk(tiles, new Coordinate(0, 0, 0), 0L, UUID.randomUUID());
        sut = new ChunkChecker();

        // Act
        sut.checkChunk(chunk);

        // Assert
        assertEquals(1, sut.getClustersFilled().size());
    }

    /**
     * Test code WGT1.6
     */
    @Test
    @DisplayName("test if a cluster with a unique shape gets filled")
    void testReplacingFullySurroundedPassableTilesUniqueShape() {
        Tile[][] tiles = generateChunk(impassable);
        tiles[1][1] = new Tile(TileType.GRASS, new Coordinate(1, 1));
        tiles[1][2] = new Tile(TileType.GRASS, new Coordinate(2, 1));
        tiles[1][3] = new Tile(TileType.GRASS, new Coordinate(3, 1));
        tiles[1][4] = new Tile(TileType.GRASS, new Coordinate(4, 1));
        tiles[2][1] = new Tile(TileType.GRASS, new Coordinate(1, 2));
        tiles[2][2] = new Tile(TileType.GRASS, new Coordinate(2, 2));
        tiles[2][3] = new Tile(TileType.GRASS, new Coordinate(3, 2));
        tiles[2][4] = new Tile(TileType.GRASS, new Coordinate(4, 2));
        tiles[3][1] = new Tile(TileType.GRASS, new Coordinate(1, 3));
        tiles[3][2] = new Tile(TileType.GRASS, new Coordinate(2, 3));
        tiles[3][3] = new Tile(TileType.GRASS, new Coordinate(3, 3));
        tiles[3][4] = new Tile(TileType.GRASS, new Coordinate(4, 3));
        tiles[4][1] = new Tile(TileType.GRASS, new Coordinate(1, 4));
        tiles[4][2] = new Tile(TileType.GRASS, new Coordinate(2, 4));
        tiles[4][3] = new Tile(TileType.GRASS, new Coordinate(3, 4));
        tiles[4][4] = new Tile(TileType.GRASS, new Coordinate(4, 4));

        tiles[4][5] = new Tile(TileType.GRASS, new Coordinate(5, 4));
        tiles[4][6] = new Tile(TileType.GRASS, new Coordinate(6, 4));
        tiles[4][7] = new Tile(TileType.GRASS, new Coordinate(7, 4));
        tiles[3][7] = new Tile(TileType.GRASS, new Coordinate(7, 3));

        chunk = new Chunk(tiles, new Coordinate(0, 0, 0), 0L, UUID.randomUUID());
        sut = new ChunkChecker();

        // Act
        sut.checkChunk(chunk);

        // Assert
        assertEquals(1, sut.getClustersFilled().size());
    }

    /**
     * Generate a tileSet for testing purposes. All the tiles are impassable, in your test you can alter the tiles that you want passable.
     *
     * @param tileType the type of tile that will be used to fill the chunk.
     * @return a filled chunk which contains only tileType
     */
    private Tile[][] generateChunk(TileType tileType) {
        Tile[][] tiles = new Tile[CHUNK_HEIGHT][CHUNK_WIDTH];
        for (int y = 0; y < tiles.length; y++) {
            for (int x = 0; x < tiles[0].length; x++) {
                tiles[y][x] = new Tile(tileType, new Coordinate(x, y));
            }
        }
        return tiles;
    }
}