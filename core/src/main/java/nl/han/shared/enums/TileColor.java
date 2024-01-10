package nl.han.shared.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.awt.*;

@Getter
@RequiredArgsConstructor
public enum TileColor {
    EMPTY(new Color(0, 0, 0)),
    FOREST(new Color(29, 99, 52)),
    GRASS(new Color(75, 161, 72)),
    HILL(new Color(66, 140, 63)),
    ICE(new Color(189, 212, 242)),
    MOUNTAIN(new Color(100, 100, 115)),
    RAINFOREST(new Color(15, 84, 45)),
    SAND(new Color(217, 185, 145)),
    SNOW(new Color(228, 234, 247)),
    SAVANNAH(new Color(221, 180, 118)),
    TUNDRA(new Color(85, 117, 53)),
    WALL(new Color(0, 0, 0)),
    WATER(new Color(64, 138, 201)),
    STAIRS_UP(new Color(0, 255, 0)),
    STAIRS_DOWN(new Color(255, 0, 0)),
    DUNGEON_FLOOR(new Color(128, 128, 128)),
    DUNGEON_WALL(new Color(51, 51, 51));

    private final Color color;
}
