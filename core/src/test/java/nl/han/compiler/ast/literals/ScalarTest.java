package nl.han.compiler.ast.literals;

import nl.han.compiler.CompilerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing class for {@link Scalar}.
 * @see <a href="https://confluenceasd.aimsites.nl/display/ASDS1G2/Testrapport+Onderzoek+Programmeren+Agents">Testrapport</a>
 */
public class ScalarTest {

    private Scalar sut;

    @BeforeEach
    void setup() {
        sut = new Scalar("20");
    }

    /**
     * Test code COM47
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
     * Test code COM48
     */
    @Test
    @DisplayName("test if a scalar that is compared to another scalar is greater")
    void testCompareToGreater() {
        // Arrange
        Scalar scalar = new Scalar("10");

        // Act
        int actual = sut.compareTo(scalar);

        // Assert
        assertTrue(actual > 0);
    }

    /**
     * Test code COM49
     */
    @Test
    @DisplayName("test if a scalar that is compared to another scalar is equal")
    void testCompareToEqual() {
        // Arrange
        Scalar scalar = new Scalar("20");

        int expected = 0;

        // Act
        int actual = sut.compareTo(scalar);

        // Assert
        assertEquals(expected, actual);
    }

    /**
     * Test code COM50
     */
    @Test
    @DisplayName("test if a scalar that is compared to another scalar is lower")
    void testCompareToLower() {
        // Arrange
        Scalar scalar = new Scalar("30");

        // Act
        int actual = sut.compareTo(scalar);

        // Assert
        assertTrue(actual < 0);
    }

    /**
     * Test code COM51
     */
    @Test
    @DisplayName("test if a scalar is compared to not a scalar gives an error")
    void testCompareToError() {
        // Arrange
        Bool bool = new Bool(true);

        String expected = "Literal must be scalar";

        // Act & Assert
        CompilerException exception = assertThrows(CompilerException.class, () -> sut.compareTo(bool));

        // Assert
        assertEquals(expected, exception.getMessage());
    }
}
