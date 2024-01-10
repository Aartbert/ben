package nl.han.compiler.ast.operators;

import nl.han.compiler.CompilerException;
import nl.han.compiler.ast.enums.Attribute;
import nl.han.compiler.ast.literals.Bool;
import nl.han.compiler.ast.literals.ILiteral;
import nl.han.compiler.ast.literals.Percentage;
import nl.han.compiler.ast.literals.Scalar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing class for {@link AndOperator}.
 * @see <a href="https://confluenceasd.aimsites.nl/display/ASDS1G2/Testrapport+Onderzoek+Programmeren+Agents">Testrapport</a>
 */
public class AndOperatorTest {

    private AndOperator sut;

    @BeforeEach
    void setup() {
        sut = new AndOperator();
    }

    /**
     * Test code COM52
     */
    @Test
    @DisplayName("test if a valid 'and' operation returns true when it is executed with 2 operands that are true")
    void testEvaluateOperation() {
        // Arrange
        ILiteral lhs = new Bool(true);
        ILiteral rhs = new Bool(true);

        // Act
        boolean actual = sut.evaluate(lhs, rhs);

        // Assert
        assertTrue(actual);
    }

    /**
     * Test code COM53
     */
    @Test
    @DisplayName("test if a valid 'and' operation returns false when it is executed with 1 operand being false")
    void testEvaluateOperationFalseOperand() {
        // Arrange
        ILiteral lhs = new Bool(true);
        ILiteral rhs = new Bool(false);

        // Act
        boolean actual = sut.evaluate(lhs, rhs);

        // Assert
        assertFalse(actual);
    }

    /**
     * Test code COM54
     */
    @Test
    @DisplayName("test if an exception is thrown when an 'and' operation is executed with invalid operands")
    void testEvaluateOperationInvalidOperands() {
        // Arrange
        ILiteral lhs = new Percentage(10);
        ILiteral rhs = new Scalar(25);

        String expected = "Literal must be a boolean";

        // Act & Assert
        CompilerException exception = assertThrows(CompilerException.class, () -> sut.evaluate(lhs, rhs));

        // Assert
        assertEquals(expected, exception.getMessage());

    }

    /**
     * Test code COM55
     */
    @Test
    @DisplayName("test if an exception is thrown when an 'and' operation is checked with invalid operands")
    void testCheckInvalidOperands() {
        // Arrange
        Attribute lhs = Attribute.HEALTH;
        ILiteral rhs = new Scalar(25);

        String expected = "Both operands in 'And' operations must be of type 'Bool'";

        // Act & Assert
        CompilerException exception = assertThrows(CompilerException.class, () -> sut.check(lhs, rhs));

        // Assert
        assertEquals(expected, exception.getMessage());
    }
}
