package nl.han.pathfinding;

import nl.han.pathfinding.exception.NoPathFoundException;
import nl.han.pathfinding.grid.Grid;
import nl.han.pathfinding.grid.Node;
import nl.han.shared.datastructures.world.Chunk;
import nl.han.shared.datastructures.world.Tile;
import nl.han.shared.enums.Action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implements A* algorithm as one of the three pathfinding prototypes for
 * pathfinding.
 *
 * @see Node
 * @see Grid
 */
public class AStar implements IPathFindingAlgorithm {
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Action> findPath(Chunk chunk, Tile start, Tile end) throws NoPathFoundException {
        Node startNode = new Node(start);
        Node endNode = new Node(end);
        Grid grid = new Grid(chunk);

        startNode.setG(0);
        startNode.setH(calculateHeuristic(startNode, startNode, endNode));
        startNode.setF(startNode.getG() + startNode.getH());

        List<Node> open = new ArrayList<>(List.of(startNode));

        while (!open.isEmpty()) {
            Node node = findBestNodeFrom(open);

            if (node.getX() == endNode.getX() && node.getY() == endNode.getY())
                return getShortestPath(node);

            open.remove(node);

            for (Node neighbour : node.getNeighbours(grid)) {
                addToListIfBetter(node, startNode, endNode, neighbour, open);
            }
        }

        throw new NoPathFoundException();
    }

    /**
     * Checks if a neighbour of a node has a better f-value than itself. And if so,
     * adds it to the list.
     *
     * @param node      node of which the values will be compared to the neighbour.
     * @param endNode   node the path should end at.
     * @param neighbour neighbour of which the values will be compared to the node.
     * @param open      list of open nodes, which the neighbour will be added to if
     *                  it is better.
     * @author Sven van Hoof
     */
    private void addToListIfBetter(Node node, Node startNode, Node endNode, Node neighbour, List<Node> open) {
        if (!neighbour.isPassable())
            return;

        float g = node.getG() + neighbour.getWeight();
        float h = calculateHeuristic(neighbour, startNode, endNode);
        float f = g + h;

        if (neighbour.getF() > f) {
            neighbour.setPreviousNode(node);
            neighbour.setG(g);
            neighbour.setH(h);
            neighbour.setF(f);

            if (!open.contains(neighbour)) {
                open.add(neighbour);
            }
        }
    }

    /**
     * Returns the node with the best f-value in the list. If the f-value is equal,
     * h-value is used as the tiebreaker.
     *
     * @param list list containing all the nodes to get the best of.
     * @return node with the lowest f-value. Returns null if list is empty.
     * @author Sven van Hoof
     */
    public Node findBestNodeFrom(List<Node> list) {
        if (list.isEmpty())
            return null;
        Node bestNode = list.get(0);

        for (Node node : list) {
            if (bestNode.getF() > node.getF()
                    || bestNode.getF() == node.getF() && bestNode.getH() > node.getH()) {
                bestNode = node;
            }
        }

        return bestNode;
    }

    /**
     * Returns the full path for a given endNode.
     *
     * @param endNode ending node of a path.
     * @return path for the given endNode.
     * @author Sven van Hoof
     */
    public List<Action> getShortestPath(Node endNode) {
        List<Node> path = new ArrayList<>();
        Node currentNode = endNode;

        while (currentNode != null) {
            path.add(currentNode);
            currentNode = currentNode.getPreviousNode();
        }

        return convertPathToActions(path);
    }

    /**
     * Calculates the Manhattan distance between two nodes.
     *
     * @param node      first node.
     * @param otherNode other node.
     * @return Manhattan distance between the two nodes.
     * @author Sven van Hoof
     */
    public int calculateManhattanDistance(Node node, Node otherNode) {
        int differenceX = Math.abs(node.getX() - otherNode.getX());
        int differenceY = Math.abs(node.getY() - otherNode.getY());

        return differenceX + differenceY;
    }

    /**
     * Calculates the heuristic-value for a given node. Adds the cross-product multiplied by a
     * small number to the result of the Manhattan distance to create more human-like motions.
     *
     * @param currentNode the node to calculate the heuristic for.
     * @param startNode   the node the algorithm started at.
     * @param endNode     the destination of the algorithm.
     * @return a heuristic-value
     * @author Sven van Hoof
     */
    public float calculateHeuristic(Node currentNode, Node startNode, Node endNode) {
        int dx1 = currentNode.getX() - endNode.getX();
        int dy1 = currentNode.getY() - endNode.getY();
        int dx2 = startNode.getX() - endNode.getX();
        int dy2 = startNode.getY() - endNode.getY();
        int cross = Math.abs(dx1 * dy2 - dx2 * dy1);

        return calculateManhattanDistance(currentNode, endNode) + cross * 0.001f;
    }

    /**
     * Changes the display character of the start and end nodes to S and E
     * respectively. Also changes the start node's
     * weight to zero.
     *
     * @param startNode start node.
     * @param endNode   end node.
     * @author Sven van Hoof
     */
    public void setStartAndEndNodes(Node startNode, Node endNode) {
        startNode.setG(0);
        startNode.setH(calculateManhattanDistance(startNode, endNode));
        startNode.setF(startNode.getG() + startNode.getH());
    }

    /**
     * Converts a path to a list of MovementActions
     *
     * @param path list of nodes that forms a path.
     * @return list of MovementActions.
     * @author Jasper Kooy
     */
    public List<Action> convertPathToActions(List<Node> path) {
        List<Action> actions = new ArrayList<>();
        Collections.reverse(path);

        for (int i = 0; i < path.size() - 1; i++) {
            Node currentNode = path.get(i);
            Node nextNode = path.get(i + 1);

            if (currentNode.getX() + 1 == nextNode.getX()) {
                actions.add(Action.MOVE_RIGHT);
            } else if (currentNode.getX() - 1 == nextNode.getX()) {
                actions.add(Action.MOVE_LEFT);
            } else if (currentNode.getY() + 1 == nextNode.getY()) {
                actions.add(Action.MOVE_DOWN);
            } else if (currentNode.getY() - 1 == nextNode.getY()) {
                actions.add(Action.MOVE_UP);
            }
        }

        return actions;
    }
}
