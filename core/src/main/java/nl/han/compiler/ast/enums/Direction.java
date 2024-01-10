package nl.han.compiler.ast.enums;

import nl.han.compiler.ast.IASTNode;

/**
 * This enum representing directions in the game.
 */
public enum Direction implements IASTNode {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Direction{" +
                "name='" + name() + '\'' +
                '}';
    }
}
