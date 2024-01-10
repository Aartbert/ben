package nl.han.world.population.spawner;

import lombok.Getter;
import nl.han.shared.datastructures.Config;
import nl.han.shared.datastructures.Item;
import nl.han.shared.datastructures.WorldRules;
import nl.han.shared.datastructures.WorldRulesBuilder;
import nl.han.shared.datastructures.world.Chunk;
import nl.han.shared.datastructures.world.Tile;
import nl.han.shared.enums.ItemData;
import nl.han.shared.enums.TileType;
import nl.han.shared.utils.Lottery;
import nl.han.shared.utils.random.ICOCRandom;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Has all the logic for spawning items in the world.
 *
 * @author Julian van Kuijk, Fabian van Os & Sven van Hoof
 */
public class ItemSpawner implements ISpawner {
    @Getter
    protected final WorldRules.ItemSpawnRules spawnRate;
    private final Lottery<ItemData> lottery;
    private ICOCRandom random;

    public ItemSpawner(Config config) {
        WorldRules rules = WorldRulesBuilder.convertToWorldRules(config);
        List<ItemData> itemData = Arrays.stream(ItemData.values()).toList();
        Map<ItemData, Integer> items = itemData.stream().collect(Collectors.toMap(item -> item, ItemData::getSpawnWeight));

        this.spawnRate = rules.getItemSpawnRules();
        lottery = new Lottery<>(items);
    }

    /**
     * Spawns items for a given chunk.
     *
     * @param chunk The chunk to generate the items for.
     * @author Julian van Kuijk, Fabian van Os & Sven van Hoof
     */
    @Override
    public void execute(Chunk chunk) {
        random = chunk.getRandom();
        List<Tile> tiles = Arrays.stream(chunk.getTiles()).flatMap(Stream::of).collect(Collectors.toList());
        List<Tile> validTiles = getValidTiles(tiles);

        for (int toSpawn = random.nextInt(spawnRate.getMin(), spawnRate.getMax() + 1); toSpawn > 0 && !validTiles.isEmpty(); toSpawn--) {
            Tile randomTile = getRandom(validTiles);
            randomTile.addItem(new Item(UUID.randomUUID(), lottery.drawRandom()));
            validTiles.remove(randomTile);
        }
    }

    /**
     * Returns a random element from a list.
     *
     * @param list the list to return a random item from.
     * @param <T>  the type of elements in the list.
     * @return returns a random element from a list.
     * @author Fabian van Os & Sven van Hoof
     */
    protected <T> T getRandom(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }

    /**
     * Returns all the valid tiles in a chunk.
     * Warning java:S6204 is suppressed because Sonarlint doesn't see the List.remove() outside of this
     * function as a mutation of the list.
     *
     * @param tiles all the tiles in a chunk
     * @return returns the tiles where an item can spawn on.
     * @author Sven van Hoof & Fabian van Os
     */
    @SuppressWarnings("java:S6204")
    protected List<Tile> getValidTiles(List<Tile> tiles) {
        return tiles.stream().filter(tile -> tile.isPassable()
                        && tile.getType() != TileType.STAIRS_UP
                        && tile.getType() != TileType.STAIRS_DOWN)
                .collect(Collectors.toList());
    }
}
