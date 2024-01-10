package nl.han.compiler.ast.actions;

import nl.han.compiler.CompilerException;
import nl.han.compiler.ast.IASTNode;
import nl.han.compiler.ast.IASTNodeTest;
import nl.han.compiler.ast.Sentence;
import nl.han.compiler.ast.enums.Attribute;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for {@link Attack}.
 * @see <a href="https://confluenceasd.aimsites.nl/display/ASDS1G2/Testrapport+Onderzoek+Programmeren+Agents">Testrapport</a>
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AttackTest implements IASTNodeTest {

    private Attack sut;

    private final Volume<IASTNode, Getter, Setter> instructions = Volume.of(
            point(Attribute.PLAYER, () -> sut.getCreatureType(), node -> sut.setCreatureType((Attribute) node))
    );

    @BeforeEach
    void setup() {
        sut = new Attack();
    }

    /**
     * Test code COM1
     */
    @Test
    @DisplayName("test if an attack on player can be converted to the correct action")
    void testGetActionAttackPlayer() {
        // Assert
        sut.setCreatureType(Attribute.PLAYER);

        List<Action> expected = List.of(Action.SEARCH_PLAYER);

        // Act
        List<Action> actual = sut.getAction();

        // Assert
        assertEquals(expected, actual);
    }

    /**
     * Test code COM2
     */
    @Test
    @DisplayName("test if an attack on monster can be converted to the correct action")
    void testGetActionAttackMonster() {
        // Assert
        sut.setCreatureType(Attribute.MONSTER);

        List<Action> expected = List.of(Action.SEARCH_MONSTER);

        // Act
        List<Action> actual = sut.getAction();

        // Assert
        assertEquals(expected, actual);
    }

    /**
     * Test code COM3
     */
    @Test
    @DisplayName("test if an attack on health gives an error")
    void testGetActionAttackError() {
        // Assert
        sut.setCreatureType(Attribute.HEALTH);

        String expected = "Unexpected value: Attribute{name='HEALTH'}";

        // Act & Assert
        CompilerException exception = assertThrows(CompilerException.class, () -> sut.getAction());

        // Assert
        assertEquals(expected, exception.getMessage());
    }

    /**
     * Test code COM4
     */
    @Test
    @DisplayName("test if attack transforms to get stamina condition")
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
     * Test code COM5
     */
    @ParameterizedTest
    @DisplayName("test if a child can be retrieved from an attack")
    @MethodSource("provider")
    void testGetChildren(IASTNode expected, Getter getter, Setter setter) {
        testGetChildren(sut, expected, getter, setter);
    }

    /**
     * Test code COM6
     */
    @ParameterizedTest
    @DisplayName("test if a child can be added to an attack")
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
