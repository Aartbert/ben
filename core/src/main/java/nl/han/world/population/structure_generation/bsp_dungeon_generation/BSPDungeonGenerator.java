package nl.han.world.population.structure_generation.bsp_dungeon_generation;

import lombok.Getter;
import lombok.Setter;
import nl.han.shared.datastructures.WorldRules;
import nl.han.shared.datastructures.WorldRulesBuilder;
import nl.han.shared.datastructures.game.Game;
import nl.han.shared.datastructures.world.Chunk;
import nl.han.shared.datastructures.world.Coordinate;
import nl.han.shared.datastructures.world.Structure;
import nl.han.shared.datastructures.world.Tile;
import nl.han.shared.enums.StructureType;
import nl.han.shared.enums.TileType;
import nl.han.shared.utils.random.ICOCRandom;
import nl.han.world.population.structure_generation.IDungeonGenerator;
import nl.han.world.population.structure_generation.bsp_dungeon_generation.binary_space_partitioning.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static nl.han.shared.datastructures.world.Chunk.CHUNK_HEIGHT;
import static nl.han.shared.datastructures.world.Chunk.CHUNK_WIDTH;

/**
 * This class is responsible for generating dungeon chunks and closely utilizes a generic binary space partitioning
 * algorithm that doesn't have any information on this class.
 *
 * @author Lucas van Steveninck
 */
@Setter
@Getter
public class BSPDungeonGenerator implements IDungeonGenerator {
    private static final int MAX_ROOMS = 5;
    private static final int MIN_ROOM_WIDTH = 6;
    private static final int MIN_ROOM_HEIGHT = 6;
    private static final int OFFSET = 2;
    private final Game game;
    private final int maxDepth;
    private BinarySpacePartitioning binarySpacePartitioning;
    private ICOCRandom random;
    private Chunk initialCurrentChunk;
    private Chunk currentChunk;
    private Room chunkAsRoom = new Room(new Coordinate(0, 0), CHUNK_WIDTH, CHUNK_HEIGHT);

    /**
     * Constructs a BSPDungeonGenerator that should be used to generate dungeon-like chunks.
     *
     * @param game The current state of the game which used to find surrounding chunks and the world seed.
     */
    public BSPDungeonGenerator(Game game) {
        WorldRules rules = WorldRulesBuilder.convertToWorldRules(game.getWorld().getConfig());
        this.binarySpacePartitioning = new BinarySpacePartitioning();
        this.game = game;
        this.maxDepth = rules.getDungeonDepth();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Chunk generateDungeonChunk(Coordinate chunkCoordinate) {
        currentChunk = game.getWorld().getChunk(chunkCoordinate);
        if (currentChunk == null) currentChunk = softGenerateDungeonChunk(chunkCoordinate);
        initialCurrentChunk = new Chunk(currentChunk);
        random = currentChunk.getRandom();
        binarySpacePartitioning.setRandom(random);

        Tree<Room, Room> boundingBoxTree = binarySpacePartitioning.getTree(chunkAsRoom, MIN_ROOM_WIDTH + OFFSET * 2, MIN_ROOM_HEIGHT + OFFSET * 2, MAX_ROOMS);

        generateDungeonRooms(boundingBoxTree);
        connectDungeonRooms(boundingBoxTree);
        connectToConnectionPoints();
        finishStaircaseGeneration();

        currentChunk.setFullyGenerated(true);
        return currentChunk;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Chunk softGenerateDungeonChunk(Coordinate chunkCoordinate) {
        currentChunk = game.getWorld().getChunk(chunkCoordinate);
        if (currentChunk == null)
            currentChunk = new Chunk(new Tile[CHUNK_HEIGHT][CHUNK_WIDTH], chunkCoordinate, game.getWorld().getSeed(), game.getId());
        initialCurrentChunk = new Chunk(currentChunk);
        random = currentChunk.getRandom();

        if (maxDepth == -1 || currentChunk.getCoordinate().z() < maxDepth) {
            generateDownwardsStaircase();
        }
        generateConnectionPoints();

        game.getWorld().addChunk(currentChunk);
        return currentChunk;
    }

    /**
     * This method connects the current chunk and every adjacent chunk on the same z-coordinate by calculating
     * their joined connection point. The joined connection point will be a random number based on the connection
     * points that the 2 chunks pre-determined.
     */
    private void connectToConnectionPoints() {
        Coordinate coordinate = currentChunk.getCoordinate();
        int x;
        int y;
        Coordinate left;
        Coordinate right;

        Chunk upChunk = game.getWorld().getChunk(new Coordinate(coordinate.x(), coordinate.y() - 1, coordinate.z()));
        x = Math.abs(currentChunk.getUpConnectionX() - upChunk.getDownConnectionX());
        left = new Coordinate(x, 0);
        right = findNearestPassablePosition(currentChunk.getTiles(), left);
        connectY(left.y(), right.y(), left.x());
        connectX(left.x(), right.x(), right.y());

        Chunk downChunk = game.getWorld().getChunk(new Coordinate(coordinate.x(), coordinate.y() + 1, coordinate.z()));
        x = Math.abs(currentChunk.getDownConnectionX() - downChunk.getUpConnectionX());
        left = new Coordinate(x, CHUNK_HEIGHT - 1);
        right = findNearestPassablePosition(currentChunk.getTiles(), left);
        connectY(left.y(), right.y(), left.x());
        connectX(left.x(), right.x(), right.y());

        Chunk leftChunk = game.getWorld().getChunk(new Coordinate(coordinate.x() - 1, coordinate.y(), coordinate.z()));
        y = Math.abs(currentChunk.getLeftConnectionY() - leftChunk.getRightConnectionY());
        left = new Coordinate(0, y);
        right = findNearestPassablePosition(currentChunk.getTiles(), left);
        connectX(left.x(), right.x(), left.y());
        connectY(left.y(), right.y(), right.x());

        Chunk rightChunk = game.getWorld().getChunk(new Coordinate(coordinate.x() + 1, coordinate.y(), coordinate.z()));
        y = Math.abs(currentChunk.getRightConnectionY() - rightChunk.getLeftConnectionY());
        left = new Coordinate(CHUNK_WIDTH - 1, y);
        right = findNearestPassablePosition(currentChunk.getTiles(), left);
        connectX(left.x(), right.x(), left.y());
        connectY(left.y(), right.y(), right.x());

    }

    /**
     * This method determines the connection points for a chunk, so that the chunks next to it can be generated
     * successfully.
     */
    private void generateConnectionPoints() {
        currentChunk.setUpConnectionX(random.nextInt(0, CHUNK_WIDTH));
        currentChunk.setDownConnectionX(random.nextInt(0, CHUNK_WIDTH));
        currentChunk.setLeftConnectionY(random.nextInt(0, CHUNK_HEIGHT));
        currentChunk.setRightConnectionY(random.nextInt(0, CHUNK_HEIGHT));
    }

    /**
     * This method will finish staircase generation assuming that the current chunk has previously been soft-generated.
     */
    private void finishStaircaseGeneration() {
        generateUpwardsStaircases();
        makeStaircasesAccessible();
    }

    /**
     * This method generates upwards staircases in the current chunk based on attributes of the chunk above it.
     * For every downwards staircase in the chunk above the current chunk,
     * an upwards staircase will be generated at that exact coordinate.
     */
    private void generateUpwardsStaircases() {
        Coordinate aboveCoordinate = new Coordinate(currentChunk.getCoordinate().x(), currentChunk.getCoordinate().y(), currentChunk.getCoordinate().z() - 1);
        Chunk above = game.getWorld().getChunk(aboveCoordinate);

        List<Tile> aboveStairs = Arrays.stream(above.getTiles())
                .flatMap(Stream::of)
                .filter(tile -> tile != null && tile.getType() == TileType.STAIRS_DOWN)
                .toList();
        aboveStairs.forEach(stair -> generateUpwardsStaircase(stair.getCoordinate()));
    }

    /**
     * This method generates an upwards staircase in the current chunk at a certain coordinate.
     *
     * @param coordinate The position where the staircase should be generated.
     */
    private void generateUpwardsStaircase(Coordinate coordinate) {
        currentChunk.addStructure(new Structure(coordinate, StructureType.DUNGEON_STAIRS_UP));
        currentChunk.setTile(initialCurrentChunk.getTile(coordinate) == null ?
                new Tile(TileType.STAIRS_UP, coordinate)
                : initialCurrentChunk.getTile(coordinate));
    }

    /**
     * This method loops over every staircase tile in the current chunk to ensure that every staircase is accessible.
     * Staircases that aren't accessible are made accessible by generating corridors.
     */
    private void makeStaircasesAccessible() {
        Arrays.stream(currentChunk.getTiles())
                .flatMap(Stream::of)
                .filter(tile -> tile != null && (tile.getType() == TileType.STAIRS_DOWN || tile.getType() == TileType.STAIRS_UP))
                .toList()
                .forEach(stair -> {
                    if (!isAccessible(stair))
                        connect(stair.getCoordinate(), findNearestPassablePosition(currentChunk.getTiles(), stair.getCoordinate()));
                    currentChunk.setTile(stair);
                });
    }

    /**
     * This method determines the location of the downwards staircase by picking a random position in the current
     * chunk that lies within the offset.
     */
    private void generateDownwardsStaircase() {
        Coordinate coordinate = new Coordinate(random.nextInt(OFFSET, CHUNK_WIDTH - OFFSET), random.nextInt(OFFSET, CHUNK_HEIGHT - OFFSET));
        currentChunk.addStructure(new Structure(coordinate, StructureType.DUNGEON_STAIRS_DOWN));
        currentChunk.setTile(initialCurrentChunk.getTile(coordinate) == null ?
                new Tile(TileType.STAIRS_DOWN, coordinate)
                : initialCurrentChunk.getTile(coordinate));
    }

    /**
     * This method determines whether a tile has at least one passable tile around it within its own chunk.
     *
     * @param tile The tile of which the accessibility should be evaluated.
     * @return True if the tile has at least one passable tile around it within its own chunk.
     */
    private boolean isAccessible(Tile tile) {
        int x = tile.getCoordinate().x();
        int y = tile.getCoordinate().y();
        int z = tile.getCoordinate().z();
        return chunkAsRoom.contains(new Coordinate(x + 1, y, z)) && currentChunk.getTile(new Coordinate(x + 1, y, z)).isPassable()
                || chunkAsRoom.contains(new Coordinate(x - 1, y, z)) && currentChunk.getTile(new Coordinate(x - 1, y, z)).isPassable()
                || chunkAsRoom.contains(new Coordinate(x, y + 1, z)) && currentChunk.getTile(new Coordinate(x, y + 1, z)).isPassable()
                || chunkAsRoom.contains(new Coordinate(x, y - 1, z)) && currentChunk.getTile(new Coordinate(x, y - 1, z)).isPassable();
    }

    /**
     * This method generates dungeon rooms for every leaf node in a BSP tree.
     * Dungeon rooms are generated within the current chunk by determining their
     * measurements based on the boundingBox that is available for their leaf node.
     * The measurements of the actual dungeon room are stored in each leaf node so
     * that they can be used to connect rooms later on.
     *
     * @param boundingBoxTree A tree of the bounding boxes that should end up containing the actual dungeon rooms.
     */
    protected void generateDungeonRooms(Tree<Room, Room> boundingBoxTree) {
        boundingBoxTree.getLeafNodes().forEach(leafNode -> {
            Room boundingBox = leafNode.getValue();
            Room dungeonRoom = determineDungeonRoom(boundingBox);
            leafNode.setLeafValue(dungeonRoom);
            generateDungeonRoom(boundingBox, dungeonRoom);
        });
    }

    /**
     * This method generates a dungeon room by setting every tile that lies within a certain bounding box.
     * Every tile that lies in the bounding box and the dungeon room is set to be a floor tile.
     * Every tile that only lies within the bounding box is set to be a wall tile.
     *
     * @param boundingBox The bounding box in which the dungeon room will be contained.
     * @param dungeonRoom The measurements that have been determined for the dungeon room.
     */
    protected void generateDungeonRoom(Room boundingBox, Room dungeonRoom) {
        for (int x = boundingBox.getX(); x < boundingBox.getXBoundary(); x++) {
            for (int y = boundingBox.getY(); y < boundingBox.getYBoundary(); y++) {
                Coordinate coordinate = new Coordinate(x, y);
                if (dungeonRoom.contains(coordinate)) {
                    currentChunk.setTile(initialCurrentChunk.getTile(coordinate) == null ?
                            new Tile(TileType.DUNGEON_FLOOR, coordinate)
                            : initialCurrentChunk.getTile(coordinate));
                } else {
                    currentChunk.setTile(initialCurrentChunk.getTile(coordinate) == null ?
                            new Tile(TileType.DUNGEON_WALL, coordinate)
                            : initialCurrentChunk.getTile(coordinate));
                }
            }
        }
    }

    /**
     * This method overwrites tiles to ensure that every dungeon room within the chunk that is
     * stored in the instance of BSPDungeonGenerator that this method was called upon is accessible.
     *
     * @param boundingBoxTree A tree of bounding boxes that contain dungeon rooms.
     */
    protected void connectDungeonRooms(Tree<Room, Room> boundingBoxTree) {
        connectChildren(boundingBoxTree.getRoot());
    }

    /**
     * This method overwrites tiles to ensure that the children of a room node are connected.
     *
     * @param node The room node of which the children should be connected.
     */
    protected void connectChildren(Node<Room, Room> node) {
        if (node instanceof ParentNode<Room, Room> parent) {
            connectChildren(parent.getLeft());
            connectChildren(parent.getRight());
            if (parent.getLeft() != null && parent.getRight() != null) {
                connect(getRandomDungeonFloorTilePosition(parent.getLeft().getValue()),
                        getRandomDungeonFloorTilePosition(parent.getRight().getValue()));
            }
        }
    }

    /**
     * This method overwrites tiles to ensure that 2 positions are connected.
     *
     * @param left  The starting point.
     * @param right The position that should be connected with the starting point.
     */
    protected void connect(Coordinate left, Coordinate right) {
        if (left.equals(right)) return;
        if (Math.abs(left.x() - right.x()) >= Math.abs(left.y() - right.y())) {
            connectX(left.x(), right.x(), left.y());
            connectY(left.y(), right.y(), right.x());
        } else {
            connectY(left.y(), right.y(), left.x());
            connectX(left.x(), right.x(), right.y());
        }
    }

    /**
     * This method overwrites tiles to ensure that 2 positions are connected horizontally.
     *
     * @param leftX  The x position that represents the starting point.
     * @param rightX The x position that should be connected with the starting point.
     * @param y      The y position where the connection should be established.
     */
    protected void connectX(int leftX, int rightX, int y) {
        int x = leftX;
        while (x != rightX) {

            currentChunk.setTile(initialCurrentChunk.getTile(new Coordinate(x, y)) == null ?
                    new Tile(TileType.DUNGEON_FLOOR, new Coordinate(x, y))
                    : initialCurrentChunk.getTile(new Coordinate(x, y)));
            x = approachTargetValue(x, rightX);
        }

        currentChunk.setTile(initialCurrentChunk.getTile(new Coordinate(x, y)) == null ?
                new Tile(TileType.DUNGEON_FLOOR, new Coordinate(x, y))
                : initialCurrentChunk.getTile(new Coordinate(x, y)));
    }

    /**
     * This method overwrites tiles to ensure that 2 positions are connected vertically.
     *
     * @param leftY  The y position that represents the starting point.
     * @param rightY The y position that should be connected with the starting point.
     * @param x      The x position where the connection should be established.
     */
    protected void connectY(int leftY, int rightY, int x) {
        int y = leftY;
        while (y != rightY) {
            currentChunk.setTile(initialCurrentChunk.getTile(new Coordinate(x, y)) == null ?
                    new Tile(TileType.DUNGEON_FLOOR, new Coordinate(x, y))
                    : initialCurrentChunk.getTile(new Coordinate(x, y)));
            y = approachTargetValue(y, rightY);
        }
        currentChunk.setTile(initialCurrentChunk.getTile(new Coordinate(x, y)) == null ?
                new Tile(TileType.DUNGEON_FLOOR, new Coordinate(x, y))
                : initialCurrentChunk.getTile(new Coordinate(x, y)));
    }

    /**
     * This method shifts a certain value towards a target value. When the value and the target are equal, this method
     * will return that value.
     *
     * @param value  The value that should be shifted.
     * @param target The value that the original value should be shifted towards.
     */
    protected int approachTargetValue(int value, int target) {
        if (value == target) return value;
        return value > target ? value - 1 : value + 1;
    }

    /**
     * This method returns a position that represents a random passable location in a bounding box. This method assumes
     * that the tiles of the chunk stored in this instance of BSPDungeonGenerator have been set.
     *
     * @param boundingBox The bounding box from which a random passable location should be selected.
     * @return A random passable position that is contained within the boundingBox that was provided.
     */
    protected Coordinate getRandomDungeonFloorTilePosition(Room boundingBox) {
        List<Coordinate> passableTiles = new ArrayList<>();
        for (int x = boundingBox.getX(); x < boundingBox.getXBoundary(); x++) {
            for (int y = boundingBox.getY(); y < boundingBox.getYBoundary(); y++) {
                if (currentChunk.getTile(new Coordinate(x, y)).getType() == TileType.DUNGEON_FLOOR) {
                    passableTiles.add(new Coordinate(x, y));
                }
            }
        }

        return passableTiles.get(random.nextInt(0, passableTiles.size()));
    }

    /**
     * This method finds the nearest passable position to a certain origin position within the current chunk.
     *
     * @param tiles  A 2D array that contains the tiles of the current chunk.
     * @param origin The starting position when looking for close passable positions.
     * @return A coordinate that represents the nearest passable position within the current chunk.
     */
    public Coordinate findNearestPassablePosition(Tile[][] tiles, Coordinate origin) {
        Coordinate nearestPassablePosition = null;
        int minDistance = Integer.MAX_VALUE;

        for (int y = 0; y < CHUNK_HEIGHT; y++) {
            for (int x = 0; x < CHUNK_WIDTH; x++) {
                if (tiles[y][x].isPassable() && !(y == origin.y() && x == origin.x()) && tiles[y][x].getType() == TileType.DUNGEON_FLOOR) {
                    int distance = calculateDistance(origin, new Coordinate(x, y));

                    if (distance < minDistance) {
                        minDistance = distance;
                        nearestPassablePosition = tiles[y][x].getCoordinate();
                    }
                }
            }
        }

        if (nearestPassablePosition == null)
            throw new IllegalStateException("The current chunk does not contain any passable tiles.");
        return nearestPassablePosition;
    }

    /**
     * This method calculates the distance between 2 coordinates.
     *
     * @param left  The left position.
     * @param right The right position.
     * @return An integer that represents the distance between the 2 coordinates that were provided.
     */
    private int calculateDistance(Coordinate left, Coordinate right) {
        return Math.abs(left.y() - right.y()) + Math.abs(left.x() - right.x());
    }

    /**
     * This method generates a random dungeon room that fits within a provided bounding box and an optional offset.
     *
     * @param boundingBox The bounding box in which the dungeon room should be contained.
     * @return A randomly generated dungeon room.
     */
    protected Room determineDungeonRoom(Room boundingBox) {
        int xOrigin = boundingBox.getX() + OFFSET;
        int xBound = boundingBox.getXBoundary() - OFFSET - MIN_ROOM_WIDTH;
        int x = random.nextInt(xOrigin, xBound + 1);
        int yOrigin = boundingBox.getY() + OFFSET;
        int yBound = boundingBox.getYBoundary() - OFFSET - MIN_ROOM_HEIGHT;
        int y = random.nextInt(yOrigin, yBound + 1);
        int width = random.nextInt(MIN_ROOM_WIDTH, boundingBox.getXBoundary() - OFFSET - x + 1);
        int height = random.nextInt(MIN_ROOM_HEIGHT, boundingBox.getYBoundary() - OFFSET - y + 1);
        return new Room(new Coordinate(x, y), width, height);
    }
}
