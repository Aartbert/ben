package nl.han.shared.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;

/**
 * Enum representation for different types of ItemData.
 * It defines different item attributes such as their value,
 * spawnChance, and type.
 * <br/>
 * Each item has an associated value, spawnChance and ItemType
 * <br/>
 * ItemData can be used where different items with distinct values,
 * spawn chances and ItemType are required.
 *
 * @author Jordan Geurtsen
 * @see ItemType
 */
@Getter
@AllArgsConstructor
public enum ItemData {
    SMALL_HEALTH_POTION("Small Health Potion", 3, 1, ItemType.HEALTH, '⟇', Color.WHITE, Color.BLUE),
    MEDIUM_HEALTH_POTION("Medium Health Potion", 6, 2, ItemType.HEALTH,'⟑', Color.WHITE, Color.BLUE),
    BIG_HEALTH_POTION("Big Health Potion", 9, 3, ItemType.HEALTH,'⟐', Color.WHITE, Color.BLUE);

    private final String name;
    private final int value;
    private final int spawnWeight;
    private final ItemType type;
    private final Character character;
    private final Color characterColor;
    private final Color backgroundColor;
}
