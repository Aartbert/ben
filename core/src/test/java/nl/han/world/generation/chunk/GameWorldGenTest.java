package nl.han.world.generation.chunk;

import nl.han.shared.datastructures.game.Game;
import nl.han.shared.datastructures.world.Chunk;
import nl.han.shared.datastructures.world.Coordinate;
import nl.han.shared.datastructures.world.Tile;
import nl.han.shared.datastructures.world.World;
import nl.han.world.generation.generator.GameWorldGen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import static nl.han.CryptsOfChaos.seededRandom;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This class tests the GameWorldGen class.
 */
class GameWorldGenTest {

    GameWorldGen sut;
    private static final Random random = new Random();
    Game mockedGame;
    World mockedWorld;


    /**
     * This method is executed before each test and setups the gameWorldGen, seed, chunk, chunk1, chunk2, random.
     */
    @BeforeEach
    void setUp() {
        mockedGame = mock(Game.class);
        mockedWorld = mock(World.class);
        when(mockedGame.getWorld()).thenReturn(mockedWorld);
        when(mockedWorld.getSeed()).thenReturn(0L);
        sut = new GameWorldGen(mockedGame);
    }

    /**
     * This method tests if the seed always generates the same world.
     */
    @Test
    @DisplayName("test if seed always generates the same world")
    void testGeneratePerlinNoise() {
        //arrange
        int seed = 123456789;
        Chunk chunk1 = new Chunk(new Tile[24][80], new Coordinate(0, 0, 0), 0L, UUID.randomUUID());
        Chunk chunk2 = new Chunk(new Tile[24][80], new Coordinate(0, 0, 0), 0L, UUID.randomUUID());

        //act
        seededRandom.setSeed(seed);
        sut.softGameWorldGen(chunk1);
        sut.gameWorldGen(chunk1);
        seededRandom.setSeed(seed);
        sut.softGameWorldGen(chunk2);
        sut.gameWorldGen(chunk2);

        //assert
        assertEquals(chunk1.toString(), chunk2.toString());
    }

    /**
     * This method tests if the chunks are not the same.
     */
    @Test
    @DisplayName("test if chunks are not the same")
    void testIfChunksAreNotTheSame() {
        //arrange
        int seed = 123456789;
        Chunk chunk1 = new Chunk(new Tile[24][80], new Coordinate(0, 0, 0), 0L, UUID.randomUUID());
        Chunk chunk2 = new Chunk(new Tile[24][80], new Coordinate(0, 0, 0), 1L, UUID.randomUUID());
        //act
        seededRandom.setSeed(seed);
        sut.softGameWorldGen(chunk1);
        sut.gameWorldGen(chunk1);
        seededRandom.setSeed(seed);
        sut.softGameWorldGen(chunk2);
        sut.gameWorldGen(chunk2);

        //assert
        assertNotEquals(chunk1.getTiles(), chunk2.getTiles());
    }

    /**
     * This method tests if two different seeds result in different chunks.
     */
    @Test
    @DisplayName("test if two different seeds result in different chunks")
    void testTwoSeedsDiffer() {
        // Arrange
        int seed = 123456789;
        int seed1 = 987654321;
        Chunk chunk1 = new Chunk(new Tile[24][80], new Coordinate(0, 0, 0), seed, UUID.randomUUID());
        Chunk chunk2 = new Chunk(new Tile[24][80], new Coordinate(0, 0, 0), seed1, UUID.randomUUID());
        // Act
        seededRandom.setSeed(seed);
        sut.softGameWorldGen(chunk1);
        sut.gameWorldGen(chunk1);
        seededRandom.setSeed(seed);
        sut.softGameWorldGen(chunk2);
        sut.gameWorldGen(chunk2);

        // Assert
        assertNotEquals((Arrays.deepToString(chunk1.getTiles())), Arrays.deepToString(chunk2.getTiles()));
    }

    /**
     * This method tests if the seed always generates the same world.
     */
    @RepeatedTest(5)
    @DisplayName("test if genPerlinNoise returns a value between 0 and 255")
    void test() {
        //arrange
        int min = 0;
        int max = 255;
        int seed = random.nextInt(1000000000);
        //act
        int actual = sut.generatePerlinNoise(seed, new Coordinate(0, 0), 1);
        //assert
        assertTrue(actual >= min && actual <= max);
    }
}