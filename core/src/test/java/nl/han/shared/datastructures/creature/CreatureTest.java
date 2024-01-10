package nl.han.shared.datastructures.creature;

import nl.han.shared.datastructures.BoundedValue;
import nl.han.shared.datastructures.world.Chunk;
import nl.han.shared.datastructures.world.Coordinate;
import nl.han.shared.datastructures.world.Tile;
import nl.han.shared.enums.TileType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This class is responsible for testing the Creature class.
 *
 * @see <a href="https://confluenceasd.aimsites.nl/display/ASDS1G2/Testrapport+game+core">Testrapport</a>
 */
class CreatureTest {
    Player sut;
    Tile tile = new Tile(TileType.GRASS, new Coordinate(0, 0));
    Chunk chunk = mock();

    @BeforeEach
    void setUp() {
        BoundedValue sutHealth = new BoundedValue(50, 100, 0);
        BoundedValue sutPower = new BoundedValue(50, 100, 0);
        BoundedValue sutStamina = new BoundedValue(50, 100, 0);

        sut = new Player(null, "TestPlayer", null, null, null, sutHealth, sutPower, sutStamina, null);
        sut.setChunk(chunk);
        sut.setCoordinate(new Coordinate(0, 0));
        when(chunk.getTile(any())).thenReturn(tile);
    }

    /**
     * Test code GCPA-1
     */
    @Test
    @DisplayName("Test to see if creature's stamina is lowered")
    void testStaminaLowered() {
        // Arrange
        BoundedValue expectedHealth = new BoundedValue(50, 100, 0);
        BoundedValue expectedPower = new BoundedValue(50, 100, 0);
        BoundedValue expectedStamina = new BoundedValue(49, 100, 0);
        Player expected = new Player(null, "TestPlayer", null, null, null, expectedHealth, expectedStamina, expectedPower, null);

        // Act
        sut.moveUp();

        // Assert
        assertEquals(expected.getStamina().getIntValue(), sut.getStamina().getIntValue());
    }

    /**
     * Test code GCPA-2
     */
    @Test
    @DisplayName("Test to see if creature's Y-position is lowered when moving up")
    void testMoveUp() {
        // Arrange
        BoundedValue expectedHealth = new BoundedValue(50, 100, 0);
        BoundedValue expectedPower = new BoundedValue(50, 100, 0);
        BoundedValue expectedStamina = new BoundedValue(50, 100, 0);
        Player expected = new Player(null, "TestPlayer", null, null, null, expectedHealth, expectedPower, expectedStamina, null);
        expected.setCoordinate(new Coordinate(0, -1));

        // Act
        sut.moveUp();

        // Assert
        assertEquals(sut.getCoordinate(), expected.getCoordinate());
    }

    /**
     * Test code GCPA-3
     */
    @Test
    @DisplayName("Test to see if creature's Y-position is increased when moving down")
    void testMoveDown() {
        // Arrange
        BoundedValue expectedHealth = new BoundedValue(50, 100, 0);
        BoundedValue expectedPower = new BoundedValue(50, 100, 0);
        BoundedValue expectedStamina = new BoundedValue(50, 100, 0);
        Player expected = new Player(null, "TestPlayer", null, null, null, expectedHealth, expectedPower, expectedStamina, null);
        expected.setCoordinate(new Coordinate(0, 1));

        // Act
        sut.moveDown();

        // Assert
        assertEquals(sut.getCoordinate(), expected.getCoordinate());
    }

    /**
     * Test code GCPA-4
     */
    @Test
    @DisplayName("Test to see if creature's X-position is lowered when moving left")
    void testMoveLeft() {
        // Arrange
        BoundedValue expectedHealth = new BoundedValue(50, 100, 0);
        BoundedValue expectedPower = new BoundedValue(50, 100, 0);
        BoundedValue expectedStamina = new BoundedValue(50, 100, 0);
        Player expected = new Player(null, "TestPlayer", null, null, null, expectedHealth, expectedPower, expectedStamina, null);
        expected.setCoordinate(new Coordinate(-1, 0));

        // Act
        sut.moveLeft();

        // Assert
        assertEquals(sut.getCoordinate(), expected.getCoordinate());
    }

    /**
     * Test code GCPA-5
     */
    @Test
    @DisplayName("Test to see if creature's X-position is increased when moving right")
    void testMoveRight() {
        // Arrange
        BoundedValue expectedHealth = new BoundedValue(50, 100, 0);
        BoundedValue expectedPower = new BoundedValue(50, 100, 0);
        BoundedValue expectedStamina = new BoundedValue(50, 100, 0);
        Player expected = new Player(null, "TestPlayer", null, null, null, expectedHealth, expectedPower, expectedStamina, null);
        expected.setCoordinate(new Coordinate(1, 0));

        // Act
        sut.moveRight();

        // Assert
        assertEquals(sut.getCoordinate(), expected.getCoordinate());
    }
}