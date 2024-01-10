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
 * Testing class for {@link LessThanOperator}.
 * @see <a href="https://confluenceasd.aimsites.nl/display/ASDS1G2/Testrapport+Onderzoek+Programmeren+Agents">Testrapport</a>
 */
public class LessThanOperatorTest {

    private LessThanOperator sut;

    @BeforeEach
    void setup() {
        sut = new LessThanOperator();
    }

    /**
     * Test code COM62
     */
    @Test
    @DisplayName("test if a valid 'less than' operation returns false when it is executed with a left operand that is greater than the right operand")
    void testEvaluateOperation() {
        // Arrange
        ILiteral lhs = new Percentage(10);
        ILiteral rhs = new Percentage(9);

        // Act
        boolean actual = sut.evaluate(lhs, rhs);

        // Assert
        assertFalse(actual);
    }

    /**
     * Test code COM63
     */
    @Test
    @DisplayName("test if a valid 'less than' operation returns true when it is executed with a left operand that is less than the right operand")
    void testEvaluateOperationTrueResult() {
        // Arrange
        ILiteral lhs = new Percentage(9);
        ILiteral rhs = new Percentage(10);

        // Act
        boolean actual = sut.evaluate(lhs, rhs);

        // Assert
        assertTrue(actual);
    }

    /**
     * Test code COM64
     */
    @Test
    @DisplayName("test if an exception is thrown when a 'less than' operation is checked with invalid operands")
    void testCheckInvalidOperands() {
        // Arrange
        Attribute lhs = Attribute.HEALTH;
        ILiteral rhs = new Bool(true);
        String expected = "Both operands in 'Less than' operations must be numeric values";

        // Act & Assert
        CompilerException exception = assertThrows(CompilerException.class, () -> sut.check(lhs, rhs));

        // Assert
        assertEquals(expected, exception.getMessage());
    }
}
