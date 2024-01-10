package nl.han.compiler.ast.literals;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.Objects;

/**
 * This class stores a boolean name and represents a boolean literal within an AST.
 */
@Getter
@ToString
public class Bool implements ILiteral {

    private final boolean value;

    public Bool(boolean value) {
        this.value = value;
    }

    public Bool(String value) {
        this.value = "TRUE".equals(value);
    }

    /**
     * Converts the boolean to an integer bases on its state.
     *
     * @return 1 if true, 0 if false
     */
    public int convert() {
        return value ? 1 : 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiteralType getType() {
        return LiteralType.BOOL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(@NonNull ILiteral ILiteral) {
        if (ILiteral instanceof Bool bool) {
            return value == bool.value ? 0 : 1;
        } else {
            return -1;
        }
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
     * Is equal when the value is the same.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bool bool = (Bool) o;

        return value == bool.value;
    }
}
