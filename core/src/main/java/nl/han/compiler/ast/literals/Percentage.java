package nl.han.compiler.ast.literals;

import lombok.NonNull;
import lombok.ToString;
import nl.han.compiler.CompilerException;

import java.util.Objects;

/**
 * This class stores a percentage and represents a percentage literal within an AST.
 */
@ToString
public class Percentage implements ILiteral {

    private final int value;

    public Percentage(int value) {
        this.value = value;
    }

    public Percentage(String value) {
        this.value = Integer.parseInt(value.replaceAll("[^0-9.]", ""));
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
        if (ILiteral instanceof Percentage per) return Integer.compare(value, per.value);
        else throw new CompilerException("Literal must be percentage");
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

        Percentage percentage = (Percentage) o;

        return value == percentage.value;
    }
}
