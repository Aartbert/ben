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
public enum BotType {
    ZOMBIE(30, 2, 10, 40, 'M', Color.WHITE, new Color(1, 80, 32), "zombie.wav", 1, 50, Color.BLUE),
    SKELETON(20, 2, 20, 25, 'M', Color.BLACK, Color.WHITE, "skeleton.wav", 1, 50, Color.BLUE);

    private final int health;
    private final int power;
    private final int stamina;
    private final int spawnChance;
    private final Character character;
    private final Color characterColor;
    private final Color backgroundColor;
    private final String audioPath;
    private final int audioChance;
    private final int audioCooldown;
    private final Color activeAudioPlaybackBackgroundColor;
}