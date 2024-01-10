package nl.han.compiler.ast.enums;

import nl.han.compiler.ast.IASTNode;

/**
 * This enum representing various items in the game.
 */
public enum CompilerItem implements IASTNode {
    HEALTH_POTION;

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "CompilerItem{" +
                "name='" + name() + '\'' +
                '}';
    }
}
