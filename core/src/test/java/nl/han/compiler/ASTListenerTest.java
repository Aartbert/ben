package nl.han.compiler;

import nl.han.IAMParser;
import nl.han.compiler.ast.Configuration;
import nl.han.compiler.ast.IASTNode;
import nl.han.compiler.ast.Sentence;
import nl.han.compiler.ast.actions.*;
import nl.han.compiler.ast.enums.Attribute;
import nl.han.compiler.ast.enums.CompilerItem;
import nl.han.compiler.ast.enums.Direction;
import nl.han.compiler.ast.expressions.BinaryExpression;
import nl.han.compiler.ast.expressions.Comparison;
import nl.han.compiler.ast.expressions.Existence;
import nl.han.compiler.ast.literals.Bool;
import nl.han.compiler.ast.literals.Percentage;
import nl.han.compiler.ast.literals.Scalar;
import nl.han.compiler.ast.operators.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Testing class for {@link ASTListener}.
 * @see <a href="https://confluenceasd.aimsites.nl/display/ASDS1G2/Testrapport+Onderzoek+Programmeren+Agents">Testrapport</a>
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ASTListenerTest {

    private ASTListener sut;

    private final Map<IASTNode, EnterCall> scenarios = Map.ofEntries(
            entry(new Sentence(), () -> sut.enterSentence(mock())),
            entry(new BinaryOperation(), () -> sut.enterBinaryOperation(mock())),
            entry(new Movement(), () -> sut.enterMovement(mock())),
            entry(new BinaryExpression(), () -> sut.enterBinaryExpression(mock())),
            entry(new AndOperator(), () -> sut.enterAndOperator(mock())),
            entry(new OrOperator(), () -> sut.enterOrOperator(mock())),
            entry(new LessThanOperator(), () -> sut.enterLessThanOperator(mock())),
            entry(new IsEqualOperator(), () -> sut.enterIsEqualOperator(mock())),
            entry(new GreaterThanOperator(), () -> sut.enterGreaterThanOperator(mock())),
            entry(new Scalar("5"), () -> sut.enterScalar(mockText(IAMParser.ScalarContext.class, "5"))),
            entry(new Percentage("5%"), () -> sut.enterPercentage(mockText(IAMParser.PercentageContext.class, "5%"))),
            entry(new Bool(true), () -> sut.enterTrue(mockText(IAMParser.TrueContext.class, "true"))),
            entry(new Bool(false), () -> sut.enterFalse(mockText(IAMParser.FalseContext.class, "false"))),
            entry(Direction.UP, () -> sut.enterUp(mock())),
            entry(Direction.DOWN, () -> sut.enterDown(mock())),
            entry(Direction.LEFT, () -> sut.enterLeft(mock())),
            entry(Direction.RIGHT, () -> sut.enterRight(mock())),
            entry(Attribute.HEALTH, () -> sut.enterHealth(mock())),
            entry(Attribute.STAMINA, () -> sut.enterStamina(mock())),
            entry(Attribute.POWER, () -> sut.enterPower(mock())),
            entry(Attribute.MONSTER, () -> sut.enterMonster(mock())),
            entry(Attribute.PLAYER, () -> sut.enterPlayer(mock())),
            entry(Attribute.ENEMY, () -> sut.enterEnemy(mock())),
            entry(CompilerItem.HEALTH_POTION, () -> sut.enterHealthPotion(mock())),
            entry(new Comparison(), () -> sut.enterComparison(mock())),
            entry(new Existence(), () -> sut.enterExistence(mock())),
            entry(new PickUp(), () -> sut.enterPickUp(mock())),
            entry(new Wander(), () -> sut.enterWander(mock())),
            entry(new Retreat(), () -> sut.enterRetreat(mock())),
            entry(new Attack(), () -> sut.enterAttack(mock())),
            entry(new Use(), () -> sut.enterUse(mock()))
    );

    @BeforeEach
    void setup() {
        sut = new ASTListener();

        Configuration ast = mock();
        sut.setAst(ast);

        Deque<IASTNode> stack = new ArrayDeque<>();
        stack.push(ast);
        sut.setStack(stack);
    }

    /**
     * Test code COM74
     */
    @ParameterizedTest
    @MethodSource("provider")
    @DisplayName("test if a node can enter the listener")
    void testEnter(IASTNode expected, EnterCall call) {
        // Act
        call.call();

        IASTNode actual = sut.getStack().peek();

        // Assert
        assertEquals(expected, actual);
    }

    private <T extends ParserRuleContext> T mockText(Class<T> clazz, String text) {
        T ctx = mock(clazz);
        when(ctx.getText()).thenReturn(text);

        return ctx;
    }

    private Stream<Object[]> provider() {
        return scenarios.entrySet().stream()
                .map(entry -> new Object[]{entry.getKey(), entry.getValue()});
    }

    @FunctionalInterface
    private interface EnterCall {

        void call();
    }
}
