package nl.han.shared.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * This is a representation of the different game modes available within the project.
 * <br/>
 * Each game mode can have specific attributes depending on whether it supports teams or has respawning enabled.
 *
 * @author Jordan Geurtsen
 */
@Getter
@AllArgsConstructor
public enum GameMode {
    LMS(false, false),
    CTF(true, true);

    private final boolean hasTeams;
    private final boolean hasRespawn;
}
