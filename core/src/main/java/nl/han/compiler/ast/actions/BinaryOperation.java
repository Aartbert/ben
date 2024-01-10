package nl.han.compiler.ast.actions;

import lombok.Getter;
import lombok.Setter;
import nl.han.compiler.ast.IASTNode;
import nl.han.compiler.ast.Sentence;
import nl.han.compiler.ast.operators.Operator;
import nl.han.shared.enums.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class represents a binary operation within an AST and stores an operator along with two actions.
 */
@Getter
@Setter
public class BinaryOperation implements IAction {

    private Operator operator;
    private IAction lhs;
    private IAction rhs;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Action> getAction() {
        List<Action> lhsAction = lhs.getAction();
        List<Action> rhsAction = rhs.getAction();

        lhsAction.addAll(rhsAction);

        return lhsAction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void transform(Sentence sentence) {

        lhs.transform(sentence);
        rhs.transform(sentence);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IASTNode> getChildren() {
        List<IASTNode> children = new ArrayList<>();

        if (lhs != null) children.add(lhs);
        if (operator != null) children.add(operator);
        if (rhs != null) children.add(rhs);

        return children;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASTNode addChild(IASTNode child) {
        if (child instanceof IAction action && this.lhs == null) this.lhs = action;
        else if (child instanceof Operator ope) this.operator = ope;
        else if (child instanceof IAction operation) this.rhs = operation;
        else IAction.super.addChild(child);

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(operator, lhs, rhs);
    }

    /**
     * {@inheritDoc}
     * Is equal when the operator, lhs and rhs are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BinaryOperation binaryOperation = (BinaryOperation) o;

        return Objects.equals(operator, binaryOperation.operator) &&
                Objects.equals(lhs, binaryOperation.lhs) &&
                Objects.equals(rhs, binaryOperation.rhs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "BinaryOperation";
    }
}
