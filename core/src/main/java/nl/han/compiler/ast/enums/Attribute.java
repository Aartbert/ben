package nl.han.compiler.ast.enums;

import nl.han.compiler.ast.IASTNode;
import nl.han.compiler.ast.literals.LiteralType;

/**
 * This enum representing various attributes in the game.
 */
public enum Attribute implements IASTNode {
    HEALTH,
    STAMINA,
    POWER,
    ENEMY,
    PLAYER,
    MONSTER;

    /**
     * Returns the type of the attribute that this method is called upon.
     *
     * @return The LiteralType that corresponds with the attribute that this method is called upon.
     */
    public LiteralType getType() {
        return LiteralType.NUMERIC;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Attribute{" +
                "name='" + name() + '\'' +
                '}';
    }
}
