package nl.han.world.population.spawner;

import lombok.Getter;
import lombok.Setter;
import nl.han.shared.datastructures.WorldRules;
import nl.han.shared.datastructures.WorldRulesBuilder;
import nl.han.shared.datastructures.creature.Bot;
import nl.han.shared.datastructures.game.Game;
import nl.han.shared.datastructures.world.Chunk;
import nl.han.shared.datastructures.world.Coordinate;
import nl.han.shared.datastructures.world.Tile;
import nl.han.shared.enums.BotType;
import nl.han.shared.enums.TileType;
import nl.han.shared.utils.random.ICOCRandom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static nl.han.shared.datastructures.world.Chunk.CHUNK_HEIGHT;
import static nl.han.shared.datastructures.world.Chunk.CHUNK_WIDTH;

/**
 * Has all the logic for spawning monsters in the world.
 *
 * @author Fabian van Os
 */
@Getter
@Setter
public class MonsterSpawner implements ISpawner {
    private static final int MIN_IN_CLUSTER = 2;
    private static final int MAX_IN_CLUSTER = 6;
    private static final int RADIUS = 3;
    private final List<BotType> monsters = Arrays.stream(BotType.values()).toList();
    private final Game game;
    private final WorldRules.MonsterSpawnRules spawnRules;
    private ICOCRandom random;

    /**
     * Constructor to generate monsters with a seed.
     *
     * @param game the current game, so that you can eventually get all monsters.
     */
    public MonsterSpawner(Game game) {
        WorldRules rules = WorldRulesBuilder.convertToWorldRules(game.getWorld().getConfig());

        this.game = game;
        this.spawnRules = rules.getMonsterSpawnRules();
    }

    /**
     * Checks if there are monsters on a tile.
     *
     * @param monstersInChunk All the monsters that are currently on the chunk.
     * @param tile            The tile that will be checked for monsters.
     * @param output          a list of the tiles where no monsters are standing on.
     * @author Fabian van Os
     */
    private static void checkIfMonsterOnTile(List<Bot> monstersInChunk, Tile tile, List<Tile> output) {
        boolean noMatchingCoordinate = true;
        for (Bot monster : monstersInChunk) {
            Coordinate monsterCoordinate = monster.getCoordinate();
            if (monsterCoordinate.equals(tile.getCoordinate())) {
                noMatchingCoordinate = false;
                break;
            }
        }
        if (noMatchingCoordinate) {
            output.add(tile);
        }
    }

    /**
     * Calls setMonsters that sets the monsters on the tiles.
     *
     * @param chunk The chunk to generate the monsters for.
     * @author Fabian van Os
     */
    @Override
    public void execute(Chunk chunk) {
        random = chunk.getRandom();
        setMonsters(chunk);
    }

    /**
     * Places monsters on the different tiles in the chunk
     *
     * @param chunk The tile to where items are placed on.
     * @author Fabian van Os
     */
    public void setMonsters(Chunk chunk) {
        int toSpawn = random.nextInt(spawnRules.getMin(), spawnRules.getMax() + 1);
        while (toSpawn > 0) {
            int toSpawnInCluster = random.nextInt(Math.min(toSpawn, MIN_IN_CLUSTER), Math.min(toSpawn, MAX_IN_CLUSTER) + 1);

            placeMonsters(toSpawnInCluster, chunk);

            toSpawn -= toSpawnInCluster;
        }
    }

    /**
     * Places monsters in a cluster in the chunk.
     *
     * @param howMuchMonsters Says how many monsters can be placed in this cluster.
     * @param chunk           The chunk where monsters are placed on.
     * @author Fabian van Os
     */
    protected void placeMonsters(int howMuchMonsters, Chunk chunk) {
        BotType monsterType = monsters.get(random.nextInt(0, monsters.size()));
        Tile tile = generateMonster(monsterType, chunk);
        howMuchMonsters--;
        if (howMuchMonsters <= 0) return;

        generateAroundMonster(monsterType, tile, howMuchMonsters, chunk);
    }

    /**
     * Generates the other monsters based on the radius of the first monster of the cluster.
     *
     * @param monsterType     The type of the monster.
     * @param tile            The base tile where the other monsters could spawn.
     * @param howMuchMonsters Says how many monsters can be placed in this cluster.
     * @param chunk           The chunk where monsters are placed on.
     * @author Fabian van Os
     */
    protected void generateAroundMonster(BotType monsterType, Tile tile, int howMuchMonsters, Chunk chunk) {
        List<Tile> emptyTiles = getEmptyTilesFromChunk(chunk);
        List<Tile> possibleTiles;
        int xCoordinate = tile.getCoordinate().x();
        int yCoordinate = tile.getCoordinate().y();
        Coordinate coordinate;

        possibleTiles = makePossibleTiles(xCoordinate, yCoordinate, emptyTiles, chunk);

        for (int i = 0; i < howMuchMonsters; i++) {
            if (possibleTiles.isEmpty()) return;

            int randomTile = random.nextInt(0, possibleTiles.size());
            coordinate = possibleTiles.get(randomTile).getCoordinate();

            //TODO UUID, WORDT DOOR GAMECORE GEDAAN
            Bot monster = new Bot(UUID.randomUUID(), coordinate, game.getId(), game.getMonsterConfig(), monsterType);
            monster.setChunk(chunk);
            game.addMonster(monster);
            possibleTiles.remove(possibleTiles.get(randomTile));
        }
    }

    /**
     * Makes possible tiles, taking the empty tiles and considers the radius.
     *
     * @param xCoordinate The x coordinate of the tile where the first monster is standing on.
     * @param yCoordinate The y coordinate of the tile where the first monster is standing on.
     * @param emptyTiles  The tiles where no monsters are standing on and are passable.
     * @param chunk       The chunk of the possible tiles.
     * @return A list with possible tiles.
     * @author Fabian van Os
     */
    public List<Tile> makePossibleTiles(int xCoordinate, int yCoordinate, List<Tile> emptyTiles, Chunk chunk) {
        List<Tile> possibleTiles = new ArrayList<>();
        for (int y = yCoordinate - RADIUS; y <= yCoordinate + RADIUS; y++) {
            for (int x = xCoordinate - RADIUS; x <= xCoordinate + RADIUS; x++) {
                if (x < 0 || x > CHUNK_WIDTH - 1 || y < 0 || y > CHUNK_HEIGHT - 1) continue;

                if (emptyTiles.contains(chunk.getTile(new Coordinate(x, y)))) {
                    possibleTiles.add(chunk.getTile(new Coordinate(x, y)));
                }
            }
        }

        return possibleTiles;
    }

    /**
     * Returns a list of all the tiles that are not occupied and passable.
     *
     * @param chunk The chunk where monsters are placed on.
     * @return Returns a list of tiles.
     * @author Fabian van Os
     */
    public List<Tile> getEmptyTilesFromChunk(Chunk chunk) {
        List<Bot> monstersInChunk = getAllMonstersInChunk(chunk);
        List<Tile> output = new ArrayList<>();

        for (int y = 0; y < CHUNK_HEIGHT; y++) {
            for (int x = 0; x < CHUNK_WIDTH; x++) {
                Tile tile = chunk.getTile(new Coordinate(x, y));
                if (tile.isPassable() && tile.getType() != TileType.STAIRS_UP && tile.getType() != TileType.STAIRS_DOWN) {
                    checkIfMonsterOnTile(monstersInChunk, chunk.getTile(new Coordinate(x, y)), output);
                }
            }
        }

        return output;
    }

    /**
     * Generates the first monster in the cluster.
     *
     * @param monsterType The type of the monster.
     * @param chunk       The chunk where monsters are placed on.
     * @return The tile where the monster is spawned on.
     * @author Fabian van Os
     */
    protected Tile generateMonster(BotType monsterType, Chunk chunk) {
        List<Tile> emptyTiles = getEmptyTilesFromChunk(chunk);
        int randomIndex = random.nextInt(0, emptyTiles.size() - 1);
        Tile tile = emptyTiles.get(randomIndex);
        //TODO UUID, WORDT GEDAAN DOOR GAME CORE
        Bot monster = new Bot(UUID.randomUUID(), tile.getCoordinate(), game.getId(), game.getMonsterConfig(), monsterType);
        monster.setChunk(chunk);
        game.addMonster(monster);

        return tile;
    }

    /**
     * Returns all the monsters in the given chunk.
     *
     * @param chunk The chunk where monsters are placed on.
     * @return Returns all the monsters in the given chunk.
     * @author Fabian van Os
     */
    public List<Bot> getAllMonstersInChunk(Chunk chunk) {
        List<Bot> monstersInChunk = new ArrayList<>();
        for (Bot monster : game.getMonsters()) {
            if (chunk.equals(monster.getChunk())) {
                monstersInChunk.add(monster);
            }
        }
        return monstersInChunk;
    }
}

