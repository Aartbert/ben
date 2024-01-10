package nl.han.shared.datastructures.world;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import nl.han.shared.datastructures.Config;
import nl.han.shared.datastructures.WorldRulesBuilder;

import java.util.List;

/**
 * Represents the world in a game.
 *
 * @author Jordan Geurtsen
 * @see Chunk
 * @see Config
 */
@Getter
@Setter
@RequiredArgsConstructor
public class World {
    public final long seed;
    private final List<Chunk> chunks;
    private final Config config;

    public World(Config config, List<Chunk> chunks) {
        this(WorldRulesBuilder.convertToWorldRules(config).getSeed(), chunks, config);
    }

    /**
     * This method is used for adding a new chunk to the list of chunks.<br/>
     * Chunks represent a specific portion of some larger entity.
     *
     * @param chunk represents the chunk to be added to the list of chunks
     * @author Jordan Geurtsen
     * @see Chunk
     */
    public void addChunk(Chunk chunk) {
        chunks.add(chunk);
    }

    /**
     * This method is used to retrieve chunks at specific coordinates.
     * @param chunkCoordinate The coordinates of the chunk that should be retrieved.
     * @author Lucas van Steveninck
     * @see Chunk
     */
    public Chunk getChunk(Coordinate chunkCoordinate) {
        return chunks.stream()
                .filter(chunk -> chunk.getCoordinate().equals(chunkCoordinate))
                .findFirst().orElse(null);
    }
}
