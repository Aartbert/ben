package nl.han.compiler.ast;

import nl.han.compiler.CompilerException;

import java.util.ArrayList;
import java.util.List;

/**
 * This interface represents a node within an AST and is responsible for logic that all nodes in an AST should be
 * able to access.
 */
public interface IASTNode {

    /**
     * Transforms the ASTNode that this method is called upon.
     * @param sentence The ASTNode representing the sentence.
     */
    default void transform(IASTNode sentence) { }

    /**
     * Checks whether the ASTNode that this method is called upon is valid.
     *
     * @throws CompilerException When the ASTNode that th is method is called upon is not valid.
     */
    default void check() {
    }

    /**
     * Retrieves a list of child nodes of the AST node.
     *
     * @return A list of ASTNode representing the children of the current node.
     */
    default List<IASTNode> getChildren() {
        return new ArrayList<>();
    }

    /**
     * Adds a child node to the AST node.
     *
     * @param child The ASTNode to be added as a child.
     * @return The added ASTNode.
     * @throws CompilerException if the addition is not allowed.
     */
    default IASTNode addChild(IASTNode child) {
        throw new CompilerException("Cannot add '" + child.getClass().getSimpleName() + "' to '" + getClass().getSimpleName() + "'.");
    }
}
