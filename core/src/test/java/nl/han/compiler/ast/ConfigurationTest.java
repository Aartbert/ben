package nl.han.compiler.ast;

import nl.han.compiler.utils.Getter;
import nl.han.compiler.utils.Setter;
import nl.han.compiler.utils.Volume;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;

import static nl.han.compiler.utils.Point.point;

/**
 * Testing class for {@link Configuration}.
 * @see <a href="https://confluenceasd.aimsites.nl/display/ASDS1G2/Testrapport+Onderzoek+Programmeren+Agents">Testrapport</a>
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConfigurationTest implements IASTNodeTest {

    private Configuration sut;

    private final Getter getter = () -> sut.getSentences().isEmpty() ? null : sut.getSentences().get(0);

    private final Volume<IASTNode, Getter, Setter> instructions = Volume.of(
            point(new Sentence(), getter, node -> sut.setSentences(new ArrayList<>(List.of((Sentence) node))))
    );

    @BeforeEach
    void setup() {
        sut = new Configuration();
    }

    /**
     * Test code COM70
     */
    @ParameterizedTest
    @DisplayName("test if a child can be retrieved from a configuration")
    @MethodSource("provider")
    void testGetChildren(IASTNode expected, Getter getter, Setter setter) {
        testGetChildren(sut, expected, getter, setter);
    }

    /**
     * Test code COM71
     */
    @ParameterizedTest
    @DisplayName("test if a child can be added to a configuration")
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
