package nl.han.screens.game;

import com.google.inject.Inject;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import nl.han.interfaces.IKeyStrokeListener;
import nl.han.interfaces.ISubmitListener;
import nl.han.shared.datastructures.PlayerAttributes;
import nl.han.shared.datastructures.VisualTile;

import java.util.List;

/**
 * The GameScreen class is responsible for rendering the game, chat, and
 * console. It's basically the main screen of the game.
 *
 * @author Vasil Verdouw, Sem Gerrits
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class GameScreen extends BasicWindow {
    private static final int LEFT_WIDTH = 80;
    private static final int RIGHT_WIDTH = 40;
    private static final int TOP_LEFT_HEIGHT = 24;
    private static final int BOTTOM_LEFT_HEIGHT = 24;
    private static final int TOP_RIGHT_HEIGHT = 22;
    private static final int MIDDLE_RIGHT_HEIGHT = 12;
    private static final int BOTTOM_RIGHT_HEIGHT = 12;

    private static final String TOP_LEFT_TITLE = "Game screen";
    private static final String BOTTOM_LEFT_TITLE = "Commando's";
    private static final String TOP_RIGHT_TITLE = "Players";
    private static final String BOTTOM_RIGHT_TITLE = "Chat";
    private static final String MIDDLE_RIGHT_TITLE = "Inventory";

    @Inject
    private GameRenderer gameRenderer;

    @Inject
    private ChatRenderer chatRenderer;

    @Inject
    private InventoryRenderer inventoryRenderer;
    private TerminalConsole terminalConsole;
    private final HistoryLabel playerList = new HistoryLabel("Geen players gevonden...", TOP_RIGHT_HEIGHT - 6,
            RIGHT_WIDTH, false, "");
    private final Label playerAttributesLabel = new Label("").setPreferredSize(new TerminalSize(LEFT_WIDTH, 6));
    private final Label inventoryLabel = new Label("").setPreferredSize(new TerminalSize(RIGHT_WIDTH, 3));
    private final Label enemyLabel = new Label("").setPreferredSize(new TerminalSize(LEFT_WIDTH, 1));
    /**
     * Creates a new window with a grid layout.
     *
     * @author Vasil Verdouw, Sem Gerrits
     */
    public GameScreen(TerminalConsole terminalConsole) {
        super("Crypts of Chaos");
        this.terminalConsole = terminalConsole;
    }

    /**
     * Creates a grid layout with 4 components
     *
     * @author Vasil Verdouw, Sem Gerrits
     */
    public void createGameScreen() {
        Panel playerAttributesPanel = new Panel(new GridLayout(1));
        playerAttributesPanel.addComponent(playerAttributesLabel);
        playerAttributesPanel.addComponent(enemyLabel);
        playerAttributesPanel.addComponent(playerList);

        Component leftTop = gameRenderer.createGameScreen();
        Component leftBottom = terminalConsole.createConsole();
        Component rightTop = playerAttributesPanel;
        Component rightBottom = chatRenderer.createChatLog(BOTTOM_RIGHT_HEIGHT, RIGHT_WIDTH);
        Component rightMiddle = inventoryRenderer.createInventory(MIDDLE_RIGHT_HEIGHT, RIGHT_WIDTH);

        terminalConsole.addSubmitListener(terminalConsole::updateCommandLog);
        createGridLayout(leftTop, leftBottom, rightTop, rightBottom, rightMiddle);
    }

    /**
     * Creates a grid layout with 4 components and adds them to itself.
     *
     * @param leftTop     the component to display in the top left
     * @param leftBottom  the component to display in the bottom left
     * @param rightTop    the component to display in the top right
     * @param rightBottom the component to display in the bottom right
     * @param rightMiddle the component to display in the middle right
     * @author Vasil Verdouw, Sem Gerrits, Jasper Kooy
     */
    public void createGridLayout(Component leftTop, Component leftBottom,
            Component rightTop, Component rightBottom,
            Component rightMiddle) {
        Panel mainPanel = new Panel(new GridLayout(2));
        Panel leftPanel = new Panel(new GridLayout(1));
        Panel rightPanel = new Panel(new GridLayout(1));

        leftPanel.addComponent(leftTop.withBorder(Borders.singleLine(TOP_LEFT_TITLE)));
        leftPanel.addComponent(leftBottom.withBorder(Borders.singleLine(BOTTOM_LEFT_TITLE)));
        rightPanel.addComponent(rightTop.withBorder(Borders.singleLine(TOP_RIGHT_TITLE)));
        rightPanel.addComponent(rightMiddle.withBorder(Borders.singleLine(MIDDLE_RIGHT_TITLE)));
        rightPanel.addComponent(rightBottom.withBorder(Borders.singleLine(BOTTOM_RIGHT_TITLE)));

        leftTop.setPreferredSize(new TerminalSize(LEFT_WIDTH, TOP_LEFT_HEIGHT));
        leftBottom.setPreferredSize(new TerminalSize(LEFT_WIDTH, BOTTOM_LEFT_HEIGHT));
        rightTop.setPreferredSize(new TerminalSize(RIGHT_WIDTH, TOP_RIGHT_HEIGHT));
        rightMiddle.setPreferredSize(new TerminalSize(RIGHT_WIDTH, MIDDLE_RIGHT_HEIGHT));
        rightBottom.setPreferredSize(new TerminalSize(RIGHT_WIDTH, BOTTOM_RIGHT_HEIGHT));

        mainPanel.addComponent(leftPanel);
        mainPanel.addComponent(rightPanel);

        setComponent(mainPanel);
    }

    /**
     * Sets the player list to the given list of players.
     * probably something like: playerName: (hp, x, y)
     *
     * @param playerList list of strings containing player info
     */
    public void setPlayerList(List<String> playerList) {
        this.playerList.setHistory(playerList);
    }

    /**
     * Updates the game screen with the given visual tiles.
     *
     * @param visualTiles the visual tiles to update the game screen with
     * @author Vasil Verdouw, Sem Gerrits
     */
    public void updateGameScreen(PlayerAttributes playerAttributes, VisualTile[][] visualTiles, PlayerAttributes enemyAttributes) {
        String playerAttributesString = playerAttributes.toString();
        String enemyAttributesString = (enemyAttributes!=null ? enemyAttributes.toString() : "");

        playerAttributesLabel.setText(playerAttributesString);
        enemyLabel.setText("enemy: " + enemyAttributesString);
        gameRenderer.updateGameScreen(visualTiles);
    }

    /**
     * Adds a chat message to the chat log.
     *
     * @param message the message to add to the chat log
     */
    public void addChatMessage(String message) {
        chatRenderer.addChatMessage(message);
    }

    /**
     * Overwrites the command log with the given list
     *
     * @param message the list to overwrite with
     * @author Jordan Geurtsen
     */
    public void overwriteCommandLog(List<String> message) {
        terminalConsole.overwriteCommandLog(message);
    }

    /**
     * Adds a submit listener to the terminal console.
     *
     * @param listener the listener to add
     * @author Vasil Verdouw
     */
    public void addSubmitListener(ISubmitListener listener) {
        terminalConsole.addSubmitListener(listener);
    }

    /**
     * Adds a keystroke listener to the terminal console.
     *
     * @param keyStrokeListener the listener to add
     * @author Vasil Verdouw
     */
    public void addKeyStrokeListener(IKeyStrokeListener keyStrokeListener) {
        terminalConsole.addKeyStrokeListener(keyStrokeListener);
    }

    /**
     * Updates the ui to display the player's current items
     *
     * @param inventory the items to update the inventory with
     */
    public void updateInventory(List<String> inventory) {
        inventoryRenderer.updateInventory(inventory);
    }
}
