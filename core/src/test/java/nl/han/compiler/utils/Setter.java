package nl.han.compiler.utils;

import nl.han.compiler.ast.IASTNode;

/**
 * Functional interface to represent the set function of an {@link IASTNode}.
 *
 * @author Tom Gerritsen
 */
@FunctionalInterface
public interface Setter {

    void set(IASTNode node);
}
