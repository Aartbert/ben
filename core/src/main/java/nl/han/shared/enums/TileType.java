package nl.han.shared.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;

/**
 * Represents different types of Tiles within the application.
 * <br/>
 * Each tile type is defined by a character, a color for the character, a background color and a movement cost.
 * This cost is used to determine if a TileType is passable.
 *
 * @author Jordan Geurtsen
 * @see Color
 * @see Character
 */
@Getter
@AllArgsConstructor
public enum TileType {

    EMPTY(' ', TileColor.EMPTY.getColor(), 1),
    FOREST('#', TileColor.FOREST.getColor(), 4),
    GRASS('*', TileColor.GRASS.getColor(), 1),
    HILL('◠', TileColor.HILL.getColor(), 3),
    ICE('+', TileColor.ICE.getColor(), 2),
    MOUNTAIN('▲', TileColor.MOUNTAIN.getColor(), 0),
    RAINFOREST('*', TileColor.RAINFOREST.getColor(), 5),
    SAND('▒', TileColor.SAND.getColor(), 2),
    SNOW('▓', TileColor.SNOW.getColor(), 2),
    SAVANNAH('·', TileColor.SAVANNAH.getColor(), 1),
    TUNDRA('#', TileColor.TUNDRA.getColor(), 1),
    WALL('█', TileColor.WALL.getColor(), 0),
    WATER('~', TileColor.WATER.getColor(), 0),
    STAIRS_UP('⤊', TileColor.STAIRS_UP.getColor(), 1),
    STAIRS_DOWN('⤋', TileColor.STAIRS_DOWN.getColor(), 1),
    DUNGEON_FLOOR('#', TileColor.DUNGEON_FLOOR.getColor(), 1),
    DUNGEON_WALL('▓', TileColor.DUNGEON_WALL.getColor(), 0);

    private final Character character;
    private final Color backgroundColor;
    private final int movementCost;

    /**
     * Checks whether a tile is passable or not based on its movement cost.
     * <br/>
     * If a TileType's movement cost is greater than 0, it is said to be passable.
     *
     * @return True if the TileType is passable, else false.
     * @author Jordan Geurtsen
     */
    public boolean isPassable() {
        return movementCost > 0;
    }

    /**
     * Returns the color of the character of the TileType. <br/>
     * This will be a darker version of the background color so that the character is visible.
     *
     * @return The color of the character of the TileType.
     */
    public Color getCharacterColor() {
        return backgroundColor.darker();
    }
}