package nl.han.world.population.spawner;

import nl.han.shared.datastructures.BoundedValue;
import nl.han.shared.datastructures.Config;
import nl.han.shared.datastructures.creature.Player;
import nl.han.shared.datastructures.world.Chunk;
import nl.han.shared.datastructures.world.Coordinate;

import nl.han.shared.datastructures.world.Tile;
import nl.han.shared.utils.random.COCTestRandom;
import nl.han.world.population.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static nl.han.shared.datastructures.world.Chunk.CHUNK_HEIGHT;
import static nl.han.shared.datastructures.world.Chunk.CHUNK_WIDTH;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing class for {@link PlayerSpawner}.
 * The PlayerSpawnerTest class contains unit-tests for the PlayerSpawner class.
 *
 * @see <a href="https://confluenceasd.aimsites.nl/display/ASDS1G2/Testplan+Onderzoek+Wereldpopulatie">Testrapport</a>
 * @author Brett Hammer
 */
class PlayerSpawnerTest {
    private static final TestUtil TEST_UTIL = new TestUtil();
    private PlayerSpawner sut;
    private final COCTestRandom random = new COCTestRandom();
    static final UUID UUID = java.util.UUID.randomUUID();
    private static final Chunk CHUNK = new Chunk(new Tile[24][80], new Coordinate(0, 0, 0), 0L, UUID);

    /**
     * Sets up the test environment before each
     * test case by initializing required objects and configurations.
     *
     * @author Brett Hammer
     */
    @BeforeEach
    void setup() {
        sut = new PlayerSpawner();

        sut.setRandom(random);
        int[] integers = new int[1000];
        Arrays.fill(integers, 0);
        random.setIntegers(integers);

        sut.setStartCoordinate(0);
    }

    /**
     * Test code WPT27.
     * Tests whether a player is in range of the target coordinate within a specified range.
     *
     * @author Brett Hammer
     */
    @Test
    @DisplayName("test if the player is in range of the target coordinate")
    void testPlayerInRange() {
        // Arrange
        Coordinate playerCoords = new Coordinate(21, 21);
        Player player = new Player(UUID.randomUUID(), "Test1", playerCoords, UUID.randomUUID(), new Config(), new BoundedValue(100, 100, 0), new BoundedValue(100, 100, 0), new BoundedValue(100, 100, 0), "IP");
        player.setChunk(CHUNK);

        List<Player> players = new ArrayList<>();
        players.add(player);

        sut.setPlayersToSpawn(players);

        Coordinate target = new Coordinate(19, 19);

        // Act
        boolean actual = sut.playerInRange(target, 5);

        // Assert
        assertTrue(actual);
    }

    /**
     * Test code WPT28.
     * Tests the generation of a random coordinate that is not present in a given list.
     *
     * @author Brett Hammer
     */
    @Test
    @DisplayName("test if random coordinate is chosen that is not in given list")
    void testGetRandomCoordinatesNotIn() {
        // Arrange
        List<Coordinate> all = new ArrayList<>();
        all.add(new Coordinate(0,0));
        all.add(new Coordinate(0,1));
        all.add(new Coordinate(1,0));
        all.add(new Coordinate(1,1));

        List<Coordinate> notIn = new ArrayList<>();
        notIn.add(new Coordinate(0,0));
        notIn.add(new Coordinate(0,1));

        Coordinate expected = new Coordinate(1,0);

        // Act
        Coordinate actual = sut.getRandomCoordinatesNotIn(all, notIn);

        // Assert
        assertEquals(expected, actual);
    }

    /**
     * Test code WPT29.
     * Tests the creation of a list of coordinates representing the entire chunk.
     *
     * @author Brett Hammer
     */
    @Test
    @DisplayName("test if coordinates of list from a chunk are created")
    void testCreateCoordinateListOfChunk() {
        // Arrange
        char[][] chunkGrid = new char[24][80];
        for (char[] chars : chunkGrid) {
            Arrays.fill(chars, '.');
        }
        Chunk chunk = TEST_UTIL.createChunkFromCharArray(chunkGrid, UUID);

        List<Coordinate> expected = new ArrayList<>();
        for (int y = 0; y < CHUNK_HEIGHT; y++) {
            for (int x = 0; x < CHUNK_WIDTH; x++) {
                expected.add(new Coordinate(x, y));
            }
        }

        // Act
        List<Coordinate> actual = sut.createCoordinateListOfChunk(chunk);

        // Assert
        assertEquals(expected, actual);
    }

    /**
     * Test code WPT30.
     * Tests the extraction of a list from another list.
     *
     * @author Brett Hammer
     */
    @Test
    @DisplayName("test if the extract list is extracted of all")
    void testExtractList() {
        // Arrange
        List<Coordinate> all = new ArrayList<>();
        all.add(new Coordinate(0,0));
        all.add(new Coordinate(0,1));
        all.add(new Coordinate(1,0));
        all.add(new Coordinate(1,1));

        List<Coordinate> extract = new ArrayList<>();
        extract.add(new Coordinate(0,0));
        extract.add(new Coordinate(1,1));

        List<Coordinate> expected = new ArrayList<>();
        expected.add(new Coordinate(0, 1));
        expected.add(new Coordinate(1, 0));

        // Act
        List<Coordinate> actual = sut.extractList(all, extract);

        // Assert
        assertEquals(expected, actual);
    }

    /**
     * Test code WPT31.
     * Tests the spawning of a player at a specified coordinate within a given chunk.
     *
     * @author Brett Hammer
     */
    @Test
    @DisplayName("test if player is set with coords and chunk")
    void testSpawnPlayer() {
        // Arrange
        Player player = new Player(UUID.randomUUID(), "Test1", new Coordinate(-1, -1), UUID.randomUUID(), new Config(), new BoundedValue(100, 100, 0), new BoundedValue(100, 100, 0), new BoundedValue(100, 100, 0), "IP");
        player.setChunk(null);

        Coordinate expectedCoords = new Coordinate(20, 20);

        // Act
        sut.spawnPlayer(player, expectedCoords, CHUNK);

        // Assert
        assertEquals(expectedCoords, player.getCoordinate());
        assertEquals(CHUNK, player.getChunk());
    }
}
