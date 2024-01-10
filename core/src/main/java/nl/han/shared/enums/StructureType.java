package nl.han.shared.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The {@code StructureType} enum describes the different types of structures, like a tower or a staircase.
 * Each type of structure is associated with a two-dimensional array of {@link TileType}, representing
 * the blueprint of the structure.
 * <br/>
 * An instance of {@code StructureType} has their unique tile blueprint.
 *
 * @author Jordan Geurtsen
 * @see TileType
 */
@Getter
@AllArgsConstructor
public enum StructureType {
    DUNGEON_STAIRS_UP(
            new TileType[][]{
                    {
                            TileType.STAIRS_UP
                    }
            }
    ),
    DUNGEON_STAIRS_DOWN(
            new TileType[][]{
                    {
                            TileType.STAIRS_DOWN
                    }
            }
    ),
    IVORYTOWER(
            new TileType[][]{
                    {
                            TileType.WALL, TileType.WALL, TileType.WALL, TileType.WALL
                    },
                    {
                            TileType.WALL, TileType.SNOW, TileType.SNOW, TileType.WALL
                    },
                    {
                            TileType.STAIRS_DOWN, TileType.SNOW, TileType.SNOW, TileType.WALL
                    },
                    {
                            TileType.WALL, TileType.SNOW, TileType.SNOW, TileType.WALL
                    },
                    {
                            TileType.WALL, TileType.WALL, TileType.WALL, TileType.WALL
                    }
            }
    ),
    GOLDPLATETOWER(
            new TileType[][]{

                    {
                            TileType.WALL, TileType.WALL, TileType.WALL, TileType.WALL, TileType.WALL, TileType.WALL, TileType.WALL
                    },
                    {
                            TileType.WALL, TileType.SAND, TileType.SAND, TileType.SAND, TileType.SAND, TileType.SAND, TileType.WALL
                    },
                    {
                            TileType.WALL, TileType.SAND, TileType.SAND, TileType.SAND, TileType.SAND, TileType.SAND, TileType.WALL
                    },
                    {
                            TileType.WALL, TileType.WALL, TileType.WALL, TileType.STAIRS_DOWN, TileType.WALL, TileType.WALL, TileType.WALL
                    }
            }
    );

    private final TileType[][] template;
}
