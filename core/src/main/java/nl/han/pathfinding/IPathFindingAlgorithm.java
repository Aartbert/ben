package nl.han.pathfinding;

import nl.han.pathfinding.exception.NoPathFoundException;
import nl.han.shared.datastructures.world.Chunk;
import nl.han.shared.datastructures.world.Tile;
import nl.han.shared.enums.Action;

import java.util.List;

/**
 * Interface for implementing pathfinding algorithms.
 *
 * @author Sven van Hoof
 */
public interface IPathFindingAlgorithm {
    /**
     * Finds a valid path from startNode to endNode in the given grid.
     *
     * @param chunk chunk to find a path in.
     * @param start tile the path should start at.
     * @param end   tile the path should end at.
     * @return list of actions that should be taken to go from the startNode to the
     *         endNode
     * @throws NoPathFoundException if no valid path is found.
     * @author Sven van Hoof, Vasil Verdouw
     */
    List<Action> findPath(Chunk chunk, Tile start, Tile end) throws NoPathFoundException;
}
