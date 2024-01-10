package nl.han.compiler.ast.enums;

import nl.han.compiler.ast.literals.LiteralType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testing class for {@link Attribute}.
 * @see <a href="https://confluenceasd.aimsites.nl/display/ASDS1G2/Testrapport+Onderzoek+Programmeren+Agents">Testrapport</a>
 */
public class AttributeTest {

    /**
     * Test code COM23
     */
    @Test
    @DisplayName("test if the literal type of an known attribute is numeric")
    void testGetTypeNumeric() {
        // Arrange
        Attribute attribute = Attribute.HEALTH;

        LiteralType expected = LiteralType.NUMERIC;

        // Act
        LiteralType actual = attribute.getType();

        // Assert
        assertEquals(expected, actual);
    }
}
