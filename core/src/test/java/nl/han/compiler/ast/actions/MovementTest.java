package nl.han.compiler.ast.actions;

import nl.han.compiler.ast.IASTNode;
import nl.han.compiler.ast.IASTNodeTest;
import nl.han.compiler.ast.Sentence;
import nl.han.compiler.ast.enums.Attribute;
import nl.han.compiler.ast.enums.Direction;
import nl.han.compiler.ast.expressions.Comparison;
import nl.han.compiler.ast.literals.Scalar;
import nl.han.compiler.ast.operators.GreaterThanOperator;
import nl.han.compiler.utils.Getter;
import nl.han.compiler.utils.Setter;
import nl.han.compiler.utils.Volume;
import nl.han.shared.enums.Action;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static nl.han.compiler.utils.Point.point;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testing class for {@link Movement}.
 * @see <a href="https://confluenceasd.aimsites.nl/display/ASDS1G2/Testrapport+Onderzoek+Programmeren+Agents">Testrapport</a>
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MovementTest implements IASTNodeTest {

    private Movement sut;

    private final Volume<IASTNode, Getter, Setter> instructions = Volume.of(
            point(new Scalar(1), () -> sut.getScalar(), node -> sut.setScalar((Scalar) node)),
            point(Direction.UP, () -> sut.getDirection(), node -> sut.setDirection((Direction) node))
    );

    @BeforeEach
    void setup() {
        sut = new Movement();
    }

    /**
     * Test code COM10
     */
    @Test
    @DisplayName("test if a movement can be converted to the correct string")
    void testGetAction() {
        // Assert
        List<Action> expected = List.of(Action.MOVE_UP);

        sut.addChild(Direction.UP);

        // Act
        List<Action> actual = sut.getAction();

        // Assert
        assertEquals(expected, actual);
    }

    /**
     * Test code COM11
     */
    @Test
    @DisplayName("test if movement transforms to get stamina condition")
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

    /**
     * Test code COM12
     */
    @ParameterizedTest
    @DisplayName("test if children can be retrieved from a movement")
    @MethodSource("provider")
    void testGetChildren(IASTNode expected, Getter getter, Setter setter) {
        sut.setScalar(null);
        testGetChildren(sut, expected, getter, setter);
    }

    /**
     * Test code COM13
     */
    @ParameterizedTest
    @DisplayName("test if a child can be added to a movement")
    @MethodSource("provider")
    void testAddChild(IASTNode expected, Getter getter, Setter setter) {
        testAddChild(sut, expected, getter, setter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Volume<IASTNode, Getter, Setter> instructions() {
        return instructions;
    }
}
