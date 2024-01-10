package nl.han.world.generation.generator;

import jakarta.inject.Inject;
import nl.han.shared.datastructures.game.Game;
import nl.han.shared.datastructures.world.Chunk;
import nl.han.shared.datastructures.world.Coordinate;

/**
 * This class calls the functions for the world gen.
 */
public class GameWorldGen {
    private final PerlinNoise gameWorldGenPerlinNoise = new PerlinNoise();
    private final WorldBiomeGen worldBiomeGen;
    private final WorldTilesGen worldTilesGen;
    @Inject
    private final WorldStructureGen worldStructureGen;
    private static final double FREQUENCY = 1.0 / 24.0;
    private final Game game;

    /**
     * Constructor for the GameWorldGen class.
     *
     * @param game The game for which this instance of GameWorldGen should generate chunks.
     */
    public GameWorldGen(Game game) {
        this.game = game;
        worldStructureGen = new WorldStructureGen();
        worldBiomeGen = new WorldBiomeGen(this);
        worldTilesGen = new WorldTilesGen(this);
    }

    /**
     * Generates the world for the given chunk.
     *
     * @param chunk The chunk to generate the world for.
     */
    public void gameWorldGen(Chunk chunk) {
        worldTilesGen.processChunk(game.getWorld().getSeed(), chunk);
    }

    /**
     * Soft-generates the world for the given chunk.
     *
     * @param chunk The chunk to generate the world for.
     */
    public void softGameWorldGen(Chunk chunk) {
        worldBiomeGen.generateBiome(game.getWorld().getSeed(), chunk);
        worldStructureGen.generateStructures(chunk);
    }


    /**
     * Generates the perlin noise for the given seed, x, y and octaveAmount.
     *
     * @param seed         The seed to use for the perlin noise.
     * @param coordinate   The coordinate to use for the perlin noise.
     * @param octaveAmount The amount of octaves to use for the perlin noise.
     * @return The perlin noise for the given seed, x, y and octaveAmount.
     */
    public int generatePerlinNoise(long seed, Coordinate coordinate, int octaveAmount) {
        double value = 0.0;
        double amplitude = 1.0;
        double totalAmplitude = 0.0;
        int x = coordinate.x();
        int y = coordinate.y();

        for (int octave = 0; octave < octaveAmount; octave++) {
            double frequency = Math.pow(2, octave);
            value += gameWorldGenPerlinNoise.noise2_ImproveX(seed, x * FREQUENCY * frequency, y * FREQUENCY * frequency) * amplitude;
            totalAmplitude += amplitude;
            amplitude *= 0.5;
        }

        if (totalAmplitude == 0) return 0;

        value /= totalAmplitude;
        return (int) ((value + 1) * 127.5);
    }
}
