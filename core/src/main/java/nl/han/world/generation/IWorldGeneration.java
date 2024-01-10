package nl.han.world.generation;

import nl.han.shared.datastructures.world.Chunk;
import nl.han.shared.datastructures.world.Coordinate;

/**
 * This interface represents what we expect from other components.
 */
public interface IWorldGeneration {

    /**
     * Fully generates a chunk.
     *
     * @param chunkCoordinate The coordinate of the chunk.
     * @return The generated chunk.
     */
    Chunk generateChunk(Coordinate chunkCoordinate);

    /**
     * Soft-generates a chunk. When soft-generating a chunk, characteristics that other chunks might need to know about
     * this chunk will be determined. Everything else will only be determined when the chunk is fully generated
     *
     * @param chunkCoordinate The coordinate of the chunk.
     * @return The generated chunk.
     */
    Chunk softGenerateChunk(Coordinate chunkCoordinate);
}
