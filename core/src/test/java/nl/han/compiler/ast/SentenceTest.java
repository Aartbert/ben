package nl.han.compiler.ast;

import nl.han.compiler.ast.actions.Attack;
import nl.han.compiler.ast.expressions.Comparison;
import nl.han.compiler.ast.expressions.IExpression;
import nl.han.compiler.utils.Getter;
import nl.han.compiler.utils.Setter;
import nl.han.compiler.utils.Volume;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static nl.han.compiler.utils.Point.point;

/**
 * Testing class for {@link Sentence}.
 * @see <a href="https://confluenceasd.aimsites.nl/display/ASDS1G2/Testrapport+Onderzoek+Programmeren+Agents">Testrapport</a>
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SentenceTest implements IASTNodeTest {

    private Sentence sut;

    private final Volume<IASTNode, Getter, Setter> instructions = Volume.of(
            point(new Attack(), () -> sut.getOperation(), node -> sut.setOperation((Attack) node)),
            point(new Comparison(), () -> sut.getCondition(), node -> sut.setCondition((IExpression) node))
    );

    @BeforeEach
    void setup() {
        sut = new Sentence();
    }

    /**
     * Test code COM72
     */
    @ParameterizedTest
    @DisplayName("test if a child can be retrieved from a sentence")
    @MethodSource("provider")
    void testGetChildren(IASTNode expected, Getter getter, Setter setter) {
        testGetChildren(sut, expected, getter, setter);
    }

    /**
     * Test code COM73
     */
    @ParameterizedTest
    @DisplayName("test if a child can be added to a sentence")
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
