package nl.han.pathfinding.grid;

import lombok.Getter;
import lombok.Setter;
import nl.han.shared.datastructures.world.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single tile in the grid. Also stores pathfinding data.
 */
@Getter
@Setter
public class Node {
    private final int x;
    private final int y;
    private final int weight;
    private boolean passable;
    private char display;
    private Node previousNode;
    /**
     * The f-value of a node is the sum of g and h-values. A lower f-value means a
     * node is considered the better path.
     */
    private float f = Float.MAX_VALUE;
    /**
     * The g-value of a node is the total cost of moving from the start tile to the
     * current node.
     */
    private float g = Float.MAX_VALUE;
    /**
     * The h-value is the heuristic value. Which is the manhattan distance between
     * two nodes. This will be larger for a
     * node that is further from the end node.
     */
    private float h = Float.MAX_VALUE;

    /**
     * Create a node
     *
     * @param x        the x position of the node
     * @param y        the y position of the node
     * @param weight   the weight of the node
     * @param passable whether the node is passable or not
     * @param display  the display of the node
     */
    public Node(int x, int y, int weight, boolean passable, char display) {
        this.x = x;
        this.y = y;
        this.weight = weight;
        this.passable = passable;
        this.display = display;
        this.previousNode = null;
    }

    /**
     * Create a node from a tile
     * 
     * @param tile the tile to create the node from
     */
    public Node(Tile tile) {
        this.x = tile.getCoordinate().x();
        this.y = tile.getCoordinate().y();
        this.weight = tile.getMovementCost();
        this.passable = tile.isPassable();
        this.display = tile.getCharacter();
        this.previousNode = null;
    }

    /**
     * Get the neighbours of a node
     *
     * @param grid the grid to get the neighbours from
     * @return a list of neighbours
     */
    public List<Node> getNeighbours(Grid grid) {
        List<Node> neighbours = new ArrayList<>();

        // possible move directions
        // (up, down, left, right)
        final int[] deltaX = { 0, 0, -1, 1 };
        final int[] deltaY = { -1, 1, 0, 0 };

        for (int i = 0; i < deltaX.length; i++) {
            int newX = x + deltaX[i];
            int newY = y + deltaY[i];

            // check if the new position is within the grid
            if (isValid(grid, newX, newY)) {
                Node neighbor = grid.getNodes()[newY][newX];
                if (neighbor != null) {
                    neighbours.add(neighbor);
                }
            }
        }

        return neighbours;
    }

    /**
     * Check if the given coordinates are within the bounds of the grid.
     *
     * @param grid grid that the coordinates should be in.
     * @param x    x-coordinate of the coordinate that should be in bounds.
     * @param y    y-coordinate of the coordinate that should be in bounds.
     * @return {@code true} if coordinates are in bounds, otherwise {@code false}.
     */
    public boolean isValid(Grid grid, int x, int y) {
        return x >= 0 && x < grid.getNodes()[0].length && y >= 0 && y < grid.getNodes().length;
    }

    /**
     * Represents the Node object. Takes the following variables into account:
     * <ul>
     * <li>x</li>
     * <li>y</li>
     * <li>weight</li>
     * <li>passable</li>
     * <li>display</li>
     * <li>f</li>
     * <li>g</li>
     * <li>h</li>
     * </ul>
     * {@inheritDoc}
     *
     * @return string representation of the node
     */
    @Override
    public String toString() {
        return "Node{" +
                "x=" + x +
                ", y=" + y +
                ", weight=" + weight +
                ", passable=" + passable +
                ", display=" + display +
                ", f=" + f +
                ", g=" + g +
                ", h=" + h +
                '}';
    }

    /**
     * Compares this object's values with another object's values. The values will
     * be compared are:
     * <ul>
     * <li>x</li>
     * <li>y</li>
     * <li>weight</li>
     * <li>passable</li>
     * <li>f</li>
     * <li>g</li>
     * <li>h</li>
     * <li>previousNode</li>
     * </ul>
     *
     * @param o the other object to compare values with.
     * @return {@code true} if all are the same, otherwise {@code false}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Node node = (Node) o;
        return x == node.x && y == node.y && weight == node.weight && passable == node.passable && f == node.f
                && g == node.g && h == node.h && Objects.equals(previousNode, node.previousNode);
    }

    /**
     * Hashes the current object to an integer. The values that will be hashed are:
     * <ul>
     * <li>x</li>
     * <li>y</li>
     * <li>weight</li>
     * <li>passable</li>
     * <li>f</li>
     * <li>g</li>
     * <li>h</li>
     * <li>previousNode</li>
     * </ul>
     * {@inheritDoc}
     *
     * @return hashed integer
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y, weight, passable, previousNode, f, g, h);
    }
}
