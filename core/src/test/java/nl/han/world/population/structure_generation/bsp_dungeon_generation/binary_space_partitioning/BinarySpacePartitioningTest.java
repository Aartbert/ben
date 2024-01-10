package nl.han.world.population.structure_generation.bsp_dungeon_generation.binary_space_partitioning;

import nl.han.shared.datastructures.world.Coordinate;
import nl.han.world.population.TestUtil;
import nl.han.shared.utils.random.COCTestRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing class for {@link BinarySpacePartitioning}.
 * @see <a href="https://confluenceasd.aimsites.nl/display/ASDS1G2/Testrapport+Onderzoek+Wereldpopulatie">Testrapport</a>
 * @author Lucas van Steveninck
 */
class BinarySpacePartitioningTest {
    BinarySpacePartitioning sut;
    COCTestRandom randomMock;
    final TestUtil testUtil = new TestUtil();
    static final int MAX_LEAF_NODES = 8;
    static final int MIN_ROOM_WIDTH = 15;
    static final int MIN_ROOM_HEIGHT = 8;
    static final float[] TREE_CREATION_FLOATS = new float[] {0.6f, 0.6f, 0.8f, 0.8f, 0.4f, 0.2f, 0.2f};
    static final int[] TREE_CREATION_INTEGERS = new int[] {40, 25, 12};
    final Room mockedTreeCreationRoom = testUtil.createRoomSpy(0, 0, 80, 24);
    static final float[] PARTITIONING_FLOATS = new float[] {0.4f, 0.2f, 0.2f};
    static final int[] PARTITIONING_INTEGERS = new int[] {12};
    final Room mockedPartitionRoom = testUtil.createRoomSpy(40, 0, 40, 24);

    @BeforeEach
    void setup() {
        sut = new BinarySpacePartitioning();
        randomMock = new COCTestRandom();
        sut.setRandom(randomMock);
    }

    /**
     * Test code WPT32
     */
    @Test
    @DisplayName("test whether a BSP tree is created correctly according to minimum measurement requirements")
    void testGetTree() {
        // Arrange
        ParentNode<Room, Room> expectedRoot = new ParentNode<>(
                new Room(new Coordinate(0, 0), 80, 24),
                new ParentNode<>(
                        new Room(new Coordinate(0, 0), 40, 24),
                        new LeafNode<>(new Room(new Coordinate(0, 0), 25, 24)),
                        new LeafNode<>(new Room(new Coordinate(25, 0), 15, 24))
                ),
                new ParentNode<>(
                        new Room(new Coordinate(40, 0), 40, 24),
                        new LeafNode<>(new Room(new Coordinate(40, 0), 40, 12)),
                        new LeafNode<>(new Room(new Coordinate(40, 12), 40, 12))
                ));
        Tree<Room, Room> expected = new Tree<>(expectedRoot);

        randomMock.setFloats(TREE_CREATION_FLOATS);
        randomMock.setIntegers(TREE_CREATION_INTEGERS);

        // Act
        Tree<Room, Room> actual = sut.getTree(mockedTreeCreationRoom, MIN_ROOM_WIDTH, MIN_ROOM_HEIGHT, MAX_LEAF_NODES);

        // Assert
        assertEquals(expected, actual);
    }

    /**
     * Test code WPT33
     */
    @Test
    @DisplayName("test whether tree creation is cut off correctly when the max number of leaf nodes is reached")
    void testGetTreeCutOffByMaxLeafNodes() {
        // Arrange
        LeafNode<Room, Room> expectedRoot = new LeafNode<>(new Room(new Coordinate(0, 0), 80, 24));
        Tree<Room, Room> expected = new Tree<>(expectedRoot);

        randomMock.setFloats(TREE_CREATION_FLOATS);
        randomMock.setIntegers(TREE_CREATION_INTEGERS);

        // Act
        Tree<Room, Room> actual = sut.getTree(mockedTreeCreationRoom, MIN_ROOM_WIDTH, MIN_ROOM_HEIGHT, 1);

        // Assert
        assertEquals(expected, actual);
    }

    /**
     * Test code WPT34
     */
    @Test
    @DisplayName("test whether tree creation is cut off when illegal arguments are provided")
    void testGetTreeIllegalArguments() {
        // Arrange
        int illegalWidth = 0;
        int illegalHeight = 0;
        int illegalMaxLeafNodes = 0;
        String expectedMessage = "Binary space partitioning always results in at least one 1x1 room.";

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> sut.getTree(mockedTreeCreationRoom, illegalWidth, illegalHeight, illegalMaxLeafNodes));
        assertEquals(expectedMessage, exception.getMessage());
    }

    /**
     * Test code WPT35
     */
    @Test
    @DisplayName("test whether a room can be partitioned into a node correctly according to minimum measurement requirements")
    void testPartition() {
        // Arrange
        Node<Room, Room> expected = new ParentNode<>(
                new Room(new Coordinate(40, 0), 40, 24),
                new LeafNode<>(new Room(new Coordinate(40, 0), 40, 12)),
                new LeafNode<>(new Room(new Coordinate(40, 12), 40, 12))
        );

        randomMock.setFloats(PARTITIONING_FLOATS);
        randomMock.setIntegers(PARTITIONING_INTEGERS);

        // Act
        Node<Room, Room> actual = sut.partition(mockedPartitionRoom, MIN_ROOM_WIDTH, MIN_ROOM_HEIGHT, MAX_LEAF_NODES);

        // Assert
        assertEquals(expected, actual);
    }

    /**
     * Test code WPT36
     */
    @Test
    @DisplayName("test whether the partitioning process is cut off correctly when the max number of leaf nodes is reached")
    void testPartitionCutOffByMaxLeafNodes() {
        // Arrange
        Node<Room, Room> expected = new LeafNode<>(new Room(new Coordinate(40, 0), 40, 24));

        sut.setCurrentLeafNodes(1);

        randomMock.setFloats(PARTITIONING_FLOATS);
        randomMock.setIntegers(PARTITIONING_INTEGERS);

        // Act
        Node<Room, Room> actual = sut.partition(mockedPartitionRoom, MIN_ROOM_WIDTH, MIN_ROOM_HEIGHT, 1);

        // Assert
        assertEquals(expected, actual);
    }

    /**
     * Test code WPT37
     */
    @Test
    @DisplayName("test if it is possible to randomly split rooms horizontally")
    void testAttemptHorizontalSplit() {
        // Arrange
        randomMock.setIntegers(new int[] {9});
        List<Room> expected = new ArrayList<>(List.of(
                new Room(new Coordinate(40, 0), 40, 9),
                new Room(new Coordinate(40, 9), 40, 15)));
        Room mockedRoom = testUtil.createRoomSpy(40, 0, 40, 24);

        // Act
        List<Room> actual = sut.attemptRandomHorizontalSplit(mockedRoom, MIN_ROOM_HEIGHT, MAX_LEAF_NODES);

        // Assert
        assertEquals(expected, actual);
    }

    /**
     * Test code WPT38
     */
    @Test
    @DisplayName("test if it is not possible to horizontally split a room when there is not enough space for the room to be split into 2 separate rooms that both meet the minimum height requirement")
    void testAttemptIllegalHorizontalSplit() {
        // Arrange
        List<Room> expected = new ArrayList<>();
        Room mockedRoom = testUtil.createRoomSpy(40, 0, 40, 14);

        // Act
        List<Room> actual = sut.attemptRandomHorizontalSplit(mockedRoom, MIN_ROOM_HEIGHT, MAX_LEAF_NODES);

        // Assert
        assertEquals(expected, actual);
    }

    /**
     * Test code WPT39
     */
    @Test
    @DisplayName("test if it is possible to horizontally split a room when the maximum number of leaf nodes would be surpassed as a result")
    void testAttemptHorizontalSplitMaxLeafNodes() {
        // Arrange
        List<Room> expected = new ArrayList<>();
        Room mockedRoom = testUtil.createRoomSpy(40, 0, 40, 24);
        sut.setCurrentLeafNodes(1);

        // Act
        List<Room> actual = sut.attemptRandomHorizontalSplit(mockedRoom, MIN_ROOM_HEIGHT, 1);

        // Assert
        assertEquals(expected, actual);
    }

    /**
     * Test code WPT40
     */
    @Test
    @DisplayName("test if it is possible to randomly split rooms vertically")
    void testAttemptVerticalSplit() {
        // Arrange
        randomMock.setIntegers(new int[] {63});
        List<Room> expected = new ArrayList<>(List.of(
                new Room(new Coordinate(40, 0), 23, 24),
                new Room(new Coordinate(63, 0), 17, 24)));
        Room mockedRoom = testUtil.createRoomSpy(40, 0, 40, 24);

        // Act
        List<Room> actual = sut.attemptRandomVerticalSplit(mockedRoom, MIN_ROOM_WIDTH, MAX_LEAF_NODES);

        // Assert
        assertEquals(expected, actual);
    }

    /**
     * Test code WPT41
     */
    @Test
    @DisplayName("test if it is not possible to vertically split a room when there is not enough space for the room to be split into 2 separate rooms that both meet the minimum width requirement")
    void testAttemptIllegalVerticalSplit() {
        // Arrange
        List<Room> expected = new ArrayList<>();
        Room mockedRoom = testUtil.createRoomSpy(40, 0, 28, 24);

        // Act
        List<Room> actual = sut.attemptRandomVerticalSplit(mockedRoom, MIN_ROOM_WIDTH, MAX_LEAF_NODES);

        // Assert
        assertEquals(expected, actual);
    }

    /**
     * Test code WPT42
     */
    @Test
    @DisplayName("test if it is possible to vertically split a room when the maximum number of leaf nodes would be surpassed as a result")
    void testAttemptVerticalSplitMaxLeafNodes() {
        // Arrange
        List<Room> expected = new ArrayList<>();
        Room mockedRoom = testUtil.createRoomSpy(40, 0, 40, 24);
        sut.setCurrentLeafNodes(1);

        // Act
        List<Room> actual = sut.attemptRandomVerticalSplit(mockedRoom, MIN_ROOM_WIDTH, 1);

        // Assert
        assertEquals(expected, actual);
    }
}
