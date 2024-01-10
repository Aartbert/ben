package nl.han.compiler.ast.actions;

import nl.han.compiler.ast.IASTNode;
import nl.han.compiler.ast.Sentence;
import nl.han.compiler.ast.enums.Attribute;
import nl.han.compiler.ast.expressions.Comparison;
import nl.han.compiler.ast.literals.Scalar;
import nl.han.compiler.ast.operators.GreaterThanOperator;
import nl.han.shared.enums.Action;

import java.util.List;

/**
 * This class represents the Wander action in the AST.
 */
public class Wander implements IAction {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Action> getAction() {
        return List.of(Action.MOVE_AROUND);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void transform(Sentence sentence) {
        IASTNode condition = new Comparison()
                .addChild(Attribute.STAMINA)
                .addChild(new GreaterThanOperator())
                .addChild(new Scalar(0));

        sentence.addChild(condition);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * {@inheritDoc}
     * Is equal when the classes are assignable from each other.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Wander wander = (Wander) o;

        return getClass().isAssignableFrom(wander.getClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Wander";
    }
}
