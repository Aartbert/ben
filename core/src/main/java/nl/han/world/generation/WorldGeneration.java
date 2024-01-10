package nl.han.world.generation;

import nl.han.shared.datastructures.game.Game;
import nl.han.shared.datastructures.world.Chunk;
import nl.han.shared.datastructures.world.Coordinate;
import nl.han.shared.datastructures.world.Tile;
import nl.han.shared.datastructures.world.World;
import nl.han.world.generation.generator.GameWorldGen;

import static nl.han.shared.datastructures.world.Chunk.CHUNK_HEIGHT;
import static nl.han.shared.datastructures.world.Chunk.CHUNK_WIDTH;

/**
 * This class represents the generation of the world.
 */
public class WorldGeneration implements IWorldGeneration {
    private final GameWorldGen gameWorldGen;
    private final Game game;

    public WorldGeneration(Game game) {
        this.game = game;
        this.gameWorldGen = new GameWorldGen(game);

    }

    /**
     * @inheritDoc
     */
    @Override
    public Chunk generateChunk(Coordinate chunkCoordinate) {
        Chunk chunk;
        chunk = game.getWorld().getChunk(chunkCoordinate);
        if (chunk == null) chunk = softGenerateChunk(chunkCoordinate);
        gameWorldGen.gameWorldGen(chunk);
        chunk.setFullyGenerated(true);
        return chunk;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Chunk softGenerateChunk(Coordinate chunkCoordinate) {
        Chunk chunk;
        chunk = game.getWorld().getChunk(chunkCoordinate);
        if (chunk == null) chunk = new Chunk(new Tile[CHUNK_HEIGHT][CHUNK_WIDTH], chunkCoordinate, game.getWorld().getSeed(), game.getId());
        gameWorldGen.softGameWorldGen(chunk);
        game.getWorld().addChunk(chunk);
        return chunk;
    }
}
