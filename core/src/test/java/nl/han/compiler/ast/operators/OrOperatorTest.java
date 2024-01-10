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
 * Testing class for {@link OrOperator}.
 * @see <a href="https://confluenceasd.aimsites.nl/display/ASDS1G2/Testrapport+Onderzoek+Programmeren+Agents">Testrapport</a>
 */
public class OrOperatorTest {

    private OrOperator sut;

    @BeforeEach
    void setup() {
        sut = new OrOperator();
    }

    /**
     * Test code COM65
     */
    @Test
    @DisplayName("test if a valid 'or' operation returns true when it is executed with 2 operands one of which is true")
    void testEvaluateOperation() {
        // Arrange
        ILiteral lhs = new Bool(false);
        ILiteral rhs = new Bool(true);

        // Act
        boolean actual = sut.evaluate(lhs, rhs);

        // Assert
        assertTrue(actual);
    }

    /**
     * Test code COM66
     */
    @Test
    @DisplayName("test if a valid 'or' operation returns true when it is executed with 2 operands both of which are true")
    void testEvaluateOperationTrueResult() {
        // Arrange
        ILiteral lhs = new Bool(true);
        ILiteral rhs = new Bool(true);

        // Act
        boolean actual = sut.evaluate(lhs, rhs);

        // Assert
        assertTrue(actual);
    }

    /**
     * Test code COM67
     */
    @Test
    @DisplayName("test if a valid 'or' operation returns false when both operands are false")
    void testEvaluateOperationFalseResult() {
        // Arrange
        ILiteral lhs = new Bool(false);
        ILiteral rhs = new Bool(false);

        // Act
        boolean actual = sut.evaluate(lhs, rhs);

        // Assert
        assertFalse(actual);
    }

    /**
     * Test code COM68
     */
    @Test
    @DisplayName("test if an exception is thrown when an 'or' operation is executed with invalid operands")
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
     * Test code COM69
     */
    @Test
    @DisplayName("test if an exception is thrown when an 'or' operation is checked with invalid operands")
    void testCheckInvalidOperands() {
        // Arrange
        Attribute lhs = Attribute.HEALTH;
        ILiteral rhs = new Scalar(25);
        String expected = "Both operands in 'Or' operations must be of type 'Bool'";

        // Act & Assert
        CompilerException exception = assertThrows(CompilerException.class, () -> sut.check(lhs, rhs));

        // Assert
        assertEquals(expected, exception.getMessage());
    }
}
