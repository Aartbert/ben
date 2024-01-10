package nl.han.pathfinding;

import nl.han.pathfinding.exception.NoPathFoundException;
import nl.han.pathfinding.grid.Grid;
import nl.han.pathfinding.grid.Node;
import nl.han.shared.datastructures.world.Chunk;
import nl.han.shared.datastructures.world.Coordinate;
import nl.han.shared.datastructures.world.Tile;
import nl.han.shared.enums.Action;
import nl.han.shared.enums.TileType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static nl.han.shared.enums.Action.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for {@link AStar}.
 *
 * @see <a href="https://confluenceasd.aimsites.nl/x/PALjGQ">Testrapport</a>
 */
class AStarTest {
    final AStar sut = new AStar();
    private Chunk chunk;
    final char[][] tileCharacters = {
            "_______.................________________________..__________................____".toCharArray(),
            "________..............._________________...___....__________.._............_____".toCharArray(),
            "________...............___________.........___..______________.......___..______".toCharArray(),
            "..._____................_________............_________________.......__________.".toCharArray(),
            "....______.............._________.............________________........________..".toCharArray(),
            "..._______.............__________.............._.______________..._.._________..".toCharArray(),
            "...________............____________..............____..__.______..____#######_..".toCharArray(),
            "....________............___________..............____.._______________#_____#_..".toCharArray(),
            "......______..............________................____._______________#_____#__.".toCharArray(),
            "......______................____.................____....___~~________#######__.".toCharArray(),
            "........___...................._..................____...___~~~~____~~~~________".toCharArray(),
            "..................................................._____.___~~~~~__~~~~~_______.".toCharArray(),
            "..............._..............................._.._________~~~~~~__~~~~~_______.".toCharArray(),
            "........._.__.__..............................._____________~~~______~__________".toCharArray(),
            "........________...............................______________________~__________".toCharArray(),
            "........_________..............................____._____~___________~___~______".toCharArray(),
            "....__.._____________.........................___.....____~___~~_____~~~~~______".toCharArray(),
            "...__________________.........................__....._____~_~~~__________~______".toCharArray(),
            "..___________________......................._.__....______~~~~__________________".toCharArray(),
            "_____________________...................___._____.._______~~~~~_________________".toCharArray(),
            "________~~~_~________.................__________....______~~~~~~________________".toCharArray(),
            "_____~~~~~~~_________..............._____________..________~~~~~______..________".toCharArray(),
            "____~~~~~~~~________...............________________._______~~~~~~____.........__".toCharArray(),
            "____~~~~~~~________..............._________________._______~~~~~____..........__".toCharArray()
    };

    @BeforeEach
    void setUp() {
        Tile[][] tiles = new Tile[tileCharacters.length][tileCharacters[0].length];
        for (int y = 0; y < tileCharacters.length; y++) {
            for (int x = 0; x < tileCharacters[0].length; x++) {
                switch (tileCharacters[y][x]) {
                    case '~':
                        tiles[y][x] = new Tile(TileType.WATER, new Coordinate(x, y));
                        break;
                    case '#':
                        tiles[y][x] = new Tile(TileType.WALL, new Coordinate(x, y));
                        break;
                    case '.':
                        tiles[y][x] = new Tile(TileType.ICE, new Coordinate(x, y));
                        break;
                    default:
                        tiles[y][x] = new Tile(TileType.GRASS, new Coordinate(x, y));
                        break;
                }
            }
        }

        chunk = new Chunk(tiles, new Coordinate(0, 0, 0), 0L, UUID.randomUUID());
    }

    /**
     * Test code PTH01
     */
    @Test
    @DisplayName("test that calculateManhattanDistance calculates the manhattan distance correctly")
    void calculateManhattanDistanceTest() {
        // Arrange
        Node startNode = new Node(5, 10, 2, true, '.');
        Node endNode = new Node(12, 30, 1, true, '_');
        int expected = 27;

        // Act
        int actual = sut.calculateManhattanDistance(startNode, endNode);

        // Assert
        assertEquals(expected, actual);
    }

    /**
     * Test code PTH02
     */
    @Test
    @DisplayName("test that setStartAndEndNodes correctly changes the values in the start and end nodes")
    void setStartAndEndNodes() {
        // Arrange
        Node startNode = new Node(5, 10, 2, true, '.');
        Node endNode = new Node(12, 30, 1, true, '_');
        Node expectedStartNode = new Node(5, 10, 2, true, 'S');
        Node expectedEndNode = new Node(12, 30, 1, true, 'E');
        expectedStartNode.setG(0);
        expectedStartNode.setH(27);
        expectedStartNode.setF(expectedStartNode.getG() + expectedStartNode.getH());

        // Act
        sut.setStartAndEndNodes(startNode, endNode);

        // Assert
        assertEquals(expectedStartNode, startNode);
        assertEquals(expectedEndNode, endNode);
    }

    /**
     * Test code PTH03
     */
    @Test
    @DisplayName("test that findBestNodeFrom returns the node with the lowest f-value")
    void findBestNodeFrom() {
        // Arrange
        Node expectedBestNode = new Node(5, 10, 2, true, '.');
        expectedBestNode.setF(3);
        expectedBestNode.setG(1);
        expectedBestNode.setH(2);
        Node worseNode = new Node(4, 10, 2, true, '.');
        worseNode.setF(5);
        worseNode.setG(2);
        worseNode.setH(3);
        Node worstNode = new Node(0, 0, 1, true, '_');
        worstNode.setF(20);
        worstNode.setG(10);
        worstNode.setH(10);

        // Act
        Node actual = sut.findBestNodeFrom(List.of(expectedBestNode, worseNode, worstNode));

        // Assert
        assertEquals(expectedBestNode, actual);
    }

    /**
     * Test code PTH04
     */
    @Test
    @DisplayName("test that getShortestPath correctly returns all the previousNodes in a path")
    void getShortestPath() {
        // Arrange
        Node endNode = new Node(5, 10, 2, true, '.');
        Node previousNode = new Node(4, 10, 2, true, '.');
        Node previousPreviousNode = new Node(3, 10, 1, true, '_');
        endNode.setPreviousNode(previousNode);
        previousNode.setPreviousNode(previousPreviousNode);
        List<Action> expected = List.of(MOVE_RIGHT, MOVE_RIGHT);

        // Act
        List<Action> actual = sut.getShortestPath(endNode);

        // Assert
        assertEquals(expected, actual);
    }

    /**
     * Test code PTH05
     */
    @Test
    @DisplayName("test that findPath finds the correct valid path")
    void findPath() throws NoPathFoundException {
        // Arrange
        Tile startTile = chunk.getTile(new Coordinate(0, 0));
        Tile endTile = chunk.getTile(new Coordinate(9, 9));

        int[][] impassableTiles = {
                { 0, 1 }, { 0, 6 },
                { 1, 1 }, { 1, 3 }, { 1, 4 }, { 1, 5 }, { 1, 6 }, { 1, 7 },
                { 2, 1 },
                { 3, 3 }, { 3, 4 }, { 3, 5 }, { 3, 6 }, { 3, 7 },
                { 4, 7 },
                { 5, 1 }, { 5, 5 }, { 5, 6 }, { 5, 7 },
                { 6, 1 }, { 6, 7 },
                { 7, 1 }, { 7, 4 }, { 7, 5 }, { 7, 6 }, { 7, 7 },
                { 8, 1 }, { 8, 2 }, { 8, 3 }, { 8, 4 }, { 8, 4 }, { 8, 7 },
                { 9, 7 }
        };

        for (int[] tile : impassableTiles) {
            Coordinate coordinate = new Coordinate(tile[1], tile[0]);
            chunk.setTile(new Tile(TileType.WALL, coordinate));
        }

        List<Action> expected = List.of(
                MOVE_DOWN, MOVE_DOWN, MOVE_DOWN, MOVE_RIGHT, MOVE_RIGHT, MOVE_UP, MOVE_RIGHT, MOVE_RIGHT, MOVE_RIGHT,
                MOVE_RIGHT, MOVE_RIGHT, MOVE_RIGHT, MOVE_DOWN, MOVE_DOWN, MOVE_DOWN, MOVE_DOWN, MOVE_DOWN, MOVE_DOWN,
                MOVE_DOWN, MOVE_RIGHT);

        // Act
        List<Action> actual = sut.findPath(chunk, startTile, endTile);

        // Assert
        assertEquals(expected, actual);
    }

    /**
     * Test code PTH06
     */
    @Test
    @DisplayName("test that findPath throws NoPathFoundException when no path exists")
    void findPathThrowsWhenNoPathExists() {
        // Arrange
        Tile startTile = chunk.getTile(new Coordinate(0, 0));
        Tile endTile = chunk.getTile(new Coordinate(1, 2));
        chunk.setTile(new Tile(TileType.WALL, new Coordinate(0, 1)));
        chunk.setTile(new Tile(TileType.WALL, new Coordinate(1, 0)));
        chunk.setTile(new Tile(TileType.WALL, new Coordinate(1, 1)));

        // Act + Assert
        assertThrows(NoPathFoundException.class, () -> sut.findPath(chunk, startTile, endTile));
    }

    /**
     * Test code PTH07
     */
    @Test
    @DisplayName("test that convertPathToActions correctly converts a path to a list of Actions")
    void convertPathToActions() {
        // Arrange
        Grid grid = new Grid(chunk);
        List<Node> path = new ArrayList<>(List.of(
                grid.getNodes()[0][1],
                grid.getNodes()[1][1],
                grid.getNodes()[1][2],
                grid.getNodes()[2][2],
                grid.getNodes()[1][2],
                grid.getNodes()[1][1]));

        List<Action> expected = List.of(MOVE_RIGHT, MOVE_DOWN, MOVE_UP, MOVE_LEFT, MOVE_UP);

        // Act
        List<Action> result = sut.convertPathToActions(path);

        // Assert
        assertEquals(expected, result);
    }
}
