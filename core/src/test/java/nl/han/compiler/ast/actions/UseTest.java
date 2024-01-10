package nl.han.compiler.ast.actions;

import nl.han.compiler.ast.IASTNode;
import nl.han.compiler.ast.IASTNodeTest;
import nl.han.compiler.ast.Sentence;
import nl.han.compiler.ast.enums.CompilerItem;
import nl.han.compiler.ast.expressions.Existence;
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
 * Testing class for {@link Use}.
 *
 * @see <a href="https://confluenceasd.aimsites.nl/display/ASDS1G2/Testrapport+Onderzoek+Programmeren+Agents">Testrapport</a>
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UseTest implements IASTNodeTest {

    private Use sut;

    private final Volume<IASTNode, Getter, Setter> instructions = Volume.of(
            point(CompilerItem.HEALTH_POTION, () -> sut.getItem(), node -> sut.setItem((CompilerItem) node))
    );

    @BeforeEach
    void setup() {
        sut = new Use();
    }

    /**
     * Test code COM17
     */
    @Test
    @DisplayName("test if the usage of a health potion returns the correct action")
    void testGetAction() {
        // Arrange
        CompilerItem item = CompilerItem.HEALTH_POTION;
        sut.setItem(item);
        List<Action> expected = List.of(Action.USE_HEALTH_POTION);

        // Act
        List<Action> actual = sut.getAction();

        // Assert
        assertEquals(expected, actual);
    }

    /**
     * Test code COM18
     */
    @Test
    @DisplayName("test if use transforms to get item condition")
    void testTransform() {
        // Arrange
        Sentence sentence = new Sentence();
        sut.setItem(CompilerItem.HEALTH_POTION);

        Existence expected = new Existence();
        expected.setItem(CompilerItem.HEALTH_POTION);

        // Act
        sut.transform(sentence);
        Existence actual = (Existence) sentence.getChildren().get(0);

        // Assert
        assertEquals(expected, actual);
    }

    /**
     * Test code COM19
     */
    @ParameterizedTest
    @MethodSource("provider")
    void testGetChildren(IASTNode expected, Getter getter, Setter setter) {
        testGetChildren(sut, expected, getter, setter);
    }

    /**
     * Test code COM20
     */
    @ParameterizedTest
    @MethodSource("provider")
    void testAddChild(IASTNode expected, Getter getter, Setter setter) {
        testAddChild(sut, expected, getter, setter);
    }

    @Override
    public Volume<IASTNode, Getter, Setter> instructions() {
        return instructions;
    }
}
