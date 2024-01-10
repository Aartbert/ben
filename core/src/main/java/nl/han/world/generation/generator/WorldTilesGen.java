package nl.han.world.generation.generator;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import nl.han.shared.datastructures.world.Chunk;
import nl.han.shared.datastructures.world.Coordinate;
import nl.han.shared.datastructures.world.Tile;
import nl.han.shared.enums.BiomeType;
import nl.han.shared.enums.TileType;
import nl.han.shared.utils.Lottery;
import nl.han.world.generation.post_processing.ChunkChecker;

import static nl.han.shared.datastructures.world.Chunk.CHUNK_HEIGHT;
import static nl.han.shared.datastructures.world.Chunk.CHUNK_WIDTH;

/**
 * This class generates the tiles for the given chunk.
 */
@RequiredArgsConstructor
public class WorldTilesGen {

    private final GameWorldGen gameWorldGen;

    private final ChunkChecker chunkChecker = new ChunkChecker();

    /**
     * Generates the tiles for the given chunk.
     *
     * @param seed  The seed for the world.
     * @param chunk The chunk to generate the tiles for.
     */
    public void processChunk(long seed, Chunk chunk) {
        Chunk initialChunk = new Chunk(chunk);
        BiomeType biome = chunk.getBiomeType();

        Coordinate chunkCoordinate = chunk.getCoordinate();
        int chunkX = chunkCoordinate.x() * CHUNK_WIDTH;
        int chunkY = chunkCoordinate.y() * CHUNK_HEIGHT;

        Lottery<TileType> items = new Lottery<>(biome.getSpawnableTiles());

        for (int y = 0; y < CHUNK_HEIGHT; y++) {
            for (int x = 0; x < CHUNK_WIDTH; x++) {
                int greyValue = gameWorldGen.generatePerlinNoise(seed, new Coordinate(x + chunkX, y + chunkY), 6);
                double percentage = (greyValue / 255.0) * 100;

                TileType tileType = items.draw((int) percentage);

                chunk.setTile(initialChunk.getTile(new Coordinate(x, y)) == null ?
                        new Tile(tileType, new Coordinate(x, y))
                        : initialChunk.getTile(new Coordinate(x, y)));
            }
        }
        chunkChecker.checkChunk(chunk);
    }
}
