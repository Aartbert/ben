package nl.han.compiler.ast;

import nl.han.compiler.utils.Getter;
import nl.han.compiler.utils.Setter;
import nl.han.compiler.utils.Volume;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public interface IASTNodeTest {

    @SuppressWarnings("unused")
    @DisplayName("test if a child can be retrieved from a node")
    default void testGetChildren(IASTNode sut, IASTNode node, Getter get, Setter set) {
        // Arrange
        set.set(node);

        List<IASTNode> expected = List.of(node);

        // Act
        List<IASTNode> actual = sut.getChildren();

        // Assert
        assertEquals(expected, actual);
    }

    @SuppressWarnings("unused")
    @DisplayName("test if a child can be added to a node")
    default void testAddChild(IASTNode sut, IASTNode expected, Getter get, Setter set) {
        // Act
        sut.addChild(expected);

        IASTNode actual = get.get();

        // Assert
        assertEquals(expected, actual);
    }

    default Stream<Object[]> provider() {
        return instructions().provide();
    }

    /**
     * Returns the volume of instructions associated with this AST node.
     *
     * @return the volume of instructions
     */
    Volume<IASTNode, Getter, Setter> instructions();
}
