package nl.han.world.population.spawner;

import lombok.Setter;
import nl.han.shared.datastructures.creature.Player;
import nl.han.shared.datastructures.world.Chunk;
import nl.han.shared.datastructures.world.Coordinate;
import nl.han.shared.datastructures.world.Tile;
import nl.han.shared.utils.random.COCRandom;
import nl.han.shared.utils.random.ICOCRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static nl.han.shared.datastructures.world.Chunk.CHUNK_HEIGHT;
import static nl.han.shared.datastructures.world.Chunk.CHUNK_WIDTH;

/**
 * A class responsible for spawning players on a chunk within specified constraints.
 * Implements the IStrategy interface for executing spawners.
 */
@Setter
public class PlayerSpawner implements IPlayerSpawner {
    private ICOCRandom random = new COCRandom();
    private List<Coordinate> allCoordinates;
    private int startCoordinate = 0;
    private List<Player> playersToSpawn;

    /**
     * Executes spawning players on a given chunk with a range between every player,
     * including setting properties and spawning players.
     *
     * @param chunk The chunk on which the players are spawned.
     *
     * @author Brett Hammer
     */
    @Override
    public void execute(Chunk chunk, List<Player> players) {
        playersToSpawn = players;
        allCoordinates = createCoordinateListOfChunk(chunk);

        int rangeBetweenPlayer = 20;
        if (!players.isEmpty()) spawnAllPlayers(rangeBetweenPlayer, chunk);
    }

    /**
     * Spawns players within a range in the given chunk,
     * avoiding spawning players on items and impassable tiles.
     *
     * @param rangeBetween The range within which players should be spawned relative to each other.
     * @param chunk The chunk to spawn the players in.
     *
     * @author Brett Hammer
     */
    private void spawnAllPlayers(int rangeBetween, Chunk chunk) {
        List<Coordinate> visited = new ArrayList<>();
        boolean breakForEach = false;

        for (Player player : playersToSpawn) {
            if (player.getCoordinate() != null) continue;
            Coordinate coords = getRandomCoordinatesNotIn(allCoordinates, visited);
            while (shouldContinueSpawning(coords, rangeBetween, chunk)) {
                coords = getRandomCoordinatesNotIn(allCoordinates, visited);

                if (coords == null || visited.size() >= CHUNK_WIDTH * CHUNK_HEIGHT) {
                    breakForEach = true;
                    break;
                }
                visited.add(coords);
            }

            if (breakForEach) break;
            spawnPlayer(player, coords, chunk);
        }
    }

    /**
     * Checks whether spawning should continue at the specified coordinates within the given chunk.
     * Spawning continues if any of the following conditions are met: <br>
     * 1. Another player is within the specified range of the target coordinates. <br>
     * 2. The tile at the specified coordinates is an instance of {@code ImpassableTile}. <br>
     * 3. The tile at the specified coordinates has non-empty items.
     *
     * @param coords The coordinates to check for spawning.
     * @param rangeBetween The range within which players should be spawned relative to each other.
     * @param chunk The chunk in which the spawning is being considered.
     * @return {@code true} if spawning should continue, {@code false} otherwise.
     *
     * @author Brett Hammer
     */
    private boolean shouldContinueSpawning(Coordinate coords, int rangeBetween, Chunk chunk) {
        return playerInRange(coords, rangeBetween) ||
                !(chunk.getTile(coords).isPassable()) ||
                !chunk.getTile(coords).getItems().isEmpty();
    }

    /**
     * Checks if any players are within the specified range of the target coordinates.
     *
     * @param targetCoordinate the coordinate of the target.
     * @param range The range within which players are considered to be in range.
     *
     * @author Brett Hammer
     */
    protected boolean playerInRange(Coordinate targetCoordinate, int range) {
        int leftSide = clamp(targetCoordinate.x() - range, startCoordinate, CHUNK_WIDTH);
        int rightSide = clamp(targetCoordinate.x() + range, startCoordinate, CHUNK_WIDTH);
        int upSide = clamp(targetCoordinate.y() - range, startCoordinate, CHUNK_HEIGHT);
        int downSide = clamp(targetCoordinate.y() + range, startCoordinate, CHUNK_HEIGHT);

        for (Player player : playersToSpawn) {
            if (player.getCoordinate() ==  null) continue;
            if (
                    player.getCoordinate().x() >= leftSide &&
                    player.getCoordinate().x() <= rightSide &&
                    player.getCoordinate().y() >= upSide &&
                    player.getCoordinate().y() <= downSide
            ) {
                return true;
            }
        }

        return false;
    }

    /**
     * Retrieves a random coordinate from the provided list, excluding those in the 'notIn' list.
     *
     * @param all The list of all coordinates to choose from.
     * @param notIn The list of coordinates to exclude from the selection.
     * @return A randomly selected coordinate or null if the selection is not possible.
     *
     * @author Brett Hammer
     */
    protected Coordinate getRandomCoordinatesNotIn(List<Coordinate> all, List<Coordinate> notIn) {
        List<Coordinate> chooseList = all;
        if (!notIn.isEmpty()) chooseList = extractList(all, notIn);
        if (chooseList.isEmpty()) return null;
        return chooseList.get(random.nextInt(0, chooseList.size()));
    }

    /**
     * Extracts a list of coordinates from 'all', excluding those that are in the 'extract' list.
     *
     * @param all The list of all coordinates.
     * @param extract The list of coordinates to be excluded from all.
     * @return The extracted list of coordinates.
     *
     * @author Brett Hammer
     */
    protected List<Coordinate> extractList(List<Coordinate> all, List<Coordinate> extract) {
        List<Coordinate> extractedList = new ArrayList<>();

        for (Coordinate c : all) {
            if (!extract.contains(c)) {
                extractedList.add(c);
            }
        }

        return extractedList;
    }

    /**
     * Creates a list of coordinates representing all possible positions
     * in a chunk with the given width and height.
     *
     * @return A list of coordinates for all coordinates in the chunk.
     *
     * @author Brett Hammer
     */
    protected List<Coordinate> createCoordinateListOfChunk(Chunk chunk) {
        return Stream.of(chunk.getTiles())
                .flatMap(Stream::of)
                .map(Tile::getCoordinate)
                .toList();
    }

    /**
     * Clamps the given value to be within the specified minimum and maximum range.
     *
     * @param value The value to be clamped.
     * @param min The minimum allowed value.
     * @param max The maximum allowed value.
     * @return The clamped value.
     *
     * @author Brett Hammer
     */
    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Sets the coordinates for spawning the given player.
     *
     * @param player The player to be spawned.
     * @param coords The X- and Y-Coordinate for spawning.
     * @param chunk The chunk where the player has to spawn on.
     *
     * @author Brett Hammer
     */
    protected void spawnPlayer(Player player, Coordinate coords, Chunk chunk) {
        player.setCoordinate(coords);
        player.setChunk(chunk);
    }
}
