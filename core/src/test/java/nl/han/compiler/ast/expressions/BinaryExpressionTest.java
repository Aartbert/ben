package nl.han.compiler.ast.expressions;

import nl.han.compiler.ast.IASTNode;
import nl.han.compiler.ast.IASTNodeTest;
import nl.han.compiler.ast.enums.Attribute;
import nl.han.compiler.ast.literals.Percentage;
import nl.han.compiler.ast.operators.AndOperator;
import nl.han.compiler.ast.operators.GreaterThanOperator;
import nl.han.compiler.ast.operators.LessThanOperator;
import nl.han.compiler.utils.Getter;
import nl.han.compiler.utils.Setter;
import nl.han.compiler.utils.Volume;
import nl.han.shared.datastructures.BoundedValue;
import nl.han.shared.datastructures.creature.Bot;
import nl.han.shared.datastructures.creature.Creature;
import nl.han.shared.datastructures.game.Game;
import nl.han.shared.enums.BotType;
import nl.han.shared.enums.GameMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.UUID;

import static nl.han.compiler.utils.Point.point;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for {@link BinaryExpression}.
 * @see <a href="https://confluenceasd.aimsites.nl/display/ASDS1G2/Testrapport+Onderzoek+Programmeren+Agents">Testrapport</a>
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BinaryExpressionTest implements IASTNodeTest {

    private BinaryExpression sut;

    private final Volume<IASTNode, Getter, Setter> instructions = Volume.of(
            point(new AndOperator(), () -> sut.getOperator(), node -> sut.setOperator((AndOperator)node)),
            point(new Comparison(), () -> sut.getLhs(), node -> sut.setLhs((IExpression) node))
    );

    @BeforeEach
    void setup() {
        sut = new BinaryExpression();
    }

    /**
     * Test code COM24
     */
    @Test
    @DisplayName("test validate method returns true for AND condition")
    void testValidateWithAndCondition() {
        // Arrange
        Comparison con1 = new Comparison();
        con1.setAttribute(Attribute.HEALTH);
        con1.setValue(new Percentage("30%"));
        con1.setOperator(new GreaterThanOperator());

        Comparison con2 = new Comparison();
        con2.setAttribute(Attribute.HEALTH);
        con2.setValue(new Percentage("50%"));
        con2.setOperator(new LessThanOperator());

        sut.setOperator(new AndOperator());
        sut.setLhs(con1);
        sut.setRhs(con2);

        BotType botType = BotType.ZOMBIE;

        Creature creature = new Bot(UUID.randomUUID(), null, UUID.randomUUID(), null, botType);
        creature.setHealth(new BoundedValue(14, 30, 0));

        // Act
        boolean result = sut.validate(creature, new Game(UUID.randomUUID(), "LOL", GameMode.LMS, null));

        // Assert
        assertTrue(result);
    }

    /**
     * Test code COM25
     */
    @Test
    @DisplayName("test validate method returns false for invalid conditions")
    void testValidateWithInvalidConditions() {
        // Arrange
        Comparison con1 = new Comparison();
        Comparison con2 = new Comparison();
        con1.setAttribute(Attribute.HEALTH);
        con1.setValue(new Percentage("20%"));
        con1.setOperator(new GreaterThanOperator());

        con2.setAttribute(Attribute.HEALTH);
        con2.setValue(new Percentage("30%"));
        con2.setOperator(new LessThanOperator());

        sut.setOperator(new AndOperator());
        sut.setLhs(con1);
        sut.setRhs(con2);

        BotType botType = BotType.ZOMBIE;

        Creature creature = new Bot(UUID.randomUUID(), null, UUID.randomUUID(), null, botType);

        // Act
        boolean result = sut.validate(creature, new Game(UUID.randomUUID(), "LOL", GameMode.LMS, null));


        // Assert
        assertFalse(result);
    }

    /**
     * Test code COM26
     */
    @ParameterizedTest
    @DisplayName("test if a child can be retrieved from a binary expression")
    @MethodSource("provider")
    void testGetChildren(IASTNode expected, Getter getter, Setter setter) {
        testGetChildren(sut, expected, getter, setter);
    }

    /**
     * Test code COM27
     */
    @ParameterizedTest
    @DisplayName("test if a child can be added to a binary expression")
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
