package nl.han.world.generation.post_processing;

import lombok.Getter;
import lombok.Setter;
import nl.han.shared.datastructures.world.Chunk;
import nl.han.shared.datastructures.world.Coordinate;
import nl.han.shared.enums.TileType;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that collects coordinates which are clustered together. It is used by ChunkChecker.
 * It contains a list of the coordinates and a boolean for when the cluster reaches the border.
 *
 * @author Julian van Kuijk
 */
@Getter
public class Cluster {
    @Setter
    private boolean reachable = false;
    private final List<Coordinate> coordinates = new ArrayList<>();

    /**
     * Adds a coordinate to a cluster
     * @param coordinate the coordinate to add to the cluster
     *
     * @author Julian van Kuijk
     */
    public void addCoordinate(Coordinate coordinate) {
        coordinates.add(coordinate);
    }

    /**
     * Initiates the process of filling up the cluster with a certain tileType.
     * @param chunk the chunk in which the cluster presides
     * @param tileType the type to which the cluster will be updated.
     *
     * @author Julian van Kuijk
     */
    public void fillCluster(Chunk chunk, TileType tileType){
        for(Coordinate coord : coordinates){
            chunk.updateTile(coord, tileType);
        }
    }

    @Override
    public String toString() {
        return "Cluster{" +
                "coordinates=" + coordinates +
                '}';
    }
}
