package nl.han.compiler.utils;

import nl.han.compiler.ast.IASTNode;

/**
 * Functional interface to represent the get function of an {@link IASTNode}.
 *
 * @author Tom Gerritsen
 */
@FunctionalInterface
public interface Getter {

    IASTNode get();
}
