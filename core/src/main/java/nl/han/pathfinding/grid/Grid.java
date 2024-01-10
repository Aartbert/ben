package nl.han.pathfinding.grid;

import lombok.Getter;
import nl.han.shared.datastructures.world.Chunk;
import nl.han.shared.datastructures.world.Coordinate;

/**
 * Represents a grid with nodes in them.
 */
@Getter
public class Grid {
    private final Node[][] nodes;

    /**
     * Create a grid from a chunk
     *
     * @param chunk the chunk
     */
    public Grid(Chunk chunk) {
        nodes = new Node[Chunk.CHUNK_HEIGHT][Chunk.CHUNK_WIDTH];
        for (int y = 0; y < Chunk.CHUNK_HEIGHT; y++) {
            for (int x = 0; x < Chunk.CHUNK_WIDTH; x++) {
                nodes[y][x] = new Node(chunk.getTile(new Coordinate(x, y)));
            }
        }
    }
}
