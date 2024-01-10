package nl.han.compiler.ast.operators;

import nl.han.compiler.CompilerException;
import nl.han.compiler.ast.enums.Attribute;
import nl.han.compiler.ast.literals.ILiteral;
import nl.han.compiler.ast.literals.LiteralType;

/**
 * This class represents a 'greater than' operator within an AST and is responsible for logic regarding
 * 'greater than' operations.
 */
public class GreaterThanOperator extends Operator {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean evaluate(ILiteral lhs, ILiteral rhs) {
        return lhs.compareTo(rhs) > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void check(Attribute attribute, ILiteral value) {
        if (attribute.getType() == LiteralType.NUMERIC && value.getType() == LiteralType.NUMERIC) return;
        throw new CompilerException("Both operands in 'Greater than' operations must be numeric values");
    }
}
