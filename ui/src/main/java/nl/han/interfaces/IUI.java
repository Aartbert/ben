package nl.han.interfaces;

import nl.han.shared.datastructures.PlayerAttributes;
import nl.han.shared.datastructures.VisualTile;
import nl.han.shared.enums.DisplayWindow;

import java.util.List;

/**
 * This interface is used by the core package for all UI controlling.
 *
 * @author Vasil Verdouw
 */
public interface IUI {
    /**
     * Starts the interface and launches the game window.
     *
     * @author Vasil Verdouw
     */
    void startInterface();

    /**
     * Close the interface and stops the game window.
     *
     * @author Vasil Verdouw
     */
    void stopInterface();

    /**
     * Loads a certain screen for the user.
     *
     * @param displayWindow the window to display to the user
     * @author Vasil Verdouw
     * @see DisplayWindow
     */
    void loadScreen(DisplayWindow displayWindow);

    /**
     * @param lobbies the lobbies to add to the lobby list
     * @author Jochem Kalsbeek
     */
    void addLobbies(List<String> lobbies);

    /**
     * Updates the interface with the new player attributes and tiles.
     *
     * @param playerAttributes the new player attributes to display
     * @param visualTiles      the new world tiles to display
     * @author Vasil Verdouw
     */
    void updateInterface(PlayerAttributes playerAttributes, VisualTile[][] visualTiles, PlayerAttributes enemyAttributes);

    /**
     * Sets the player list to the given list of players.
     * probably something like: playerName: (hp, x, y)
     *
     * @param playerList list of strings containing player info
     */
    void setPlayerList(List<String> playerList);

    /**
     * Adds a message to the chat box and updates the chat box.
     *
     * @param message the message to add to the chat box
     * @author Jordan Geurtsen
     */
    void addChatMessage(String message);

    /**
     * Display the player's current items in the ui
     *
     * @param items the list to overwrite with
     */
    void updateInventory(List<String> items);

    /**
     * Adds a listener to the text box that listens for submit events.
     *
     * @param listener the listener to add
     * @author Vasil Verdouw
     */
    void addSubmitListener(ISubmitListener listener);

    /**
     * Adds a listener to the interface that listens for key strokes (when not
     * typing in the console). Will be called on every key stroke.
     *
     * @param keyStrokeListener the listener to add
     * @author Vasil Verdouw
     */
    void addKeyStrokeListener(IKeyStrokeListener keyStrokeListener);

    /**
     * Add a listener to the list
     *
     * @param listener the listener that will be added
     * @author Lars Meijerink
     */
    void addButtonClickListener(IButtonClickListener listener);

    /**
     * Receive a chat message from the server to be displayed in the chat box.
     *
     * @param message the message to be displayed in the chat box
     * @author Vasil Verdouw
     */
    void receiveChatMessage(String message);

    /**
     * Sets the profile of the user.
     *
     * @author Sem Gerrits, Lars Meijerink
     * @return the profile as a string
     */
    String getAgentConfig();

    /**
     *
     * @return the name of the profile
     */
    void setAgentConfig(String content);

    /**
     * Gets the config of the monster.
     *
     * @author Laurens van Brecht
     * @return the profile as a string
     */
    String getMonsterConfig();

    /**
     * Sets the config of the monster.
     *
     * @param content the monster config to be set
     * @author Laurens van Brecht
     */
    void setMonsterConfig(String content);

    /**
     * Displays the given text on the command history.
     *
     * @param text the text to display on the help screen
     * @author Jordan Geurtsen
     */
    void overwriteCommandHistory(List<String> text);

    /**
     * Retrieves the name of the new lobby from the ui.
     *
     * @return the name of the new lobby
     * @author Jordan Geurtsen
     */
    String getNewLobbyName();

    String getWorldConfig();

    String getGameMode();
}
