package nl.han.compiler.ast.operators;

import nl.han.compiler.CompilerException;
import nl.han.compiler.ast.enums.Attribute;
import nl.han.compiler.ast.literals.Bool;
import nl.han.compiler.ast.literals.ILiteral;
import nl.han.compiler.ast.literals.LiteralType;

/**
 * This class represents an 'and' operator within an AST and is responsible for logic regarding 'and' operations.
 */
public class AndOperator extends Operator {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean evaluate(ILiteral lhs, ILiteral rhs) {
        if (lhs instanceof Bool l && rhs instanceof Bool r) return l.isValue() && r.isValue();
        throw new CompilerException("Literal must be a boolean");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void check(Attribute attribute, ILiteral value) {
        if (attribute.getType() == LiteralType.BOOL && value.getType() == LiteralType.BOOL) return;
        throw new CompilerException("Both operands in 'And' operations must be of type 'Bool'");
    }
}
