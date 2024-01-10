package nl.han.world.generation.generator;

import nl.han.shared.datastructures.world.Chunk;
import nl.han.shared.datastructures.world.Coordinate;
import nl.han.shared.datastructures.world.Tile;
import nl.han.shared.enums.BiomeType;
import nl.han.shared.enums.TileType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotSame;

class WorldStructureGenTest {

    WorldStructureGen sut = new WorldStructureGen();
    Chunk chunk = new Chunk(new Tile[24][80], new Coordinate(0, 0, 0), 0L, UUID.randomUUID());

    @BeforeEach
    void setup() {
        for (int x = 0; x < 80; x++) {
            for (int y = 0; y < 24; y++) {
                chunk.setTile(new Tile(TileType.GRASS, new Coordinate(x, y)));
            }
        }
        chunk.setBiomeType(BiomeType.CONTINENTAL);
    }

    @Test
    @DisplayName("test if structure is placed in the chunk")
    void testPlaceStructure() {
        // Arrange
        Tile[][] current = chunk.getTiles();

        // Act
        sut.generateStructures(chunk);

        // Assert
        assertNotSame(Arrays.deepToString(current), Arrays.deepToString(chunk.getTiles()));
    }
}