package nl.han.world.population.structure_generation.bsp_dungeon_generation.binary_space_partitioning;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * This class represents a leaf node in a tree and is capable of holding an exclusive leaf value in addition to the value that
 * general nodes are able to store. This class is used to implement a binary space partitioning algorithm.
 *
 * @author Lucas van Steveninck
 */
@Getter
@Setter
public class LeafNode<T, U> extends Node<T, U> {
    private U leafValue;

    public LeafNode(T value) {
        super(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void toString(StringBuilder stringBuilder, int indent) {
        stringBuilder.append("    ".repeat(indent));
        stringBuilder.append(getValue().toString());
        stringBuilder.append("\n");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeafNode<?, ?> leafNode = (LeafNode<?, ?>) o;
        return Objects.equals(leafValue, leafNode.leafValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(leafValue);
    }
}
