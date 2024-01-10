package nl.han.world.population.spawner;

import nl.han.shared.datastructures.Config;
import nl.han.shared.datastructures.WorldRules;
import nl.han.shared.datastructures.game.Game;
import nl.han.shared.datastructures.world.Chunk;
import nl.han.shared.datastructures.world.Coordinate;
import nl.han.shared.datastructures.world.Tile;
import nl.han.shared.datastructures.world.World;
import nl.han.shared.enums.GameMode;
import nl.han.shared.enums.TileType;
import nl.han.shared.utils.random.COCTestRandom;
import nl.han.world.generation.WorldGeneration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Testing class for {@link ItemSpawner}.
 *
 * @see <a href="https://confluenceasd.aimsites.nl/display/ASDS1G2/Testrapport+Onderzoek+Wereldpopulatie">Testrapport</a>
 */
class ItemSpawnerTest {
    private ItemSpawner sut;
    private Chunk chunk;

    @BeforeEach
    void setup() {
        Config config = new Config();
        config.setRules("""
                {
                    "seed": 328932193,
                    "worldSize": {
                        "width": 5,
                        "height": 6
                    },
                    "dungeonDepth": 7,
                    "itemSpawnRules": {
                        "min": 8,
                        "max": 9
                    },
                    "monsterSpawnRules": {
                        "min": 10,
                        "max": 11
                    }
                }
                """);
        WorldGeneration worldGeneration = new WorldGeneration(new Game(UUID.randomUUID(), "Test", GameMode.LMS, new World(config, new ArrayList<>())));
        chunk = worldGeneration.generateChunk(new Coordinate(0, 0));
        Game game = mock();
        World world = mock();
        when(world.getConfig()).thenReturn(config);
        when(game.getWorld()).thenReturn(world);
        sut = new ItemSpawner(game.getWorld().getConfig());
        COCTestRandom random = mock(COCTestRandom.class);
        when(random.nextInt()).thenReturn(0);
        when(random.nextInt(anyInt())).thenReturn(0);
        when(random.nextInt(anyInt(), anyInt())).thenReturn(0);
    }

    /**
     * Test code TODO: Code invullen
     *
     * @author Fabian van Os & Sven van Hoof
     */
    @Test
    @DisplayName("test that getValidTiles only returns valid tiles")
    void getValidTiles() {
        // Arrange
        Coordinate coordinate = new Coordinate(0, 0);
        List<Tile> tiles = List.of(new Tile(TileType.MOUNTAIN, coordinate), new Tile(TileType.WATER, coordinate), new Tile(TileType.GRASS, coordinate), new Tile(TileType.DUNGEON_FLOOR, coordinate), new Tile(TileType.STAIRS_UP, coordinate), new Tile(TileType.DUNGEON_WALL, coordinate), new Tile(TileType.SAVANNAH, coordinate));

        List<Tile> expected = getMultiple(tiles, 2, 3, 6);

        // Act
        List<Tile> actual = sut.getValidTiles(tiles);

        // Assert
        assertIterableEquals(expected, actual);
    }

    /**
     * Test code TODO testcode
     */
    @Test
    @DisplayName("test that items spawn on all PassableTiles in a chunk")
    void testMinAndMaxForChunkExecute() {
        // Arrange
        WorldRules.ItemSpawnRules spawnRate = sut.getSpawnRate();

        // Act
        sut.execute(chunk);
        int actual = Stream.of(chunk.getTiles()).flatMap(Stream::of).filter(tile -> !tile.getItems().isEmpty()).toList().size();

        // Assert
        assertTrue(actual <= spawnRate.getMax() && actual >= spawnRate.getMin());
    }

    /**
     * Test code TODO testcode
     */
    @Test
    @DisplayName("test that makes sure items spawn within a specified duration")
    void itemsSpawningTimeTest() {
        // Arrange
        long expectedDurationMillis = 250;

        // Act
        Instant startTime = Instant.now();
        sut.execute(chunk);
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        long durationMillis = duration.toMillis();

        // Assert
        assertTrue(durationMillis < expectedDurationMillis, "The itemSpawner took longer than " + expectedDurationMillis + " milliseconds");
    }

    public <T> List<T> getMultiple(List<T> list, int... indexes) {
        List<T> newList = new ArrayList<>();

        for (int i : indexes) {
            newList.add(list.get(i));
        }

        return newList;
    }
}
