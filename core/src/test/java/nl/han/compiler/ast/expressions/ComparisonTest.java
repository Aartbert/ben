package nl.han.compiler.ast.expressions;

import nl.han.compiler.ast.IASTNode;
import nl.han.compiler.ast.IASTNodeTest;
import nl.han.compiler.ast.enums.Attribute;
import nl.han.compiler.ast.literals.ILiteral;
import nl.han.compiler.ast.literals.Percentage;
import nl.han.compiler.ast.operators.GreaterThanOperator;
import nl.han.compiler.ast.operators.Operator;
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
 * Testing class for {@link Comparison}.
 * @see <a href="https://confluenceasd.aimsites.nl/display/ASDS1G2/Testrapport+Onderzoek+Programmeren+Agents">Testrapport</a>
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ComparisonTest implements IASTNodeTest {

    private Comparison sut;

    private final Volume<IASTNode, Getter, Setter> instructions = Volume.of(
            point(Attribute.HEALTH, () -> sut.getAttribute(), node -> sut.setAttribute((Attribute) node)),
            point(new GreaterThanOperator(), () -> sut.getOperator(), node -> sut.setOperator((Operator) node)),
            point(new Percentage("5%"), () -> sut.getValue(), node -> sut.setValue((ILiteral) node))
    );

    @BeforeEach
    void setup() {
        sut = new Comparison();
    }

    /**
     * Test code COM28
     */
    @ParameterizedTest
    @DisplayName("test if a child can be retrieved from a comparison expression")
    @MethodSource("provider")
    void testGetChildren(IASTNode expected, Getter getter, Setter setter) {
        testGetChildren(sut, expected, getter, setter);
    }

    /**
     * Test code COM29
     */
    @ParameterizedTest
    @DisplayName("test if a child can be added to a comparison expression")
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
