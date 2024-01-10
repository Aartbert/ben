package nl.han.compiler.ast.expressions;

import nl.han.compiler.ast.IASTNode;
import nl.han.shared.datastructures.creature.Creature;
import nl.han.shared.datastructures.game.Game;

/**
 * Interface representing an expression within the Abstract Syntax Tree (AST).
 * Implementations of this interface are used to evaluate conditions or expressions
 * in the context of a game state represented by a {@link Creature}.
 */
public interface IExpression extends IASTNode {

    /**
     * Validates the expression against the provided {@link Creature}.
     *
     * @param creature The creature representing the game state.
     * @return {@code true} if the expression is valid for the given creature, {@code false} otherwise.
     */
    boolean validate(Creature creature, Game game);
}
