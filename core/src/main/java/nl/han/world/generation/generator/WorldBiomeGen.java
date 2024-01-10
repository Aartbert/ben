package nl.han.world.generation.generator;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import nl.han.shared.datastructures.game.Game;
import nl.han.shared.datastructures.world.Chunk;
import nl.han.shared.datastructures.world.Coordinate;
import nl.han.shared.enums.BiomeType;

/**
 * This class represents the generation of world biomes.
 */
@RequiredArgsConstructor
@Singleton
public class WorldBiomeGen {

    GameWorldGen gameWorldGen;

    public WorldBiomeGen(GameWorldGen gameWorldGen) {
        this.gameWorldGen = gameWorldGen;
    }

    /**
     * Generates the biome for the given chunk.
     *
     * @param seed  The seed for the world.
     * @param chunk The chunk to generate the biome for.
     */
    public void generateBiome(long seed, Chunk chunk) {

        Coordinate chunkCoordinate = new Coordinate(chunk.getCoordinate().x(), chunk.getCoordinate().y());

        int greyValue = gameWorldGen.generatePerlinNoise(seed, chunkCoordinate, 1);

        BiomeType biome;
        if (greyValue < 51) {
            biome = BiomeType.POLAR;
        } else if (greyValue > 51 && greyValue < 102) {
            biome = BiomeType.CONTINENTAL;
        } else if (greyValue > 102 && greyValue < 153) {
            biome = BiomeType.TEMPERATE;
        } else if (greyValue > 153 && greyValue < 204) {
            biome = BiomeType.DRY;
        } else {
            biome = BiomeType.TROPICAL;
        }

        chunk.setBiomeType(biome);
    }
}