package nl.han.world.population.structure_generation.bsp_dungeon_generation.binary_space_partitioning;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

/**
 * This class represents a tree of nodes and is responsible for all logic regarding trees. This class is used to
 * implement a binary space partitioning algorithm.
 *
 * @author Lucas van Steveninck
 */
@Getter
@Setter
public class Tree<T, U> {
    private Node<T, U> root;
    private List<LeafNode<T, U>> leafNodes;

    public Tree(Node<T, U> root) {
        this.root = root;
        leafNodes = root.getLeafNodes();
    }

    public Tree(Tree<T, U> another) {
        this(another.root);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tree<?, ?> tree = (Tree<?, ?>) o;
        return Objects.equals(root, tree.root) && Objects.equals(leafNodes, tree.leafNodes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(root, leafNodes);
    }
}
