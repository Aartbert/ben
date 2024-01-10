package nl.han.compiler.ast.operators;

import nl.han.compiler.CompilerException;
import nl.han.compiler.ast.IASTNode;
import nl.han.compiler.ast.enums.Attribute;
import nl.han.compiler.ast.literals.ILiteral;


/**
 * This class represents an operator within an AST and requires operators that extend it to implement logic regarding
 * their specific operations.
 */
public abstract class Operator implements IASTNode {

    /**
     * Evaluates two literals within the operator to return true of false
     *
     * @param lhs an ILiteral that is used as the left half when evaluating the result of the operation.
     * @param rhs an ILiteral that is used as the right half when evaluating the result of the operation.
     * @return The boolean that is the result of the evaluation.
     */
    public abstract boolean evaluate(ILiteral lhs, ILiteral rhs);

    /**
     * Checks whether this operator being applied to two values that are to be provided would make for a valid operation.
     *
     * @param attribute The attribute to which the operator is applied.
     * @param value     The literal name representing the right operand.
     * @throws CompilerException When the operation that is being checked is not a valid operation.
     */
    public abstract void check(Attribute attribute, ILiteral value);

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

        Operator operator = (Operator) o;

        return getClass().isAssignableFrom(operator.getClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
