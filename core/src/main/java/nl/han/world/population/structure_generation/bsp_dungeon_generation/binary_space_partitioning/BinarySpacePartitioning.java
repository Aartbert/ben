package nl.han.world.population.structure_generation.bsp_dungeon_generation.binary_space_partitioning;

import lombok.Setter;
import nl.han.shared.utils.random.COCRandom;
import nl.han.shared.utils.random.ICOCRandom;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for logic regarding the binary space partitioning algorithm.
 *
 * @author Lucas van Steveninck
 */
@Setter
public class BinarySpacePartitioning {
    private ICOCRandom random;
    private int currentLeafNodes;

    public BinarySpacePartitioning() {
        this.random = new COCRandom();
    }

    public BinarySpacePartitioning(ICOCRandom random) {
        this.random = random;
    }

    /**
     * Splits an initial room into a tree of sub-rooms using binary space partitioning.
     *
     * @param room The room that should be partitioned into a tree.
     * @param minRoomWidth The minimum width for a sub-room.
     * @param minRoomHeight The minimum height for a sub-room.
     * @param maxLeafNodes The maximum number of leaf nodes that are allowed to be created.
     * @return A binary tree of which the root is equal to the original room that was provided.
     * @throws IllegalArgumentException when the binary space partitioning algorithm can't be executed using
     * the parameters that were provided because their values are illegal.
     */
    public Tree<Room, Room> getTree(Room room, int minRoomWidth, int minRoomHeight, int maxLeafNodes) {
        if (maxLeafNodes <= 0 || minRoomWidth <= 0 || minRoomHeight <= 0) {
            throw new IllegalArgumentException("Binary space partitioning always results in at least one 1x1 room.");
        }
        currentLeafNodes = 1;
        return new Tree<>(partition(room, minRoomWidth, minRoomHeight, maxLeafNodes));
    }

    /**
     * Randomly partitions a room into sub-rooms using binary space partitioning. Sub-rooms are split into sub-rooms of
     * their own until it is no longer possible to split a room into 2 halves that meet the minimum height
     * and width requirements.
     *
     * @param room The room that should be partitioned.
     * @param minRoomWidth The minimum width for a sub-room.
     * @param minRoomHeight The minimum height for a sub-room.
     * @param maxLeafNodes The maximum number of leaf nodes that are allowed to be created.
     * @return A node that represents the room and the sub-rooms it was split into.
     */
    protected Node<Room, Room> partition(Room room, int minRoomWidth, int minRoomHeight, int maxLeafNodes) {
        List<Room> subRooms;

        if (random.nextFloat() < 0.5) {
            subRooms = attemptRandomHorizontalSplit(room, minRoomHeight, maxLeafNodes);
        } else {
            subRooms = attemptRandomVerticalSplit(room, minRoomWidth, maxLeafNodes);
        }

        if(subRooms.isEmpty()) return new LeafNode<>(room);
        Node<Room, Room> left = partition(subRooms.get(0), minRoomWidth, minRoomHeight, maxLeafNodes);
        Node<Room, Room> right = partition(subRooms.get(1), minRoomWidth, minRoomHeight, maxLeafNodes);
        return new ParentNode<>(room, left, right);
    }

    /**
     * This method horizontally splits a room into 2 rooms provided that it is possible to split this room into 2 rooms
     * that meet the minimum height requirement.
     *
     * @param room The room that should be split.
     * @param minRoomHeight The minimum height for a sub-room.
     * @param maxLeafNodes The maximum number of leaf nodes that are allowed to be created.
     * @return A list of the rooms that were created as a result of splitting the original room.
     */
    public List<Room> attemptRandomHorizontalSplit(Room room, int minRoomHeight, int maxLeafNodes) {
        if (room.getHeight() < minRoomHeight * 2 || currentLeafNodes + 1 > maxLeafNodes) return new ArrayList<>();
        currentLeafNodes++;
        int origin = room.getY() + minRoomHeight;
        int bound = room.getYBoundary() - minRoomHeight;
        int splittingPoint = random.nextInt(origin, bound + 1);
        return room.splitHorizontally(splittingPoint);
    }

    /**
     * This method vertically splits a room into 2 rooms provided that it is possible to split this room into 2 rooms
     * that meet the minimum width requirement.
     *
     * @param room The room that should be split.
     * @param minRoomWidth The minimum width for a sub-room.
     * @param maxLeafNodes The maximum number of leaf nodes that are allowed to be created.
     * @return A list of the rooms that were created as a result of splitting the original room.
     */
    public List<Room> attemptRandomVerticalSplit(Room room, int minRoomWidth, int maxLeafNodes) {
        if (room.getWidth() < minRoomWidth * 2 || currentLeafNodes + 1 > maxLeafNodes) return new ArrayList<>();
        currentLeafNodes++;
        int origin = room.getX() + minRoomWidth;
        int bound = room.getXBoundary() - minRoomWidth;
        int splittingPoint = random.nextInt(origin, bound + 1);
        return room.splitVertically(splittingPoint);
    }
}
