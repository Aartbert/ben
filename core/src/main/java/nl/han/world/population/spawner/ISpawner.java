package nl.han.world.population.spawner;

import nl.han.shared.datastructures.world.Chunk;

/**
 * An interface to populate chunks. Contains the function execute. A chunk is given and updated.
 * Can be used for spawning things as: Items and Monsters.
 */
public interface ISpawner {
    /**
     * Start the spawning process.
     * @param chunk a chunk to populate.
     */
    void execute(Chunk chunk);
}
