package nl.han.compiler.ast;

import lombok.Getter;
import lombok.Setter;
import nl.han.compiler.ast.actions.IAction;
import nl.han.compiler.ast.expressions.BinaryExpression;
import nl.han.compiler.ast.expressions.IExpression;
import nl.han.compiler.ast.operators.AndOperator;
import nl.han.shared.datastructures.creature.Creature;
import nl.han.shared.datastructures.game.Game;
import nl.han.shared.enums.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class represents a sentence within an AST and stores a list of actions along with an optional condition
 * that - when it's present - should be met for the actions to occur.
 */
@Getter
@Setter
public class Sentence implements IASTNode {

    private IAction operation;
    private IExpression condition;

    /**
     * Determines what actions should be taken based on the current gamestate.
     *
     * @param creature The creature for which the actions are meant
     * @param game The game the creature currently stands in
     * @return A list of actions that should be taken.
     */
    public List<Action> determineActions(Creature creature, Game game) {
        if (condition == null || condition.validate(creature, game)) return operation.getAction();
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void transform(IASTNode node) {
        operation.transform(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IASTNode> getChildren() {
        List<IASTNode> children = new ArrayList<>();

        if (condition != null) children.add(condition);
        if (operation != null) children.add(operation);

        return children;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASTNode addChild(IASTNode child) {
        if (child instanceof IExpression con) {
            if (condition == null) {
                condition = con;
            } else if (!(condition instanceof BinaryExpression)) {
                BinaryExpression expression = new BinaryExpression();
                expression.addChild(condition);
                expression.addChild(new AndOperator());
                expression.addChild(con);
                condition = expression;
            } else {
                condition.addChild(con);
            }
        } else if (child instanceof IAction act) this.operation = act;
        else IASTNode.super.addChild(child);

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(condition, operation);
    }

    /**
     * {@inheritDoc}
     * Is equal when the condition and operation are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sentence sentence = (Sentence) o;

        return Objects.equals(condition, sentence.condition) && Objects.equals(operation, sentence.operation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Sentence";
    }
}
