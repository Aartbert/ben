package nl.han.compiler.ast.literals;

import nl.han.compiler.CompilerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing class for {@link Percentage}.
 * @see <a href="https://confluenceasd.aimsites.nl/display/ASDS1G2/Testrapport+Onderzoek+Programmeren+Agents">Testrapport</a>
 */
public class PercentageTest {

    private Percentage sut;

    @BeforeEach
    void setup() {
        sut = new Percentage("20%");
    }

    /**
     * Test code COM42
     */
    @Test
    @DisplayName("test if percentage is of the literal type numeric")
    void testGetType() {
        // Arrange
        LiteralType expected = LiteralType.NUMERIC;

        // Act
        LiteralType actual = sut.getType();

        // Act
        assertEquals(expected, actual);
    }

    /**
     * Test code COM43
     */
    @Test
    @DisplayName("test if a percentage that is compared to another percentage is greater")
    void testCompareToGreater() {
        // Arrange
        Percentage percentage = new Percentage("10%");

        // Act
        int actual = sut.compareTo(percentage);

        // Assert
        assertTrue(actual > 0);
    }

    /**
     * Test code COM44
     */
    @Test
    @DisplayName("test if a percentage that is compared to another percentage is equal")
    void testCompareToEqual() {
        // Arrange
        Percentage percentage = new Percentage("20%");

        int expected = 0;

        // Act
        int actual = sut.compareTo(percentage);

        // Assert
        assertEquals(expected, actual);
    }

    /**
     * Test code COM45
     */
    @Test
    @DisplayName("test if a percentage that is compared to another percentage is lower")
    void testCompareToLower() {
        // Arrange
        Percentage percentage = new Percentage("30%");

        // Act
        int actual = sut.compareTo(percentage);

        // Assert
        assertTrue(actual < 0);
    }

    /**
     * Test code COM46
     */
    @Test
    @DisplayName("test if a percentage is compared to not a percentage gives an error")
    void testCompareToError() {
        // Arrange
        Bool bool = new Bool(true);

        String expected = "Literal must be percentage";

        // Act & Assert
        CompilerException exception = assertThrows(CompilerException.class, () -> sut.compareTo(bool));

        // Assert
        assertEquals(expected, exception.getMessage());
    }
}
