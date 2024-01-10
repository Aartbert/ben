package nl.han.world.population.structure_generation.bsp_dungeon_generation.binary_space_partitioning;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import nl.han.shared.datastructures.world.Coordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class represents a room and its boundaries. It is responsible for all generic logic regarding rooms.
 *
 * @author Lucas van Steveninck
 */
@Getter
@ToString
@RequiredArgsConstructor
public class Room {
    private final Coordinate coordinate;
    private final int width;
    private final int height;

    /**
     * Determines whether a position is contained in this room.
     *
     * @param position The position that should be evaluated.
     * @return A boolean that is true if the position that was provided is contained in this room.
     */
    public boolean contains(Coordinate position) {
        return position.x() >= this.getX() && position.x() < this.getXBoundary() && position.y() >= this.getY() && position.y() < this.getYBoundary();
    }

    /**
     * This method attempts to horizontally split this room into 2 separate rooms.
     *
     * @param splittingPoint The y position where the splitting point of the room should be.
     * @return A list of the rooms that where created by splitting the original room. If it is not possible to split
     * the room at the splitting point an empty list is returned.
     */
    public List<Room> splitHorizontally(int splittingPoint) {
        if (!(splittingPoint > getY() && splittingPoint < getYBoundary() - 1)) return new ArrayList<>();
        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room(coordinate, width, splittingPoint - getY()));
        rooms.add(new Room(new Coordinate(getX(), splittingPoint), width, getYBoundary() - splittingPoint));
        return rooms;
    }

    /**
     * This method attempts to vertically split this room into 2 separate rooms.
     *
     * @param splittingPoint The x position where the splitting point of the room should be.
     * @return A list of the rooms that where created by splitting the original room. If it is not possible to split
     * the room at the splitting point an empty list is returned.
     */
    public List<Room> splitVertically(int splittingPoint) {
        if (!(splittingPoint > getX() && splittingPoint < getXBoundary() - 1)) return new ArrayList<>();
        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room(coordinate, splittingPoint - getX(), height));
        rooms.add(new Room(new Coordinate(splittingPoint, getY()), getXBoundary() - splittingPoint, height));
        return rooms;
    }

    /**
     * This method calculates where the horizontal boundary of this room lies by adding the width of this room to the
     * x-coordinate of this room.
     *
     * @return An integer that represents where the horizontal boundary of this room lies.
     */
    public int getXBoundary() {
        return getX() + width;
    }

    /**
     * This method calculates where the vertical boundary of this room lies by adding the height of this room to the
     * y-coordinate of this room.
     *
     * @return An integer that represents where the vertical boundary of this room lies.
     */
    public int getYBoundary() {
        return getY() + height;
    }

    /**
     * This method retrieves the x-coordinate of this room based on its position.
     *
     * @return An integer that represents the x-coordinate of this room.
     */
    public int getX() {
        return coordinate.x();
    }

    /**
     * This method retrieves the y-coordinate of this room based on its position.
     *
     * @return An integer that represents the y-coordinate of this room.
     */
    public int getY() {
        return coordinate.y();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room room)) return false;
        return width == room.width && height == room.height && Objects.equals(coordinate, room.coordinate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(coordinate, width, height);
    }
}
