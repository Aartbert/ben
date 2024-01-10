package nl.han.world.population.structure_generation.bsp_dungeon_generation.binary_space_partitioning;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * This class represents a parent node in a tree and is responsible for keeping track of its own child nodes. This class
 * is used to implement a binary space partitioning algorithm.
 *
 * @author Lucas van Steveninck
 */
@Getter
@Setter
public class ParentNode<T, U> extends Node<T, U> {
    private Node<T, U> left;
    private Node<T, U> right;

    public ParentNode(T value, Node<T, U> left, Node<T, U> right) {
        super(value);
        this.left = left;
        this.right = right;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void toString(StringBuilder stringBuilder, int indent) {
        stringBuilder.append("    ".repeat(indent));
        stringBuilder.append(getValue().toString());
        stringBuilder.append("\n");
        left.toString(stringBuilder, indent + 1);
        right.toString(stringBuilder, indent + 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParentNode<?, ?> that = (ParentNode<?, ?>) o;
        return Objects.equals(left, that.left) && Objects.equals(right, that.right);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }
}
