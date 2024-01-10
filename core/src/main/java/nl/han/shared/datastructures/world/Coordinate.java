package nl.han.shared.datastructures.world;

import java.util.List;

/**
 * Represents a Coordinate record with two fields x and y.
 *
 * @author Jordan Geurtsen & Lucas van Steveninck
 */
public record Coordinate(int x, int y, int z) {
    /**
     * Constructor for the coordinate class to allow creation of 2 dimensional coordinates.
     *
     * @param x The x position for the coordinate that is being created.
     * @param y The y position for the coordinate that is being created.
     * @author Jochem Kalsbeek & Lucas van Steveninck
     */
    public Coordinate(int x, int y) {
        this(x, y, 0);
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        return "Coordinate{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    /**
     * Calculates the absolute distance between the current {@link Coordinate coordinate} and another
     * {@link Coordinate coordinate}. This method uses a circle calculation to determine the distance.
     *
     * @param coordinate The {@link Coordinate coordinate} to compare the distance with
     * @return A distance {@link Integer int}.
     * @author Justin Slijkhuis
     */
    public double calculateDistance(Coordinate coordinate) {
        return Math.sqrt(Math.pow(coordinate.x - x, 2) + Math.pow(coordinate.y - y, 2));
    }

    /**
     * Returns all adjacent tiles of the current tile in a List.
     *
     * @return A List of adjacent tiles.
     * @author Jasper Kooy
     */
    public List<Coordinate> getAdjacentCoordinates() {
        return List.of(
                new Coordinate(x - 1, y),
                new Coordinate(x + 1, y),
                new Coordinate(x, y - 1),
                new Coordinate(x, y + 1)
        );
    }

    public String toJson() {
        return String.format("{\"x\": %d, \"y\": %d, \"z\": %d}", x, y, z);
    }
}