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
 * The Retreat class represents an action of retreating.
 */
public class Retreat implements IAction {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Action> getAction() {
        return List.of(Action.RUN);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void transform(Sentence sentence){
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

        Retreat retreat = (Retreat) o;

        return getClass().isAssignableFrom(retreat.getClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Retreat";
    }
}
