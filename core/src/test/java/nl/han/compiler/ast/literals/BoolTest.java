package nl.han.compiler.ast.literals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testing class for {@link Bool}.
 * @see <a href="https://confluenceasd.aimsites.nl/display/ASDS1G2/Testrapport+Onderzoek+Programmeren+Agents">Testrapport</a>
 */
public class BoolTest {

    private Bool sut;

    @BeforeEach
    void setup() {
        sut = new Bool(true);
    }

    /**
     * Test code COM38
     */
    @Test
    @DisplayName("test if bool is of the literal type boolean")
    void testGetType() {
        // Arrange
        LiteralType expected = LiteralType.BOOL;

        // Act
        LiteralType actual = sut.getType();

        // Act
        assertEquals(expected, actual);
    }

    /**
     * Test code COM39
     */
    @Test
    @DisplayName("test if the bool that is compared to another bool is equal")
    void testCompareToEqual() {
        // Arrange
        Bool bool = new Bool(true);

        int expected = 0;

        // Act
        int actual = sut.compareTo(bool);

        // Assert
        assertEquals(expected, actual);
    }

    /**
     * Test code COM40
     */
    @Test
    @DisplayName("test if a bool that is compared to another bool is not equal")
    void testCompareToDifferent() {
        // Arrange
        Bool bool = new Bool(false);

        int expected = 1;

        // Act
        int actual = sut.compareTo(bool);

        // Assert
        assertEquals(expected, actual);
    }

    /**
     * Test code COM41
     */
    @Test
    @DisplayName("test if a bool is compared to not a bool gives an error")
    void testCompareToError() {
        // Arrange
        Percentage percentage = new Percentage(10);

        int expected = -1;

        // Act & Assert
        int actual = sut.compareTo(percentage);

        // Assert
        assertEquals(expected, actual);
    }
}
