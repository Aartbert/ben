package nl.han.compiler.ast.literals;

import nl.han.compiler.ast.IASTNode;

/**
 * This interface represents a literal within an AST and requires literals to be able to provide their
 * literal type.
 */
public interface ILiteral extends IASTNode, Comparable<ILiteral> {

    /**
     * Retrieves the attribute type associated with this literal.
     *
     * @return The {@link LiteralType} representing the type of the literal.
     */
    LiteralType getType();
}
