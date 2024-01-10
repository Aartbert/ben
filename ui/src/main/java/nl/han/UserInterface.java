package nl.han;

import java.io.IOException;

import com.google.inject.Inject;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.screen.TerminalScreen;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;
import nl.han.interfaces.IButtonClickListener;
import nl.han.interfaces.IKeyStrokeListener;
import nl.han.interfaces.IProfile;
import nl.han.interfaces.ISubmitListener;
import nl.han.interfaces.IUI;
import nl.han.screens.createlobby.CreateLobbyScreen;
import nl.han.screens.agentconfig.AgentConfigScreen;
import nl.han.screens.game.GameScreen;
import nl.han.screens.game.TerminalConsole;
import nl.han.screens.info.AgentInfoScreenPageOne;
import nl.han.screens.info.AgentInfoScreenPageThree;
import nl.han.screens.info.AgentInfoScreenPageTwo;
import nl.han.screens.loadgame.LoadGameScreen;
import nl.han.screens.lobbyoverview.LobbyOverviewScreen;
import nl.han.screens.monsterconfig.MonsterConfigScreen;
import nl.han.screens.start.StartScreen;
import nl.han.shared.datastructures.PlayerAttributes;
import nl.han.shared.datastructures.VisualTile;
import nl.han.shared.enums.DisplayWindow;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import static java.lang.System.exit;

/**
 * The UserInterface class implements the IUI interface and provides methods to
 * start and stop the user interface,
 * update the interface with player attributes and tiles, and add listeners for
 * text and key stroke changes.
 *
 * @author Vasil Verdouw
 */
@Getter
@Setter
@Log
@RequiredArgsConstructor
public class UserInterface implements IUI {

    private ScreenMaker screenMaker = new ScreenMaker();

    @Inject
    private ButtonMaker buttonMaker;
    private TerminalScreen terminalScreen;
    private MultiWindowTextGUI gui;

    @Inject
    private StartScreen startScreen;
    @Inject
    private LoadGameScreen loadGameScreen;
    @Inject
    private LobbyOverviewScreen lobbyScreen;
    @Inject
    private GameScreen gameScreen;
    @Inject
    private AgentConfigScreen agentConfigScreen;
    @Inject
    private MonsterConfigScreen monsterConfigScreen;
    @Inject
    private IProfile profileService;
    @Inject
    private CreateLobbyScreen createLobby;
    @Inject
    private AgentInfoScreenPageOne agentInfoScreenPageOne;
    @Inject
    private AgentInfoScreenPageTwo agentInfoScreenPageTwo;
    @Inject
    private AgentInfoScreenPageThree agentInfoScreenPageThree;

    /**
     * Starts the user interface by creating and setting up the necessary
     * components.
     * Also adds a submit listener to the console and creates a grid layout for the
     * components.
     *
     * @author Vasil Verdouw
     */
    @Override
    public void startInterface() {
        terminalScreen = screenMaker.createScreen();
        gameScreen.setTerminalConsole(new TerminalConsole(terminalScreen));

        startScreen.createStartScreen();
        agentConfigScreen.createAgentConfigScreen();
        monsterConfigScreen.createMonsterConfigScreen();
        loadGameScreen.createLoadGameScreen();
        lobbyScreen.createLobbyScreen();
        gameScreen.createGameScreen();
        createLobby.createCreateLobby();
        agentInfoScreenPageOne.createAgentInfoScreenPageOne();
        agentInfoScreenPageTwo.createAgentInfoScreenPageTwo();
        agentInfoScreenPageThree.createAgentInfoScreenPageThree();

        lobbyScreen.updateLobbies(buttonMaker);

        CompletableFuture.runAsync(() -> {
            gui = screenMaker.createMultiWindowTextGUI(terminalScreen);
            loadScreen(DisplayWindow.START);
        });
    }

    /**
     * {@inheritDoc}
     *
     * @author Vasil Verdouw
     */
    @Override
    public void stopInterface() {
        try {
            terminalScreen.stopScreen();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public void addLobbies(List<String> lobbies) {
        lobbyScreen.addLobbies(lobbies);
        lobbyScreen.updateLobbies(buttonMaker);
    }

    /**
     * {@inheritDoc}
     *
     * @author Vasil Verdouw, Sem Gerrits
     */
    @Override
    public void loadScreen(DisplayWindow displayWindow) {
        gui.removeWindow(gui.getActiveWindow());

        Window window = switch (displayWindow) {
            case START -> startScreen;
            case LOAD_GAME -> loadGameScreen;
            case LOBBY -> lobbyScreen;
            case GAME -> gameScreen;
            case AGENT_CONFIG -> agentConfigScreen;
            case CREATE_LOBBY -> createLobby;
            case AGENT_INFO_PAGE_ONE -> agentInfoScreenPageOne;
            case AGENT_INFO_PAGE_TWO -> agentInfoScreenPageTwo;
            case AGENT_INFO_PAGE_THREE -> agentInfoScreenPageThree;
            case MONSTER_CONFIG -> monsterConfigScreen;
        };
        gui.addWindowAndWait(window);
        gui.waitForWindowToClose(window);
        exit(0);
    }

    /**
     * Updates the user interface with the given player attributes and tiles.
     *
     * @param playerAttributes the attributes of the player
     * @param visualTiles      the tiles to be displayed on the interface
     * @author Vasil Verdouw
     */
    @Override
    public void updateInterface(PlayerAttributes playerAttributes, VisualTile[][] visualTiles, PlayerAttributes enemyAttributes) {
        if (gameScreen == null)
            return;

        gameScreen.updateGameScreen(playerAttributes, visualTiles, enemyAttributes);
    }

    /**
     * {@inheritDoc}
     *
     * @author Vasil Verdouw
     */
    @Override
    public void setPlayerList(List<String> playerList) {
        gameScreen.setPlayerList(playerList);
    }

    /**
     * {@inheritDoc}
     *
     * @author Jordan Geurtsen
     */
    @Override
    public void addChatMessage(String message) {
        gameScreen.addChatMessage(message);
    }

    /**
     * Update the interface to display the player's items
     *
     * @param items the items to update the inventory with
     * @author Jasper Kooy
     */
    @Override
    public void updateInventory(List<String> items) {
        gameScreen.updateInventory(items);
    }

    /**
     * Adds a TextChangeListener to the submit listener list.
     *
     * @param listener the TextChangeListener to be added
     * @author Vasil Verdouw
     */
    public void addSubmitListener(ISubmitListener listener) {
        gameScreen.addSubmitListener(listener);
    }

    /**
     * Adds a KeyStrokeListener to the UserInterface.
     *
     * @param keyStrokeListener the KeyStrokeListener to be added.
     * @author Vasil Verdouw
     */
    @Override
    public void addKeyStrokeListener(IKeyStrokeListener keyStrokeListener) {
        gameScreen.addKeyStrokeListener(keyStrokeListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addButtonClickListener(IButtonClickListener listener) {
        buttonMaker.addListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void receiveChatMessage(String message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'receiveChatMessage'");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAgentConfig() {
        return agentConfigScreen.getAgentConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAgentConfig(String content) {
        agentConfigScreen.setAgentConfig(content);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMonsterConfig() {
        return monsterConfigScreen.getMonsterConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMonsterConfig(String content) {
        monsterConfigScreen.setMonsterConfig(content);
    }


    /**
     * {@inheritDoc}
     *
     * @param text the text to display on the help screen
     */
    @Override
    public void overwriteCommandHistory(List<String> text) {
        gameScreen.overwriteCommandLog(text);
    }

    /**
     * {@inheritDoc}
     *
     * @return the name of the lobby that was created by the user
     * @author Jochem Kalsbeek
     */
    @Override
    public String getNewLobbyName() {
        return createLobby.getLobbyName();
    }

    @Override
    public String getWorldConfig() {
        return createLobby.getWorldConfig();
    }

    @Override
    public String getGameMode() {
        return createLobby.getGameMode();
    }
}
