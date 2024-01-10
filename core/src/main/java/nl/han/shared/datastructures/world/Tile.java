package nl.han.shared.datastructures.world;

import lombok.Getter;
import lombok.Setter;
import nl.han.shared.datastructures.Item;
import nl.han.shared.enums.TileType;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a tile in the world.
 *
 * @author Jordan Geurtsen
 * @see TileType
 * @see Coordinate
 * @see Item
 */
@Getter
@Setter
public class Tile {
    private TileType type;
    private List<Item> items = new ArrayList<>();
    private final Coordinate coordinate;

    public Tile(TileType type, Coordinate coordinate) {
        this.type = type;
        this.coordinate = coordinate;
    }

    /**
     * Returns the character representing the type of the tile.
     *
     * @return Character representation of the tile type
     * @author Jordan Geurtsen
     * @see TileType
     */
    public Character getCharacter() {
        return type.getCharacter();
    }

    /**
     * Returns the color of the character representing the tile's type.
     *
     * @return Color of the character
     * @author Jordan Geurtsen
     * @see TileType
     */
    public Color getCharacterColor() {
        return type.getCharacterColor();
    }

    /**
     * Returns the background color of the tile based on its type.
     *
     * @return Color for the background
     * @author Jordan Geurtsen
     * @see TileType
     */
    public Color getBackgroundColor() {
        return type.getBackgroundColor();
    }

    /**
     * Returns the movement cost of the tile.
     *
     * @return Movement cost of the tile
     * @author Jordan Geurtsen
     * @see TileType
     */
    public int getMovementCost() {
        return type.getMovementCost();
    }

    /**
     * Returns a boolean indicating if the tile is passable or not.
     *
     * @return Boolean indicating the passability of the tile
     * @author Jordan Geurtsen
     * @see TileType
     */
    public boolean isPassable() {
        return type.isPassable();
    }

    /**
     * Returns the visual representation of the tile as a string.
     *
     * @return String representation of the tile
     * @author Jordan Geurtsen
     * @see TileType
     */
    @Override
    public String toString() {
        return type.getCharacter().toString();
    }

    /**
     * Returns whether the tile has items or not.
     *
     * @return boolean indicating if the tile has items
     * @author Justin Slijkhuis
     */
    public boolean hasItems() {
        return !items.isEmpty();
    }

    /**
     * Adds an item to this tile.
     *
     * @param item The item that should be added to the tile.
     * @author Lucas van Steveninck
     */
    public void addItem(Item item) {
        if (!isPassable()) return;

        items.add(item);
    }

    /**
     * Removes an item from the players inventory, if it exists.
     *
     * @param item The item to be removed.
     * @author Rieke Jansen, Justin Slijkhuis
     */
    public void removeItem(Item item) {
        items.remove(item);
    }

    /**
     * Removes an item from the players inventory.
     *
     * @param index The index of the item to be removed.
     * @author Rieke Jansen
     */
    public void removeItem(int index) {
        if (items.size() > index) {
            items.remove(index);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return type == tile.type && Objects.equals(items, tile.items) && Objects.equals(coordinate, tile.coordinate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(type, items, coordinate);
    }

    /**
     * Returns the name of the tile.
     *
     * @param tile The tile to get the name of.
     * @return The name of the tile.
     */
    public String getTileName(Tile tile) {
        String tileName = tile.getType().toString();
        return tileName.replaceAll("_", " ").toLowerCase();
    }

    /**
     * Returns the name of the tile.
     *
     * @return The name of the tile.
     */
    public String getName() {
        return type.getCharacter() + " " + getTileName(this);
    }
}