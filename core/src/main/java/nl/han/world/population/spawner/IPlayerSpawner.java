package nl.han.world.population.spawner;

import nl.han.shared.datastructures.creature.Player;
import nl.han.shared.datastructures.world.Chunk;

import java.util.List;

/**
 * An interface to populate the world with players. Can be implemented to create different games.
 * E.g. spawning players for Capture the Flag, or for Team death match.
 */
public interface IPlayerSpawner {

    /**
     * Execute the spawning process.
     * @param chunk the chunk to be populated
     * @param players the list of players to be spawned
     */
    void execute(Chunk chunk, List<Player> players);
}
