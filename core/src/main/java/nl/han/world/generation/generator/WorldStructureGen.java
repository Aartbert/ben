package nl.han.world.generation.generator;

import lombok.RequiredArgsConstructor;
import nl.han.shared.datastructures.world.*;
import nl.han.shared.enums.BiomeType;
import nl.han.shared.enums.StructureType;
import nl.han.shared.enums.TileType;
import nl.han.shared.utils.Lottery;
import nl.han.shared.utils.random.ICOCRandom;

import java.util.HashMap;

import static nl.han.shared.datastructures.world.Chunk.CHUNK_HEIGHT;
import static nl.han.shared.datastructures.world.Chunk.CHUNK_WIDTH;

/**
 * This class represents how the structures are generated.
 */
public class WorldStructureGen {
    private ICOCRandom random;

    /**
     * Generates structures in the given chunk.
     *
     * @param chunk The chunk to generate structures in.
     */
    public void generateStructures(Chunk chunk) {
        random = chunk.getRandom();
        BiomeType biome = chunk.getBiomeType();
        HashMap<StructureType, Integer> spawnableStructures = biome.getSpawnableStructures();

        if (!spawnableStructures.isEmpty()) {
            if (spawnableStructures.size() == 1) {
                StructureType structureType = (StructureType) spawnableStructures.keySet().toArray()[0];
                renderStructure(chunk, structureType);
                return;
            }
            Lottery<StructureType> structureTypeLottery = new Lottery<>(spawnableStructures);
            int randomValue = random.nextInt(100);
            StructureType structureType = structureTypeLottery.draw(randomValue);
            renderStructure(chunk, structureType);
        }
    }

    /**
     * Renders the given structure in the given chunk.
     *
     * @param chunk     The chunk to render the structure in.
     * @param structureType The structure type to render.
     */
    private void renderStructure(Chunk chunk, StructureType structureType) {
        TileType[][] template = structureType.getTemplate();

        int templateHeight = template.length;
        int templateWidth = getWidth(template);

        int randomX = random.nextInt(CHUNK_WIDTH - templateWidth);
        int randomY = random.nextInt(CHUNK_HEIGHT - templateHeight);

        for (int y = 0; y < templateHeight; y++) {
            for (int x = 0; x < templateWidth; x++) {
                TileType tileType = template[y][x];

                if (tileType == TileType.EMPTY) {
                    continue;
                }

                chunk.setTile(new Tile(tileType, new Coordinate(randomX + x, randomY + y)));
            }
        }
    }


    /**
     * Returns the width of the given template.
     *
     * @param template The template to get the width of.
     * @return The width of the given template.
     */
    private int getWidth(TileType[][] template) {
        int width = 0;
        for (TileType[] row : template) {
            if (row.length > width) {
                width = row.length;
            }
        }
        return width;
    }
}
