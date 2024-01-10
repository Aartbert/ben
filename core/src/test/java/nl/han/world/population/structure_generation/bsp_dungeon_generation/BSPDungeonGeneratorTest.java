package nl.han.world.population.structure_generation.bsp_dungeon_generation;

import nl.han.shared.datastructures.Config;
import nl.han.shared.datastructures.game.Game;
import nl.han.shared.datastructures.world.*;
import nl.han.shared.enums.StructureType;
import nl.han.shared.enums.TileType;
import nl.han.shared.utils.random.COCTestRandom;
import nl.han.world.population.TestUtil;
import nl.han.world.population.structure_generation.bsp_dungeon_generation.binary_space_partitioning.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static nl.han.shared.datastructures.world.Chunk.CHUNK_HEIGHT;
import static nl.han.shared.datastructures.world.Chunk.CHUNK_WIDTH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.mockito.Mockito.*;

/**
 * Testing class for {@link BSPDungeonGenerator}.
 * @see <a href="https://confluenceasd.aimsites.nl/display/ASDS1G2/Testrapport+Onderzoek+Wereldpopulatie">Testrapport</a>
 * @author Lucas van Steveninck
 */
class BSPDungeonGeneratorTest {
    BSPDungeonGenerator sut;
    COCTestRandom randomMock;
    TestUtil testUtil = new TestUtil();
    Game mockedGame;
    World mockedWorld;
    Chunk initialCurrentChunkSpy;
    Chunk above = new Chunk(new Tile[CHUNK_HEIGHT][CHUNK_WIDTH], new Coordinate(0, 0, 0), WORLD_SEED, UUID);
    Chunk left = new Chunk(new Tile[CHUNK_HEIGHT][CHUNK_WIDTH], new Coordinate(0, 0, 0), WORLD_SEED, UUID);
    Chunk right = new Chunk(new Tile[CHUNK_HEIGHT][CHUNK_WIDTH], new Coordinate(0, 0, 0), WORLD_SEED, UUID);
    Chunk up = new Chunk(new Tile[CHUNK_HEIGHT][CHUNK_WIDTH], new Coordinate(0, 0, 0), WORLD_SEED, UUID);
    Chunk down = new Chunk(new Tile[CHUNK_HEIGHT][CHUNK_WIDTH], new Coordinate(0, 0, 0), WORLD_SEED, UUID);
    static final UUID UUID = java.util.UUID.randomUUID();
    static final long WORLD_SEED = 1L;
    static final int[] DUNGEON_ROOM_MEASUREMENTS = new int[] {
            11, 2, 21, 6,
            49, 2, 29, 6,
            20, 14, 25, 7,
            56, 16, 22, 6};
    static final int[] CORRIDOR_CONNECTION_POINTS = new int[] {
            115, 10,
            59, 112,
            104, 34};
    static final Config config = new Config(java.util.UUID.randomUUID(), """
            {
                "seed": 328932193,
                "worldSize": {
                    "width": -1,
                    "height": -1
                },
                "dungeonDepth": -1,
                "itemSpawnRules": {
                    "min": 8,
                    "max": 9
                },
                "monsterSpawnRules": {
                    "min": 10,
                    "max": 11
                }
            }
            """, "worldConfig");
    final Room parentNodeRoom = testUtil.createRoomSpy(0, 0, 80, 10);
    final ParentNode<Room, Room> parentNode = new ParentNode<>(
            parentNodeRoom,
            new LeafNode<>(testUtil.createRoomSpy(0, 0, 36, 10)),
            new LeafNode<>(testUtil.createRoomSpy(36, 0, 44, 10))
    );
    final ParentNode<Room, Room> root = new ParentNode<>(
            testUtil.createRoomSpy(0, 0, 80, 24),
            parentNode,
            new ParentNode<>(
                    testUtil.createRoomSpy(0, 10, 80, 14),
                    new LeafNode<>(testUtil.createRoomSpy(0, 10, 49, 14)),
                    new LeafNode<>(testUtil.createRoomSpy(49, 10, 31, 14))
            )
    );
    final Tree<Room, Room> tree = new Tree<>(root);
    final Chunk expectedEmptyChunk = testUtil.createChunkFromCharArray(new char[][] {
            "################################################################################".toCharArray(),
            "################################################################################".toCharArray(),
            "################################################################################".toCharArray(),
            "################################################################################".toCharArray(),
            "################################################################################".toCharArray(),
            "################################################################################".toCharArray(),
            "################################################################################".toCharArray(),
            "################################################################################".toCharArray(),
            "################################################################################".toCharArray(),
            "################################################################################".toCharArray(),
            "################################################################################".toCharArray(),
            "################################################################################".toCharArray(),
            "################################################################################".toCharArray(),
            "################################################################################".toCharArray(),
            "################################################################################".toCharArray(),
            "################################################################################".toCharArray(),
            "################################################################################".toCharArray(),
            "################################################################################".toCharArray(),
            "################################################################################".toCharArray(),
            "################################################################################".toCharArray(),
            "################################################################################".toCharArray(),
            "################################################################################".toCharArray(),
            "################################################################################".toCharArray(),
            "################################################################################".toCharArray()
    }, UUID);
    final Chunk expectedRoomChunk = testUtil.createChunkFromCharArray(new char[][] {
            "################################################################################".toCharArray(),
            "################################################################################".toCharArray(),
            "###########°°°°°°°°°°°°°°°°°°°°°#################°°°°°°°°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
            "###########°°°°°°°°°°°°°°°°°°°°°#################°°°°°°°°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
            "###########°°°°°°°°°°°°°°°°°°°°°#################°°°°°°°°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
            "###########°°°°°°°°°°°°°°°°°°°°°#################°°°°°°°°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
            "###########°°°°°°°°°°°°°°°°°°°°°#################°°°°°°°°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
            "###########°°°°°°°°°°°°°°°°°°°°°#################°°°°°°°°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
            "################################################################################".toCharArray(),
            "################################################################################".toCharArray(),
            "################################################################################".toCharArray(),
            "################################################################################".toCharArray(),
            "################################################################################".toCharArray(),
            "################################################################################".toCharArray(),
            "####################°°°°°°°°°°°°°°°°°°°°°°°°°###################################".toCharArray(),
            "####################°°°°°°°°°°°°°°°°°°°°°°°°°###################################".toCharArray(),
            "####################°°°°°°°°°°°°°°°°°°°°°°°°°###########°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
            "####################°°°°°°°°°°°°°°°°°°°°°°°°°###########°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
            "####################°°°°°°°°°°°°°°°°°°°°°°°°°###########°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
            "####################°°°°°°°°°°°°°°°°°°°°°°°°°###########°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
            "####################°°°°°°°°°°°°°°°°°°°°°°°°°###########°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
            "########################################################°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
            "################################################################################".toCharArray(),
            "################################################################################".toCharArray()
    }, UUID);
    final Chunk expectedConnectedChunk = testUtil.createChunkFromCharArray(new char[][] {
            "################################################################################".toCharArray(),
            "################################################################################".toCharArray(),
            "###########°°°°°°°°°°°°°°°°°°°°°#################°°°°°°°°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
            "###########°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
            "###########°°°°°°°°°°°°°°°°°°°°°#################°°°°°°°°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
            "###########°°°°°°°°°°°°°°°°°°°°°#################°°°°°°°°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
            "###########°°°°°°°°°°°°°°°°°°°°°#################°°°°°°°°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
            "###########°°°°°°°°°°°°°°°°°°°°°#################°°°°°°°°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
            "############################°###################################################".toCharArray(),
            "############################°###################################################".toCharArray(),
            "############################°###################################################".toCharArray(),
            "############################°###################################################".toCharArray(),
            "############################°###################################################".toCharArray(),
            "############################°###################################################".toCharArray(),
            "####################°°°°°°°°°°°°°°°°°°°°°°°°°###################################".toCharArray(),
            "####################°°°°°°°°°°°°°°°°°°°°°°°°°###################################".toCharArray(),
            "####################°°°°°°°°°°°°°°°°°°°°°°°°°###########°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
            "####################°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
            "####################°°°°°°°°°°°°°°°°°°°°°°°°°###########°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
            "####################°°°°°°°°°°°°°°°°°°°°°°°°°###########°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
            "####################°°°°°°°°°°°°°°°°°°°°°°°°°###########°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
            "########################################################°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
            "################################################################################".toCharArray(),
            "################################################################################".toCharArray()
    }, UUID);
    final Chunk expectedFinishedChunk = testUtil.createChunkFromCharArray(new char[][] {
            "########################################°#######################################".toCharArray(),
            "########################################°#######################################".toCharArray(),
            "###########°°°°°°°°°°°°°°°°°°°°°########°########°°°°°°°°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
            "###########°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
            "###########°°°°°°°°°°°°°°°°°°°°°#################°°°°°°°°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
            "###########°°°°°°°°°°°°°°°°°°°°°#################°°°°°°°°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
            "###########°°°⤊°°°°°°°°°°°°°°°°°#################°°°°°°°°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
            "###########°°°°°°°°°°°°°°°°°°°°°#################°°°°°°°°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
            "###########°################°###################################################".toCharArray(),
            "###########°################°###################################################".toCharArray(),
            "###########°################°###################################################".toCharArray(),
            "###########°################°###################################################".toCharArray(),
            "°°°°°°°°°°°°################°################################################°°°".toCharArray(),
            "############################°################################################°##".toCharArray(),
            "####################°°°°°°°°°°°°°°°°°°°°°°°°°################################°##".toCharArray(),
            "####################°°°°°°°°°°°°°°°°°°°°°°°°°################################°##".toCharArray(),
            "####################°°°°°°°°°°°°°°°°°°°°°°°°°###########°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
            "####################°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
            "####################°°°°°°°°°°°°°°°°°°°°°°°°°###########°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
            "####################°°°°°°°°°°°°°°°°°°°°°°°°°###########°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
            "####################°°°°°°°°°°°°°°°°°°°°°°°°°###########°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
            "########################################°###############°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
            "########################################°#######################################".toCharArray(),
            "########################################°#######################################".toCharArray()
    }, UUID);

    @BeforeEach
    void setup() {
        mockedWorld = mock(World.class);
        mockedGame = mock(Game.class);
        when(mockedGame.getWorld()).thenReturn(mockedWorld);
        when(mockedWorld.getConfig()).thenReturn(config);

        sut = new BSPDungeonGenerator(mockedGame);

        randomMock = new COCTestRandom();
        sut.setRandom(randomMock);

        when(mockedWorld.getSeed()).thenReturn(WORLD_SEED);

        Chunk initialCurrentChunk = new Chunk(new Tile[CHUNK_HEIGHT][CHUNK_WIDTH], new Coordinate(0, 0, 0), WORLD_SEED, UUID);
        initialCurrentChunkSpy = spy(initialCurrentChunk);
        when(initialCurrentChunkSpy.getRandom()).thenReturn(randomMock);
        when(mockedWorld.getChunk(any())).thenReturn(initialCurrentChunkSpy);

        above.setTile(new Tile(TileType.STAIRS_DOWN, new Coordinate(14, 6)));
        up.setDownConnectionX(40);
        down.setUpConnectionX(40);
        left.setRightConnectionY(12);
        right.setLeftConnectionY(12);

        expectedFinishedChunk.addStructure(new Structure(new Coordinate(14, 6), StructureType.DUNGEON_STAIRS_UP));

        testUtil = new TestUtil();

        BinarySpacePartitioning binarySpacePartitioningMock = mock(BinarySpacePartitioning.class);
        when(binarySpacePartitioningMock.getTree(any(Room.class), anyInt(), anyInt(), anyInt())).thenReturn(tree);
        sut.setBinarySpacePartitioning(binarySpacePartitioningMock);
    }

    /**
     * Test code WPT50
     */
    @Test
    @DisplayName("test whether dungeons are created correctly")
    void testGenerateDungeonChunk() {
        // Arrange
        randomMock.setIntegers(testUtil.concatenateArrays(DUNGEON_ROOM_MEASUREMENTS, CORRIDOR_CONNECTION_POINTS));
        when(mockedWorld.getChunk(new Coordinate(0, 0, -1))).thenReturn(above);
        when(mockedWorld.getChunk(new Coordinate(0, -1, 0))).thenReturn(up);
        when(mockedWorld.getChunk(new Coordinate(0, 1, 0))).thenReturn(down);
        when(mockedWorld.getChunk(new Coordinate(-1, 0, 0))).thenReturn(left);
        when(mockedWorld.getChunk(new Coordinate(1, 0, 0))).thenReturn(right);

        // Act
        Chunk actualChunk = sut.generateDungeonChunk(new Coordinate(0, 0, 0));

        // Assert
        assertEquals(expectedFinishedChunk, actualChunk);
    }

    /**
     * Test code WPT51
     */
    @Test
    @DisplayName("test whether dungeon rooms are created correctly")
    void testGenerateDungeonRooms() {
        // Arrange
        randomMock.setIntegers(testUtil.concatenateArrays(DUNGEON_ROOM_MEASUREMENTS, CORRIDOR_CONNECTION_POINTS));
        sut.setCurrentChunk(new Chunk(new Tile[CHUNK_HEIGHT][CHUNK_WIDTH], new Coordinate(0, 0, 0), WORLD_SEED, UUID));
        sut.setInitialCurrentChunk(initialCurrentChunkSpy);


        // Act
        sut.generateDungeonRooms(tree);

        // Assert
        assertEquals(expectedRoomChunk, sut.getCurrentChunk());
    }

    /**
     * Test code WPT52
     */
    @Test
    @DisplayName("test whether dungeon rooms are connected correctly")
    void testConnectDungeonRooms() {
        // Arrange
        randomMock.setIntegers(CORRIDOR_CONNECTION_POINTS);

        sut.setCurrentChunk(expectedRoomChunk);
        sut.setInitialCurrentChunk(initialCurrentChunkSpy);

        // Act
        sut.connectDungeonRooms(tree);

        // Assert
        assertEquals(expectedConnectedChunk, sut.getCurrentChunk());
    }

    /**
     * Test code WPT53
     */
    @Test
    @DisplayName("test whether children of a node are connect correctly")
    void testConnectChildren() {
        // Arrange
        randomMock.setIntegers(new int[] {115, 10});

        Chunk expectedChunk = testUtil.createChunkFromCharArray(new char[][] {
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "###########°°°°°°°°°°°°°°°°°°°°°#################°°°°°°°°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
                "###########°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
                "###########°°°°°°°°°°°°°°°°°°°°°#################°°°°°°°°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
                "###########°°°°°°°°°°°°°°°°°°°°°#################°°°°°°°°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
                "###########°°°°°°°°°°°°°°°°°°°°°#################°°°°°°°°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
                "###########°°°°°°°°°°°°°°°°°°°°°#################°°°°°°°°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "####################°°°°°°°°°°°°°°°°°°°°°°°°°###################################".toCharArray(),
                "####################°°°°°°°°°°°°°°°°°°°°°°°°°###################################".toCharArray(),
                "####################°°°°°°°°°°°°°°°°°°°°°°°°°###########°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
                "####################°°°°°°°°°°°°°°°°°°°°°°°°°###########°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
                "####################°°°°°°°°°°°°°°°°°°°°°°°°°###########°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
                "####################°°°°°°°°°°°°°°°°°°°°°°°°°###########°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
                "####################°°°°°°°°°°°°°°°°°°°°°°°°°###########°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
                "########################################################°°°°°°°°°°°°°°°°°°°°°°##".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray()
        }, UUID);

        sut.setCurrentChunk(expectedRoomChunk);
        sut.setInitialCurrentChunk(initialCurrentChunkSpy);

        // Act
        sut.connectChildren(parentNode);

        // Assert
        assertEquals(expectedChunk, sut.getCurrentChunk());
    }

    /**
     * Test code WPT54
     */
    @Test
    @DisplayName("test whether 2 different positions are connected correctly")
    void testConnect() {
        // Arrange
        Coordinate left = new Coordinate(31, 7);
        Coordinate right = new Coordinate(49, 10);

        sut.setCurrentChunk(expectedEmptyChunk);
        sut.setInitialCurrentChunk(initialCurrentChunkSpy);

        Chunk expectedChunk = testUtil.createChunkFromCharArray(new char[][] {
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "###############################°°°°°°°°°°°°°°°°°°°##############################".toCharArray(),
                "#################################################°##############################".toCharArray(),
                "#################################################°##############################".toCharArray(),
                "#################################################°##############################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray()
        }, UUID);

        // Act
        sut.connect(left, right);

        // Assert
        assertEquals(expectedChunk, sut.getCurrentChunk());
    }

    /**
     * Test code WPT55
     */
    @Test
    @DisplayName("test whether 2 different x positions are connected correctly")
    void testConnectX() {
        // Arrange
        int leftX = 15;
        int rightX = 76;
        int y = 15;

        sut.setCurrentChunk(expectedEmptyChunk);
        sut.setInitialCurrentChunk(initialCurrentChunkSpy);

        Chunk expectedChunk = testUtil.createChunkFromCharArray(new char[][] {
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "###############°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°###".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray()
        }, UUID);

        // Act
        sut.connectX(leftX, rightX, y);

        // Assert
        assertEquals(expectedChunk, sut.getCurrentChunk());
    }

    /**
     * Test code WPT56
     */
    @Test
    @DisplayName("test whether 2 different y positions are connected correctly")
    void testConnectY() {
        // Arrange
        int leftY = 2;
        int rightY = 20;
        int x = 3;

        sut.setCurrentChunk(expectedEmptyChunk);
        sut.setInitialCurrentChunk(initialCurrentChunkSpy);

        Chunk expectedChunk = testUtil.createChunkFromCharArray(new char[][] {
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "###°############################################################################".toCharArray(),
                "###°############################################################################".toCharArray(),
                "###°############################################################################".toCharArray(),
                "###°############################################################################".toCharArray(),
                "###°############################################################################".toCharArray(),
                "###°############################################################################".toCharArray(),
                "###°############################################################################".toCharArray(),
                "###°############################################################################".toCharArray(),
                "###°############################################################################".toCharArray(),
                "###°############################################################################".toCharArray(),
                "###°############################################################################".toCharArray(),
                "###°############################################################################".toCharArray(),
                "###°############################################################################".toCharArray(),
                "###°############################################################################".toCharArray(),
                "###°############################################################################".toCharArray(),
                "###°############################################################################".toCharArray(),
                "###°############################################################################".toCharArray(),
                "###°############################################################################".toCharArray(),
                "###°############################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray(),
                "################################################################################".toCharArray()
        }, UUID);

        // Act
        sut.connectY(leftY, rightY, x);

        // Assert
        assertEquals(expectedChunk, sut.getCurrentChunk());
    }

    /**
     * Test code WPT57
     */
    @Test
    @DisplayName("test whether the value number is shifted towards the target properly when the target is above the value")
    void testApproachTargetValueUpwards() {
        // Arrange
        int expected = -4;
        int value = -5;
        int target = 10;

        // Act
        int actual = sut.approachTargetValue(value, target);

        // Assert
        assertEquals(expected, actual);
    }

    /**
     * Test code WPT58
     */
    @Test
    @DisplayName("test whether the value number is shifted towards the target properly when the target is below the value")
    void testApproachTargetValueDownwards() {
        // Arrange
        int expected = 7;
        int value = 8;
        int target = 3;

        // Act
        int actual = sut.approachTargetValue(value, target);

        // Assert
        assertEquals(expected, actual);
    }

    /**
     * Test code WPT59
     */
    @Test
    @DisplayName("test whether the value number is shifted towards the target properly when the target is equal to the value")
    void testApproachTargetValueNeutral() {
        // Arrange
        int expected = 3;
        int value = 3;
        int target = 3;

        // Act
        int actual = sut.approachTargetValue(value, target);

        // Assert
        assertEquals(expected, actual);
    }

    /**
     * Test code WPT60
     */
    @Test
    @DisplayName("test whether a random passable location can be retrieved from a room")
    void testGetRandomPassablePosition() {
        // Arrange
        sut.setCurrentChunk(expectedConnectedChunk);
        randomMock.setIntegers(new int[] {8});
        Coordinate expected = new Coordinate(12, 4);

        // Act
        Coordinate actual = sut.getRandomDungeonFloorTilePosition(parentNodeRoom);

        // Assert
        assertEquals(expected, actual);
    }

    /**
     * Test code WPT61
     */
    @Test
    @DisplayName("test whether a dungeon room is generated correctly and lies within the right bounding box")
    void testGenerateDungeonRoom() {
        // Arrange
        Room mockedRoom = testUtil.createRoomSpy(0, 0, 40, 19);
        int x = 2;
        int y = 5;
        int width = 22;
        int height = 8;
        randomMock.setIntegers(new int[] {x, y, width, height});
        Room expected = new Room(new Coordinate(x, y), width, height);

        // Act
        Room actual = sut.determineDungeonRoom(mockedRoom);

        // Assert
        assertEquals(expected, actual);
    }

    /**
     * Test code WTP62
     */
    @DisplayName("test whether dungeon generation can be completed within 500ms")
    @Test
    void testDungeonCreationTime() {
        // Arrange
        randomMock.setIntegers(testUtil.concatenateArrays(DUNGEON_ROOM_MEASUREMENTS, CORRIDOR_CONNECTION_POINTS));
        sut = new BSPDungeonGenerator(mockedGame);
        BinarySpacePartitioning binarySpacePartitioningMock = mock(BinarySpacePartitioning.class);
        when(binarySpacePartitioningMock.getTree(any(Room.class), anyInt(), anyInt(), anyInt())).thenReturn(tree);
        sut.setBinarySpacePartitioning(binarySpacePartitioningMock);
        when(mockedWorld.getChunk(new Coordinate(0, 0, -1))).thenReturn(above);
        when(mockedWorld.getChunk(new Coordinate(0, -1, 0))).thenReturn(up);
        when(mockedWorld.getChunk(new Coordinate(0, 1, 0))).thenReturn(down);
        when(mockedWorld.getChunk(new Coordinate(-1, 0, 0))).thenReturn(left);
        when(mockedWorld.getChunk(new Coordinate(1, 0, 0))).thenReturn(right);

        // Act & Assert
        assertTimeout(java.time.Duration.ofMillis(500), () -> {
            sut.generateDungeonChunk(new Coordinate(0, 0, 0));
        });
    }
}
