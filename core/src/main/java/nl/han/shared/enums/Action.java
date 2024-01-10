package nl.han.shared.enums;

/**
 * Defines the set of all the possible actions that can be taken.
 * These basic actions are crucial for the gameplay mechanics in the game.
 * <br/>
 * This enumeration encapsulates all the primary actions at one place, which makes it easier to handle game events.
 *
 * @author Jordan Geurtsen
 */
public enum Action {
    MOVE_UP,
    MOVE_DOWN,
    MOVE_LEFT,
    MOVE_RIGHT,
    MOVE_AROUND,
    RUN,
    ATTACK_MONSTER,
    ATTACK_ENEMY,
    ATTACK_PLAYER,
    SEARCH_ITEM,
    SEARCH_PLAYER,
    SEARCH_MONSTER,
    SEARCH_ENEMY,
    USE_HEALTH_POTION,
    USE_SMALL_HEALTH_POTION,
    USE_MEDIUM_HEALTH_POTION,
    USE_BIG_HEALTH_POTION,
    QUIT_GAME,
    PICK_UP,
    UNKNOWN,
    USE_STAIRCASE,
    INTERACT,
}
