package nl.han.compiler.ast.literals;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import nl.han.compiler.CompilerException;

import java.util.Objects;

/**
 * This class stores a scalar name and represents a scalar literal within an AST.
 */
@Getter
@ToString
public class Scalar implements ILiteral {

    private final double value;

    public Scalar(double value) {
        this.value = value;
    }

    public Scalar(String value) {
        this.value = Integer.parseInt(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiteralType getType() {
        return LiteralType.NUMERIC;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(@NonNull ILiteral ILiteral) {
        if (ILiteral instanceof Scalar sca) return Double.compare(value, sca.value);
        else throw new CompilerException("Literal must be scalar");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    /**
     * {@inheritDoc}
     * Is equal when the value is equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Scalar scalar = (Scalar) o;

        return value == scalar.value;
    }
}
