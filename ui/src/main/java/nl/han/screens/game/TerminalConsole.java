package nl.han.screens.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.TerminalScreen;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.java.Log;
import nl.han.interfaces.IKeyStrokeListener;
import nl.han.interfaces.ISubmitListener;
import nl.han.shared.enums.Key;

/**
 * The TerminalConsole class represents a console interface for interacting with
 * a terminal screen.
 * It provides methods for creating the console, handling user input, and
 * managing the console history.
 * 
 * @author Vasil Verdouw, Sem Gerrits
 */
@Getter
@Setter
@Log
@RequiredArgsConstructor
@ToString
public class TerminalConsole {

    private final TerminalScreen terminalScreen;
    private HistoryLabel historyLabel;
    private SubmittableTextBox console;
    private List<IKeyStrokeListener> keyStrokeListeners = new ArrayList<>();
    private static final int CHAT_WIDTH = 80;
    private static final int MAX_CHAT_HEIGHT = 23;
    private boolean isTyping = true;

    /**
     * Creates a console with a history label and a text box to type in.
     *
     * @return the panel containing the console
     * @author Vasil Verdouw, Sem Gerrits
     */
    public Panel createConsole() {
        final int GRID_COLUMNS = 1;
        final int TERMINAL_ROW = 1;

        Panel commandsPanel = new Panel(new GridLayout(GRID_COLUMNS));
        historyLabel = new HistoryLabel("", MAX_CHAT_HEIGHT, CHAT_WIDTH, false, "$ ");
        console = new SubmittableTextBox(new TerminalSize(CHAT_WIDTH, TERMINAL_ROW));

        console.addSubmitListener(text -> {
            console.setEnabled(false);
            isTyping = false;
            CompletableFuture.runAsync(this::pollInput);
        });

        commandsPanel.addComponent(console);
        commandsPanel.addComponent(historyLabel);
        return commandsPanel;
    }

    /**
     * Updates the history label with the current history.
     *
     * @param command the command to add to the history label
     * @author Jordan Geurtsen
     */
    public void updateCommandLog(String command) {
        historyLabel.addToHistory(command);
    }

    /**
     * Overwrites the command log with the given commands.
     *
     * @param commands the commands to overwrite the command log with
     * @author Jordan Geurtsen
     */
    public void overwriteCommandLog(List<String> commands) {
        historyLabel.setHistory(commands);
    }

    /**
     * Polls for user input from the terminal screen and handles the input by
     * calling the handleKeyStroke method.
     * If an IOException occurs, it is printed to the console.
     * 
     * @author Vasil Verdouw
     */
    public void pollInput() {
        try {
            while (!isTyping) {
                KeyStroke input = terminalScreen.pollInput();
                if (input != null && input.getKeyType() != null) {
                    handleKeyStroke(input);
                }
            }
        } catch (IOException e) {
            log.severe(e.getMessage());
        }
    }

    /**
     * Handles the given keystroke.
     *
     * @param input the keystroke to handle
     * @author Vasil Verdouw, Sem Gerrits
     */
    protected void handleKeyStroke(KeyStroke input) {
        if (input.getKeyType() == KeyType.Enter) {
            isTyping = true;
            console.setEnabled(true);
            console.takeFocus();
        } else {
            for (IKeyStrokeListener listener : keyStrokeListeners) {
                listener.onKeyStroke(mapKeyStrokeToKey(input));
            }
        }
    }

    /**
     * Adds a listener to the console that is called when the user submits a line.
     *
     * @param submitListener the listener to add to the console
     * @author Vasil Verdouw
     */
    public void addSubmitListener(ISubmitListener submitListener) {
        console.addSubmitListener(submitListener);
    }

    /**
     * Adds a listener to the console that is called when the user presses a key and
     * isn't typing.
     *
     * @param keyStrokeListener the listener to add to the console
     * @author Vasil Verdouw
     */
    public void addKeyStrokeListener(IKeyStrokeListener keyStrokeListener) {
        keyStrokeListeners.add(keyStrokeListener);
    }

    /**
     * Maps a KeyStroke to a Key. This has to be done because a KeyStroke is a
     * Lanterna specific class.
     * 
     * @param keyStroke the keystroke to map
     * @return the mapped keystroke
     * @author Vasil Verdouw
     */
    public Key mapKeyStrokeToKey(KeyStroke keyStroke) {
        if (keyStroke.getKeyType() == KeyType.Character) {
            return mapCharacterToKey(keyStroke.getCharacter());
        }

        return switch (keyStroke.getKeyType()) {
            case ArrowUp -> Key.ARROW_UP;
            case ArrowDown -> Key.ARROW_DOWN;
            case ArrowLeft -> Key.ARROW_LEFT;
            case ArrowRight -> Key.ARROW_RIGHT;
            case Escape -> Key.ESCAPE;
            case EOF -> Key.EOF;
            default -> Key.UNKNOWN;
        };
    }

    /**
     * Maps a character to a key.
     * 
     * @param character the character to map
     * @return the mapped key
     * @author Vasil Verdouw
     */
    public Key mapCharacterToKey(Character character) {
        return switch (character) {
            case 'w' -> Key.W;
            case 'a' -> Key.A;
            case 's' -> Key.S;
            case 'd' -> Key.D;
            case 'e' -> Key.E;
            case 'c' -> Key.C;
            case 'f' -> Key.F;
            case 'q' -> Key.Q;
            case 'z' -> Key.Z;
            case 'x' -> Key.X;
            case 'p' -> Key.P;
            default -> Key.UNKNOWN;
        };
    }

    /**
     * Sets the typing status of the TerminalConsole.
     *
     * @param typing true if the TerminalConsole is currently typing, false if
     *               otherwise
     */
    public void setTyping(boolean typing) {
        isTyping = typing;
    }
}
