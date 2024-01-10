package nl.han.compiler.ast.actions;

import nl.han.compiler.ast.Sentence;
import nl.han.compiler.ast.enums.Attribute;
import nl.han.compiler.ast.expressions.Comparison;
import nl.han.compiler.ast.literals.Scalar;
import nl.han.compiler.ast.operators.GreaterThanOperator;
import nl.han.shared.enums.Action;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testing class for {@link Retreat}.
 * @see <a href="https://confluenceasd.aimsites.nl/display/ASDS1G2/Testrapport+Onderzoek+Programmeren+Agents">Testrapport</a>
 */
public class RetreatTest {

    private Retreat sut;

    @BeforeEach
    void setup() {
        sut = new Retreat();
    }

    /**
     * Test code COM15
     */
    @Test
    @DisplayName("test if the REN_WEG action is returned")
    void testGetAction() {
        // Arrange
        List<Action> expected = List.of(Action.RUN);

        // Act
        List<Action> actual = sut.getAction();

        // Assert
        assertEquals(expected, actual);
    }

    /**
     * Test code COM16
     */
    @Test
    @DisplayName("test if retreat transforms to get stamina condition")
    void testTransform() {
        // Arrange
        Sentence sentence = new Sentence();
        Comparison expected = new Comparison();
        expected.setAttribute(Attribute.STAMINA);
        expected.setOperator(new GreaterThanOperator());
        expected.setValue(new Scalar(0));

        // Act
        sut.transform(sentence);
        Comparison actual = (Comparison) sentence.getChildren().get(0);

        // Assert
        assertEquals(expected, actual);
    }
}
