package nl.han.world.population.structure_generation.bsp_dungeon_generation.binary_space_partitioning;

import nl.han.shared.datastructures.world.Coordinate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing class for {@link Room}.
 * @see <a href="https://confluenceasd.aimsites.nl/display/ASDS1G2/Testrapport+Onderzoek+Wereldpopulatie">Testrapport</a>
 * @author Lucas van Steveninck
 */
class RoomTest {
    Room sut;

    @BeforeEach
    void setup() {
        this.sut = new Room(new Coordinate(0, 0), 80, 24);
    }

    /**
     * Test code WPT43
     */
    @Test
    @DisplayName("test whether a room can horizontally split itself correctly")
    void testSplitHorizontally() {
        // Arrange
        int splittingPoint = 11;

        List<Room> expected = new ArrayList<>(List.of(
                new Room(new Coordinate(0, 0), 80, 11),
                new Room(new Coordinate(0, 11), 80, 13)));

        // Act
        List<Room> actual = sut.splitHorizontally(splittingPoint);

        // Assert
        assertEquals(expected, actual);
    }

    /**
     * Test code WPT44
     */
    @Test
    @DisplayName("test whether a room can vertically split itself correctly")
    void testSplitVertically() {
        // Arrange
        int splittingPoint = 49;

        List<Room> expected = new ArrayList<>(List.of(
                new Room(new Coordinate(0, 0), 49, 24),
                new Room(new Coordinate(49, 0), 31, 24)));

        // Act
        List<Room> actual = sut.splitVertically(splittingPoint);

        // Assert
        assertEquals(expected, actual);
    }

    /**
     * Test code WPT45
     */
    @Test
    @DisplayName("test whether a room can determine if it contains a certain position correctly when the position is inside the room")
    void testContains() {
        // Arrange
        Coordinate position = new Coordinate(79, 23);

        // Act
        boolean actual = sut.contains(position);

        // Assert
        assertTrue(actual);
    }

    /**
     * Test code WPT46
     */
    @Test
    @DisplayName("test whether a room can determine if it contains a certain position correctly when the position is not inside the room")
    void testNotContains() {
        // Arrange
        Coordinate position = new Coordinate(80, 24);

        // Act
        boolean actual = sut.contains(position);

        // Assert
        assertFalse(actual);
    }

    /**
     * Test code WPT47
     */
    @Test
    @DisplayName("test whether a room can determine if it contains a certain position correctly when the position is an illegal position")
    void testContainsIllegalPosition() {
        // Arrange
        Coordinate position = new Coordinate(-60, -13);

        // Act
        boolean actual = sut.contains(position);

        // Assert
        assertFalse(actual);
    }
}
