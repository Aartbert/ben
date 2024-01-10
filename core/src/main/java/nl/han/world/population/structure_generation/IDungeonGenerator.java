package nl.han.world.population.structure_generation;

import nl.han.shared.datastructures.world.Chunk;
import nl.han.shared.datastructures.world.Coordinate;

/**
 * This class is responsible for generating dungeon chunks within a structure.
 */
public interface IDungeonGenerator {

    /**
     * Fully generates a chunk.
     *
     * @param chunkCoordinate The coordinate of the chunk.
     * @return The generated chunk.
     */
    Chunk generateDungeonChunk(Coordinate chunkCoordinate);

    /**
     * Soft-generates a chunk. When soft-generating a chunk, characteristics that other chunks might need to know about
     * this chunk will be determined. Everything else will only be determined when the chunk is fully generated
     *
     * @param chunkCoordinate The coordinate of the chunk.
     * @return The generated chunk.
     */
    Chunk softGenerateDungeonChunk(Coordinate chunkCoordinate);
}
