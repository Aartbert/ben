package nl.han.compiler.ast.expressions;

import lombok.Getter;
import lombok.Setter;
import nl.han.compiler.ast.IASTNode;
import nl.han.compiler.ast.literals.Bool;
import nl.han.compiler.ast.operators.AndOperator;
import nl.han.compiler.ast.operators.Operator;
import nl.han.shared.datastructures.creature.Creature;
import nl.han.shared.datastructures.game.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a binary expression in the abstract syntax tree (AST).
 * A binary expression consists of an operator and two sub-expressions(Condition and binaryExpression).
 */
@Getter
@Setter
public class BinaryExpression implements IExpression {

    private Operator operator;
    private IExpression lhs;
    private IExpression rhs;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validate(Creature creature, Game game) {
        boolean lhsResult = lhs.validate(creature, game);
        boolean rhsResult = rhs.validate(creature, game);

        Bool lhsBool = new Bool(lhsResult);
        Bool rhsBool = new Bool(rhsResult);

        return operator.evaluate(lhsBool, rhsBool);
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
        if (child instanceof IExpression condition) {
            if (lhs == null) {
                lhs = condition;
            } else if (rhs == null) {
                rhs = condition;
            } else if (rhs instanceof BinaryExpression exp) {
                exp.addChild(child);
            } else {
                BinaryExpression expression = new BinaryExpression();
                expression.addChild(rhs);
                expression.addChild(new AndOperator());
                expression.addChild(child);
                rhs = expression;
            }
        } else if (child instanceof Operator op) operator = op;
        else IExpression.super.addChild(child);

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(lhs, operator, rhs);
    }

    /**
     * {@inheritDoc}
     * Is equal when the lhs, operator and rhs are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BinaryExpression binaryExpression = (BinaryExpression) o;

        return Objects.equals(lhs, binaryExpression.lhs) &&
                Objects.equals(operator, binaryExpression.operator) &&
                Objects.equals(rhs, binaryExpression.rhs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Expression";
    }

}
