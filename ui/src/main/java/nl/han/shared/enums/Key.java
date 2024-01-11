package nl.han.shared.enums;

/**
 * This enum represents the key that is pressed.
 * All supported keys should be listed here.
 * The UNKNOWN key is used when a key is pressed that is not supported.
 * 
 * @see nl.han.screens.game.TerminalConsole#mapKeyStrokeToKey(com.googlecode.lanterna.input.KeyStroke)
 * @author Vasil Verdouw
 */
public enum Key {
    W,
    A,
    S,
    D,
    E,
    C,
    F,
    Q,
    Z,
    X,
    P,
    ESCAPE,
    UNKNOWN,
    EOF,
    ARROW_UP,
    ARROW_DOWN,
    ARROW_LEFT,
    ARROW_RIGHT
}
