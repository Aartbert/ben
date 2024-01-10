package nl.han.shared.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * The BiomeType enum represents the different types of biological environments that exist
 * within the game world. Each BiomeType has a specific set of structures that can
 * spawn within it, and a distribution map of tile types that make up the biome.
 * <br/>
 * The spawnableStructures is a list of {@link StructureType} objects that can spawn
 * within this biome.
 * <br/>
 * The spawnableTiles is a {@link HashMap} with {@link TileType} as its key and Integer
 * as its value, where the key represents a type of tile that can be found in the biome,
 * and the value represents the frequency with which this tile type appears.
 *
 * @author Jordan Geurtsen
 * @see StructureType
 * @see TileType
 */
@Getter
@AllArgsConstructor
public enum BiomeType {
    CONTINENTAL(new HashMap<>(Map.of(StructureType.GOLDPLATETOWER, 100)),
            new HashMap<>(Map.of(
                    TileType.GRASS, 40,
                    TileType.FOREST, 20,
                    TileType.HILL, 20,
                    TileType.MOUNTAIN, 20))),
    DRY(new HashMap<>(Map.of(StructureType.IVORYTOWER, 100)),
            new HashMap<>(Map.of(
                    TileType.SAND, 80,
                    TileType.GRASS, 20))),
    POLAR(new HashMap<>(Map.of(StructureType.GOLDPLATETOWER, 100)),
            new HashMap<>(Map.of(
                    TileType.SNOW, 20,
                    TileType.ICE, 40,
                    TileType.TUNDRA, 30,
                    TileType.MOUNTAIN, 10))),
    TEMPERATE(new HashMap<>(Map.of(StructureType.IVORYTOWER, 100)),
            new HashMap<>(Map.of(
                    TileType.GRASS, 45,
                    TileType.HILL, 15,
                    TileType.FOREST, 10,
                    TileType.WATER, 30))),
    TROPICAL(new HashMap<>(Map.of(StructureType.GOLDPLATETOWER, 100)),
            new HashMap<>(Map.of(
                    TileType.SAVANNAH, 50,
                    TileType.RAINFOREST, 50)));

    private final HashMap<StructureType, Integer> spawnableStructures;
    private final HashMap<TileType, Integer> spawnableTiles;
}
