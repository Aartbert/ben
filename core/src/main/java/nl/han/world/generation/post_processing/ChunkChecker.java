package nl.han.world.generation.post_processing;

import lombok.Getter;
import lombok.extern.java.Log;
import nl.han.shared.datastructures.world.Chunk;
import nl.han.shared.datastructures.world.Coordinate;
import nl.han.shared.datastructures.world.Tile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nl.han.shared.datastructures.world.Chunk.CHUNK_HEIGHT;
import static nl.han.shared.datastructures.world.Chunk.CHUNK_WIDTH;

/**
 * This class checks the chunk for unreachable tiles. It uses a depth first algoritm to check every tile that is passable.
 * These tiles are stored in chunks. The chunks are reachable or unreachable. If they are not reachable they will be updated to the unpassable tile of the biome.
 *
 * @author Julian van Kuijk
 */
@Log
public class ChunkChecker {

    @Getter
    private Map<Coordinate, Boolean> visited;
    private Tile impassableTile;
    @Getter
    private List<Cluster> clusterList;
    @Getter
    private List<Cluster> clustersFilled;

    /**
     * The ChunkChecker constructor instantiates the hashmap and arraylist.
     *
     * @author Julian van Kuijk
     */
    public ChunkChecker() {
        this.visited = new HashMap<>();
        clusterList = new ArrayList<>();
        clustersFilled = new ArrayList<>();
    }

    /**
     * checkChunk calls the submethods:
     * <ul>mapTiles()</ul>
     * <ul>searchCluster()</ul>
     * <ul>fillCluster()</ul>
     *
     * @param chunk the to be checked chunk.
     * @author Julian van Kuijk
     */
    public void checkChunk(Chunk chunk) {
        mapTiles(chunk);
        searchCluster(chunk);
        fillCluster(chunk);
    }

    /**
     * MapTiles() collects all tiles that should be processed.
     *
     * @author Julian van Kuijk
     */
    private void mapTiles(Chunk chunk) {
        for (int y = 0; y < CHUNK_HEIGHT; y++) {
            for (int x = 0; x < CHUNK_WIDTH; x++) {
                Coordinate coordinate = new Coordinate(x, y);
                Tile tile = chunk.getTile(coordinate);

                if (!tile.isPassable()) {
                    impassableTile = tile;
                    continue;
                }

                visited.put(coordinate, false);
            }
        }
    }

    /**
     * SearchCluster() searches the collected tiles from mapTiles().
     * And collects the tiles in clusters. The clusters are collected through a depthFirstSearch().
     * The tiles are reachable if they reach the border.
     *
     * @author Julian van Kuijk
     */
    private void searchCluster(Chunk chunk) {
        for (Map.Entry<Coordinate, Boolean> entry : visited.entrySet()){
            if(Boolean.FALSE.equals(entry.getValue())){
                Cluster cluster = new Cluster();
                depthFirstSearch(entry.getKey(), cluster, chunk);
                clusterList.add(cluster);
            }
        }
    }

    /**
     * FillCluster() uses the collected clusters from searchCluster().
     * It uses the impassable TileType from the biome of the chunk, and updates the tile in the chunk.
     *
     * @author Julian van Kuijk
     * */
    private void fillCluster(Chunk chunk) {
        for (Cluster cluster : clusterList) {
            if (!cluster.isReachable()) {
                cluster.fillCluster(chunk, impassableTile.getType());
                clustersFilled.add(cluster);
                log.info("Filled a cluster: " + cluster);
            }
        }
    }

    /**
     * depthFirstSearch() is a recursive function, which checks if a tile may be checked.
     * If the Tile is valid: add it to the cluster. If the Tile is valid and reaches the border. The cluster is reachable...
     * Then check the neighbours of the coordinate and repeat the process.
     *
     * @author Julian van Kuijk
     */
    private void depthFirstSearch(Coordinate coord, Cluster cluster, Chunk chunk) {
        if (coord.x() < 0 || coord.x() >= CHUNK_WIDTH ||
                coord.y() < 0 || coord.y() >= CHUNK_HEIGHT) {
            log.finest("Tile is out of bound.");
            return;
        }

        if (!chunk.getTile(coord).isPassable()) {
            log.finest("Tile is not passable.");
            return;
        }

        if (Boolean.TRUE.equals(visited.get(coord))) {
            log.finest("Tile is already visited.");
            return;
        }

        if (coord.x() == 0 || coord.x() == CHUNK_WIDTH - 1 ||
                coord.y() == 0 || coord.y() == CHUNK_HEIGHT - 1) {
            cluster.setReachable(true);
        }

        visited.put(coord, true);
        cluster.addCoordinate(coord);

        int amountOfNeighbour = 4;
        int[] dx = {0, 0, -1, 1};
        int[] dy = {-1, 1, 0, 0};

        for (int i = 0; i < amountOfNeighbour; i++) {
            int newX = coord.x() + dx[i];
            int newY = coord.y() + dy[i];
            Coordinate newCoordinate = new Coordinate(newX, newY);

            depthFirstSearch(newCoordinate, cluster, chunk);
        }
    }
}
