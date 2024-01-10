package nl.han.compiler.ast.operators;

import nl.han.compiler.CompilerException;
import nl.han.compiler.ast.enums.Attribute;
import nl.han.compiler.ast.literals.Bool;
import nl.han.compiler.ast.literals.ILiteral;
import nl.han.compiler.ast.literals.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing class for {@link IsEqualOperator}.
 * @see <a href="https://confluenceasd.aimsites.nl/display/ASDS1G2/Testrapport+Onderzoek+Programmeren+Agents">Testrapport</a>
 */
public class IsEqualOperatorTest {

    private IsEqualOperator sut;

    @BeforeEach
    void setup() {
        sut = new IsEqualOperator();
    }

    /**
     * Test code COM59
     */
    @Test
    @DisplayName("test if a valid 'is equal' operation returns true when it is executed with 2 operands that are equal to each other")
    void testEvaluateOperation() {
        // Arrange
        ILiteral lhs = new Percentage(10);
        ILiteral rhs = new Percentage(10);

        // Act
        boolean actual = sut.evaluate(lhs, rhs);

        // Assert
        assertTrue(actual);
    }

    /**
     * Test code COM60
     */
    @Test
    @DisplayName("test if a valid 'is equal' operation returns false when it is executed with two different literals")
    void testEvaluateOperationFalseResult() {
        // Arrange
        ILiteral lhs = new Percentage(42);
        ILiteral rhs = new Percentage(24);

        // Act
        boolean actual = sut.evaluate(lhs, rhs);

        // Assert
        assertFalse(actual);
    }

    /**
     * Test code COM61
     */
    @Test
    @DisplayName("test if an exception is thrown when a 'is equal' operation is checked with invalid operands")
    void testCheckInvalidOperands() {
        // Arrange
        Attribute lhs = Attribute.HEALTH;
        ILiteral rhs = new Bool(true);
        String expected = "Both operands in 'Is equal' operations must be values of the same type";

        // Act & Assert
        CompilerException exception = assertThrows(CompilerException.class, () -> sut.check(lhs, rhs));

        // Assert
        assertEquals(expected, exception.getMessage());
    }
}
