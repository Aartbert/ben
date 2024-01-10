package nl.han.world.population;

import nl.han.shared.datastructures.world.Chunk;
import nl.han.shared.datastructures.world.Coordinate;
import nl.han.shared.datastructures.world.Tile;
import nl.han.shared.enums.TileType;
import nl.han.world.population.structure_generation.bsp_dungeon_generation.binary_space_partitioning.Room;

import java.util.List;
import java.util.UUID;

import static nl.han.shared.datastructures.world.Chunk.CHUNK_HEIGHT;
import static nl.han.shared.datastructures.world.Chunk.CHUNK_WIDTH;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * This class is responsible for test util logic regarding BSP dungeon generation
 *
 * @author Lucas van Steveninck
 */
public class TestUtil {
    /**
     * Creates a room spy based on provided measurements. The spy that is created will behave as an actual instance
     * of Room upon being compared while mocking the behaviour of a Room.
     *
     * @param x The x position of the room spy that should be created.
     * @param y The y position of the room spy that should be created.
     * @param width The width of the room spy that should be created.
     * @param height The height of the room spy that should be created.
     * @return A room spy based on provided measurements.
     */
    public Room createRoomSpy(int x, int y, int width, int height) {
        Room room = new Room(new Coordinate(x, y), width, height);
        Room roomSpy = spy(room);
        when(roomSpy.getX()).thenReturn(x);
        when(roomSpy.getY()).thenReturn(y);
        when(roomSpy.getWidth()).thenReturn(width);
        when(roomSpy.getHeight()).thenReturn(height);
        when(roomSpy.getXBoundary()).thenReturn(x + width);
        when(roomSpy.getYBoundary()).thenReturn(y + height);
        when(roomSpy.splitHorizontally(anyInt())).thenAnswer(i -> List.of(createRoomSpy(x, y, width,(int) i.getArguments()[0] - y), createRoomSpy(x, (int) i.getArguments()[0], width, y + height - (int) i.getArguments()[0])));
        when(roomSpy.splitVertically(anyInt())).thenAnswer(i -> List.of(createRoomSpy(x, y, (int) i.getArguments()[0] - x, height), createRoomSpy((int) i.getArguments()[0], y, x + width - (int) i.getArguments()[0], height)));
        return roomSpy;
    }

    /**
     * Concatenates two arrays of primitive integers into a new array.
     * This method takes two integer arrays, 'left' and 'right', and concatenates them
     * into a new array. The resulting array will have a length equal to the sum of the
     * lengths of the input arrays. The elements from the 'left' array will appear first
     * in the result, followed by the elements from the 'right' array.
     *
     * @param left  The first array of primitive integers.
     * @param right The second array of primitive integers.
     * @return      A new array containing all the elements from 'left' followed by
     *              all the elements from 'right'.
     * @throws NullPointerException If either 'left' or 'right' is null.
     */
    public int[] concatenateArrays(int[] left, int[] right) {
        int length = left.length + right.length;
        int[] result = new int[length];
        System.arraycopy(left, 0, result, 0, left.length);
        System.arraycopy(right, 0, result, left.length, right.length);
        return result;
    }

    /**
     * This method creates a Chunk based on an array of chars. While creating the chunk, every
     * '.' will be represented by a DungeonFloorTile. Every other character will be represented
     * by a DungeonWallTile.
     *
     * @param chunkGrid  The array of chars that should be converted to a chunk.
     * @return A chunk that is a representation of the charArray that was provided.
     * @throws NullPointerException If charArray is null.
     */
    public Chunk createChunkFromCharArray(char[][] chunkGrid, UUID uuid) {
        Chunk chunk = new Chunk(new Tile[CHUNK_HEIGHT][CHUNK_WIDTH], new Coordinate(0, 0, 0), 0L, uuid);
        for (int y = 0; y < chunkGrid.length; y++) {
            for (int x = 0; x < chunkGrid[y].length; x++) {
                Coordinate coordinate = new Coordinate(x, y);

                switch(chunkGrid[y][x]) {
                    case '⤊' -> chunk.setTile(new Tile(TileType.STAIRS_UP, coordinate));
                    case '⤋' -> chunk.setTile(new Tile(TileType.STAIRS_DOWN, coordinate));
                    case '≈' -> chunk.setTile(new Tile(TileType.WATER, coordinate));
                    case '▒' -> chunk.setTile(new Tile(TileType.SAND, coordinate));
                    case '°' -> chunk.setTile(new Tile(TileType.DUNGEON_FLOOR, coordinate));
                    case '#' -> chunk.setTile(new Tile(TileType.DUNGEON_WALL, coordinate));
                    case '.' -> chunk.setTile(new Tile(TileType.GRASS, coordinate));
                }
            }
        }

        return chunk;
    }
}
