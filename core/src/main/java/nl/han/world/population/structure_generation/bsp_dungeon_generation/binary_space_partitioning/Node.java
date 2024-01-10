package nl.han.world.population.structure_generation.bsp_dungeon_generation.binary_space_partitioning;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a node within a tree and is responsible for all logic regarding all nodes regardless of their
 * position in the tree. This class is also used to implement a binary space partitioning algorithm.
 *
 * @author Lucas van Steveninck
 */
@Getter
@Setter
@AllArgsConstructor
public abstract class Node<T, U> {
    private T value;

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        toString(stringBuilder, 0);
        return stringBuilder.toString();
    }

    /**
     * This method builds a string that represents this node and its children. The string is built using a string
     * builder that is also responsible for storing the string. An indent is used to recursively space out children.
     *
     * @param stringBuilder A pointer to a stringbuilder that should be used to build and store the string.
     * @param indent An integer that represents the amount of space that new lines should start with.
     */
    public abstract void toString(StringBuilder stringBuilder, int indent);

    /**
     * This method builds a list of every leaf node that can be reached through this node.
     *
     * @return A list of every leaf node that can be reached through this node.
     */
    public List<LeafNode<T, U>> getLeafNodes() {
        List<LeafNode<T, U>> leafNodes = new ArrayList<>();
        if (this instanceof ParentNode<T, U> internal) {
            leafNodes.addAll(internal.getLeft().getLeafNodes());
            leafNodes.addAll(internal.getRight().getLeafNodes());
        }
        if (this instanceof LeafNode<T, U> leaf) {
            leafNodes.add(leaf);
        }
        return leafNodes;
    }
}
