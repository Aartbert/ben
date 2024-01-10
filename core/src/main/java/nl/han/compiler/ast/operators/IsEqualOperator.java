package nl.han.compiler.ast.operators;

import nl.han.compiler.CompilerException;
import nl.han.compiler.ast.enums.Attribute;
import nl.han.compiler.ast.literals.ILiteral;

/**
 * This class represents an 'is equal' operator within an AST and is responsible for logic regarding
 * 'is equal' operations.
 */
public class IsEqualOperator extends Operator {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean evaluate(ILiteral lhs, ILiteral rhs) {
        return lhs.compareTo(rhs) == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void check(Attribute attribute, ILiteral value) {
        if (attribute.getType() == value.getType()) return;
        throw new CompilerException("Both operands in 'Is equal' operations must be values of the same type");
    }
}
