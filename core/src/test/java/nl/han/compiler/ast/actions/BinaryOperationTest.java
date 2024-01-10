package nl.han.compiler.ast.actions;

import nl.han.compiler.ast.IASTNode;
import nl.han.compiler.ast.IASTNodeTest;
import nl.han.compiler.ast.enums.Direction;
import nl.han.compiler.ast.operators.AndOperator;
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

import java.util.ArrayList;
import java.util.List;

import static nl.han.compiler.utils.Point.point;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testing class for {@link BinaryOperation}.
 * @see <a href="https://confluenceasd.aimsites.nl/display/ASDS1G2/Testrapport+Onderzoek+Programmeren+Agents">Testrapport</a>
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BinaryOperationTest implements IASTNodeTest {

    private BinaryOperation sut;

    private final Volume<IASTNode, Getter, Setter> instructions = Volume.of(
            point(new AndOperator(), () -> sut.getOperator(), node -> sut.setOperator((AndOperator)node)),
            point(new Attack(), () -> sut.getLhs(), node -> sut.setLhs((IAction) node)),
            point(new Attack(), () -> sut.getLhs(), node -> sut.setLhs((IAction) node))
    );

    @BeforeEach
    void setup() {
        sut = new BinaryOperation();
    }

    /**
     * Test code COM7
     */
    @Test
    @DisplayName("test if two actions are returned when a binary operation is executed with two operands")
    void testGetActionWithTwoOperands() {
        // Arrange
        List<Action> expected = new ArrayList<>();
        expected.add(Action.MOVE_UP);
        expected.add(Action.MOVE_DOWN);

        sut.addChild(new Movement().addChild(Direction.UP));
        sut.addChild(new Movement().addChild(Direction.DOWN));

        // Act
        List<Action> actual = sut.getAction();

        // Assert
        assertEquals(expected, actual);
    }

    /**
     * Test code COM8
     */
    @ParameterizedTest
    @DisplayName("test if a child can be retrieved from a binary operation")
    @MethodSource("provider")
    void testGetChildren(IASTNode expected, Getter getter, Setter setter) {
        testGetChildren(sut, expected, getter, setter);
    }

    /**
     * Test code COM9
     */
    @ParameterizedTest
    @DisplayName("test if a child can be added to a binary operation")
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
