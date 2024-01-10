package nl.han;

import nl.han.shared.datastructures.game.Game;
import nl.han.shared.datastructures.world.*;
import nl.han.world.generation.IWorldGeneration;
import nl.han.world.generation.WorldGeneration;
import nl.han.world.population.spawner.ISpawner;
import nl.han.world.population.spawner.ItemSpawner;
import nl.han.world.population.spawner.MonsterSpawner;
import nl.han.world.population.structure_generation.IDungeonGenerator;
import nl.han.world.population.structure_generation.bsp_dungeon_generation.BSPDungeonGenerator;

/**
 * The WorldManager class is responsible for the generation and management <br/>
 * of worlds in the game. Its main role is to interface between the world
 * objects in the game world and the IWorldGeneration objects that facilitates
 * the generation of these worlds.
 *
 * @author Jordan Geurtsen
 * @see IWorldGeneration
 * @see World
 */
public class WorldManager {
    private final IWorldGeneration worldGeneration;
    private final IDungeonGenerator dungeonGeneration;
    private final ISpawner itemSpawner;
    private final ISpawner monsterSpawner;
    private final World world;

    /**
     * Constructs a WorldManager object with the specified world.
     *
     * @param game The game that the WorldManager object will be handling.
     * @author Jordan Geurtsen
     */
    public  WorldManager(Game game) {
        this.worldGeneration = new WorldGeneration(game);
        this.dungeonGeneration = new BSPDungeonGenerator(game);
        this.itemSpawner = new ItemSpawner(game.getWorld().getConfig());
        this.monsterSpawner = new MonsterSpawner(game);
        this.world = game.getWorld();
    }

    /**
     * This method is responsible for loading chunks. When the chunk for the provided coordinates has already been
     * generated previously, this chunk will be loaded. When the chunk for the provided coordinates has never been
     * generated before, every chunk around it will be soft-generated and the chunk itself will be generated.
     *
     * @param chunkCoordinate The coordinate of the chunk that is being requested.
     * @return A chunk object that represents the chunk that has been loaded.
     * @author Jordan Geurtsen & Lucas van Steveninck
     * @see Chunk
     * @see World
     */
    public Chunk loadChunk(Coordinate chunkCoordinate) {
        Chunk chunk = world.getChunk(chunkCoordinate);
        if (chunk != null && chunk.isFullyGenerated()) return chunk;

        if (world.getChunk(new Coordinate(chunkCoordinate.x() + 1, chunkCoordinate.y(), chunkCoordinate.z())) == null) {
            softGenerateChunk(new Coordinate(chunkCoordinate.x() + 1, chunkCoordinate.y(), chunkCoordinate.z()));
        }
        if (world.getChunk(new Coordinate(chunkCoordinate.x() - 1, chunkCoordinate.y(), chunkCoordinate.z())) == null) {
            softGenerateChunk(new Coordinate(chunkCoordinate.x() - 1, chunkCoordinate.y(), chunkCoordinate.z()));
        }
        if (world.getChunk(new Coordinate(chunkCoordinate.x(), chunkCoordinate.y() + 1, chunkCoordinate.z())) == null) {
            softGenerateChunk(new Coordinate(chunkCoordinate.x(), chunkCoordinate.y() + 1, chunkCoordinate.z()));
        }
        if (world.getChunk(new Coordinate(chunkCoordinate.x(), chunkCoordinate.y() - 1, chunkCoordinate.z())) == null) {
            softGenerateChunk(new Coordinate(chunkCoordinate.x(), chunkCoordinate.y() - 1, chunkCoordinate.z()));
        }
        if (world.getChunk(new Coordinate(chunkCoordinate.x(), chunkCoordinate.y(), chunkCoordinate.z() + 1)) == null) {
            softGenerateChunk(new Coordinate(chunkCoordinate.x(), chunkCoordinate.y(), chunkCoordinate.z() + 1));
        }
        if (chunkCoordinate.z() > 0 && world.getChunk(new Coordinate(chunkCoordinate.x(), chunkCoordinate.y(), chunkCoordinate.z() - 1)) == null) {
            softGenerateChunk(new Coordinate(chunkCoordinate.x(), chunkCoordinate.y(), chunkCoordinate.z() - 1));
        }

        return generateChunk(chunkCoordinate);
    }

    /**
     * This method is used to soft-generate a chunk at certain coordinates.
     * Chunks at a Z-coordinate of 0 will be soft-generated using worldGeneration,
     * while other chunks are soft-generated using dungeonGeneration.
     *
     * @param chunkCoordinate The coordinate of the chunk that should be soft-generated.
     * @author Lucas van Steveninck
     * @see IWorldGeneration
     * @see IDungeonGenerator
     */
    public void softGenerateChunk(Coordinate chunkCoordinate) {
        if (chunkCoordinate.z() == 0) worldGeneration.softGenerateChunk(chunkCoordinate);
        else dungeonGeneration.softGenerateDungeonChunk(chunkCoordinate);
    }

    /**
     * This method is used to generate a chunk at certain coordinates.
     * Chunks at a Z-coordinate of 0 will be generated using worldGeneration,
     * while other chunks are generated using dungeonGeneration.
     *
     * @param chunkCoordinate The coordinate of the chunk that should be generated.
     * @author Lucas van Steveninck
     * @see IWorldGeneration
     * @see IDungeonGenerator
     */
    public Chunk generateChunk(Coordinate chunkCoordinate) {
        Chunk generatedChunk = chunkCoordinate.z() == 0 ?
                worldGeneration.generateChunk(chunkCoordinate)
                : dungeonGeneration.generateDungeonChunk(chunkCoordinate);
        itemSpawner.execute(generatedChunk);
        monsterSpawner.execute(generatedChunk);
        return generatedChunk;
    }
}
