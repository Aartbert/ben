package nl.han;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.java.Log;
import nl.han.interfaces.IButtonClickListener;
import nl.han.interfaces.IKeyStrokeListener;
import nl.han.interfaces.ISubmitListener;
import nl.han.interfaces.IUI;
import nl.han.shared.datastructures.Config;
import nl.han.shared.datastructures.Item;
import nl.han.shared.datastructures.PlayerAttributes;
import nl.han.shared.datastructures.VisualTile;
import nl.han.shared.datastructures.creature.Bot;
import nl.han.shared.datastructures.creature.Creature;
import nl.han.shared.datastructures.creature.Player;
import nl.han.shared.datastructures.game.ChatMessage;
import nl.han.shared.datastructures.world.Chunk;
import nl.han.shared.datastructures.world.Coordinate;
import nl.han.shared.datastructures.world.Tile;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import nl.han.shared.enums.*;

import java.io.InputStream;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static nl.han.shared.datastructures.world.Chunk.CHUNK_HEIGHT;
import static nl.han.shared.datastructures.world.Chunk.CHUNK_WIDTH;

/**
 * This class represents the UI manager. It is responsible for the UI.
 *
 * @author Djurre Tieman, Vasil Verdouw
 */
@Log
@Singleton
public class UIManager implements ISubmitListener, IKeyStrokeListener, IButtonClickListener {

    @Inject
    protected GameManager gameManager;
    @Inject
    private IUI ui;

    /**
     * Starts the UI.
     *
     * @author Djurre Tieman, Vasil Verdouw
     */
    public void start() {
        ui.startInterface();
        ui.addKeyStrokeListener(this);
        ui.addSubmitListener(this);
        ui.addButtonClickListener(this);
    }

    /**
     * Updates the UI.
     *
     * @author Djurre Tieman
     */
    public void update() {
        updateTiles();
    }

    /**
     * Stops the UI.
     *
     * @author Vasil Verdouw
     * @see IUI#stopInterface()
     * @see UserInterface#stopInterface()
     */
    public void stop() {
        ui.stopInterface();
    }

    public void addLobbies(List<String> lobbies) {
        ui.addLobbies(lobbies);
    }

    /**
     * Updates the tiles on the screen. <br/>
     * TODO: Refactor out of bounds and Chunk check + gen to another class. <br/>
     * This was only meant as a demo :)
     *
     * @author Djurre Tieman
     */
    public void updateTiles() {
        Player player = gameManager.getCurrentPlayer();

        VisualTile[][] visualTiles = map(player.getChunk());
        PlayerAttributes playerAttributes = getPlayerAttributes(player);

        Creature enemy = gameManager.getAdjacentCreature();
        PlayerAttributes enemyAttributes = getPlayerAttributes(enemy);
        ui.updateInterface(playerAttributes, visualTiles, enemyAttributes);
    }

    public PlayerAttributes getPlayerAttributes(Creature creature) {
        if (creature!=null) {
            Coordinate playerCoordinate = creature.getChunk().getCoordinate();
            if(creature instanceof Player player) {
                return new PlayerAttributes(player.getHealth().getIntValue(),
                        player.getHealth().getUpperBound().intValue(),
                        playerCoordinate.x(),
                        playerCoordinate.y(),
                        playerCoordinate.z(),
                        player.getStamina().getIntValue(),
                        player.getPower().getIntValue(),
                        player.getTile().getName(),
                        player.getInventory().stream().map(Item::getName).toList());
            } else {
                return new PlayerAttributes(creature.getHealth().getIntValue(),
                        creature.getHealth().getUpperBound().intValue(),
                        playerCoordinate.x(),
                        playerCoordinate.y(),
                        playerCoordinate.z());
            }
        }
        return null;
    }

    /**
     * Moves the player to a new chunk if the player is out of bounds.
     * Also handles moving the player to the edge of the new chunk and
     * possibly generating the new chunk.
     *
     * @param player the player to move
     * @author Djurre Tieman, Vasil Verdouw
     */
    private void movePlayerToNewChunk(Player player) {
        Coordinate playerCoordinate = player.getCoordinate();
        Coordinate chunkCoordinate = player.getChunk().getCoordinate();

        if (playerCoordinate.x() < 0) {
            player.setChunk(gameManager.getCurrentChunk(new Coordinate(chunkCoordinate.x() - 1, chunkCoordinate.y())));
            player.setCoordinate(new Coordinate(CHUNK_WIDTH - 1, playerCoordinate.y()));
        } else if (playerCoordinate.x() > CHUNK_WIDTH - 1) {
            player.setChunk(gameManager.getCurrentChunk(new Coordinate(chunkCoordinate.x() + 1, chunkCoordinate.y())));
            player.setCoordinate(new Coordinate(0, playerCoordinate.y()));
        } else if (playerCoordinate.y() < 0) {
            player.setChunk(gameManager.getCurrentChunk(new Coordinate(chunkCoordinate.x(), chunkCoordinate.y() - 1)));
            player.setCoordinate(new Coordinate(playerCoordinate.x(), CHUNK_HEIGHT - 1));
        } else if (playerCoordinate.y() > CHUNK_HEIGHT - 1) {
            player.setChunk(gameManager.getCurrentChunk(new Coordinate(chunkCoordinate.x(), chunkCoordinate.y() + 1)));
            player.setCoordinate(new Coordinate(playerCoordinate.x(), 0));
        }
    }

    /**
     * Checks if the given coordinate is out of bounds for a chunk of the game.
     * <br/>
     * TODO: Refactor out of bounds and Chunk check + gen to another class. <br/>
     * This was only meant as a demo :)
     *
     * @param coordinate The coordinate to check.
     * @return True if the coordinate is out of bounds, false otherwise.
     */
    private boolean isOutOfBounds(Coordinate coordinate) {
        return coordinate.x() < 0 || coordinate.x() >= CHUNK_WIDTH || coordinate.y() < 0 || coordinate.y() >= CHUNK_HEIGHT;
    }

    /**
     * Maps the given {@link Chunk} to a 2D array of VisualTiles of the UI module.
     *
     * @param chunk The chunk where the visual tiles will be created for.
     * @return A 2D array of VisualTiles, with tiles based on the items, monsters, players and just tiles. .
     * @author Djurre Tieman, Fabian van Os
     */
    private VisualTile[][] map(Chunk chunk) {
        Tile[][] tiles = chunk.getTiles();
        VisualTile[][] visualTiles = new VisualTile[tiles.length][tiles[0].length];

        for (int y = 0; y < tiles.length; y++) {
            for (int x = 0; x < tiles[0].length; x++) {
                Tile tile = tiles[y][x];
                if (!tile.getItems().isEmpty()) {
                    visualTiles[y][x] = new VisualTile(tile.getItems().get(0).getCharacterColor(), tile.getItems().get(0).getBackgroundColor(), tile.getItems().get(0).getCharacter());
                } else {
                    visualTiles[y][x] = new VisualTile(tile.getCharacterColor(), tile.getBackgroundColor(), tile.getCharacter());
                }
            }
        }

        for (Creature creature : gameManager.getGame().getCreatures()) {
            if (!chunk.isSameChunk(creature.getChunk())) continue;
            Coordinate creatureCoordinate = creature.getCoordinate();
            if (creature instanceof Bot bot) {
                BotType botType = bot.getBotType();
                Color backgroundColor;
                if (bot.isActiveAudioPlayback()) backgroundColor = botType.getActiveAudioPlaybackBackgroundColor();
                else backgroundColor = botType.getBackgroundColor();
                visualTiles[creatureCoordinate.y()][creatureCoordinate.x()] = new VisualTile(botType.getCharacterColor(), backgroundColor, botType.getCharacter());
            } else if (creature instanceof Player player) {
                Color playerColor = Color.MAGENTA;
                if (player.equals(gameManager.getCurrentPlayer())) {
                    playerColor = Color.PINK;
                }
                visualTiles[creatureCoordinate.y()][creatureCoordinate.x()] = new VisualTile(Color.BLACK, playerColor, '@');
            }
        }

        return visualTiles;
    }

    /**
     * {@inheritDoc}
     *
     * @param text the text that was typed in the text box
     * @author Djurre Tieman, Jordan Geurtsen
     */
    @Override
    public void onSubmit(String text) {
        if (text.isEmpty()) return;

        if (text.startsWith(":")) {
            text = gameManager.getCurrentPlayer().getName() + ": " + text.substring(1);
            gameManager.sendChatMessage(text);
            ui.addChatMessage(text);
            return;
        }

        gameManager.compile(text);
    }

    /**
     * {@inheritDoc}
     *
     * @param key the key that was pressed on the keyboard
     * @author Djurre Tieman, Vasil Verdouw
     */
    @Override
    public void onKeyStroke(Key key) {
        gameManager.planAction(mapKeyToAction(key));
    }

    private Action mapKeyToAction(Key key) {
        if (key == Key.Z) {
            try {
                InputStream inputStream = AudioManager.class.getClassLoader().getResourceAsStream("21.wav");
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream);
                gameManager.sendAudioInputStream(audioInputStream);
            } catch (Exception e) {
                //TODO
            }
        }
        if (key == Key.X) {
            try {
                InputStream inputStream = AudioManager.class.getClassLoader().getResourceAsStream("boom.wav");
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream);
                gameManager.sendAudioInputStream(audioInputStream);
            } catch (Exception e) {
                //TODO
            }
        }
        return switch (key) {
            case W, ARROW_UP -> Action.MOVE_UP;
            case A, ARROW_LEFT -> Action.MOVE_LEFT;
            case S, ARROW_DOWN -> Action.MOVE_DOWN;
            case D, ARROW_RIGHT -> Action.MOVE_RIGHT;
            case C -> Action.ATTACK_ENEMY;
            case F -> Action.USE_HEALTH_POTION;
            case E -> Action.INTERACT;
            case ESCAPE, EOF -> Action.QUIT_GAME;
            default -> Action.UNKNOWN;
        };
    }

    /**
     * This method is called when a UI button is clicked.
     * Currently, it handles all buttons on all screens.
     * Because of how little buttons are used this is fine for now.
     * <br>
     * 'Creates' the game as single player; without network
     * 'Join Game' and 'Create Game' start the game as multiplayer player; with the network
     *
     * @author Vasil Verdouw
     */
    @Override
    public void onButtonClick(String buttonName, String argument) {
        switch (buttonName) {
            // Start screen
            case "Play" -> ui.loadScreen(DisplayWindow.LOAD_GAME);
            case "World config" -> log.info("World config");
            case "Agent config" -> {
                ui.setAgentConfig(gameManager.getAgentConfig());
                ui.loadScreen(DisplayWindow.AGENT_CONFIG);
            }
            case "Monster config" -> {
                ui.setMonsterConfig(gameManager.getMonsterConfig());
                ui.loadScreen(DisplayWindow.MONSTER_CONFIG);
            }
            case "Save Monster Config" -> {
                gameManager.saveMonsterConfig(ui.getMonsterConfig());
                ui.loadScreen(DisplayWindow.START);

            }
            case "Exit" -> ui.stopInterface();
            case "Create" -> onCreateGame();
            case "Join Game" -> ui.loadScreen(DisplayWindow.LOBBY);
            case "Join Lobby" -> onJoinGame(argument);
            case "Create Game" -> ui.loadScreen(DisplayWindow.CREATE_LOBBY);
            case "Load Game" -> log.info("Load game");
            case "Return to Start" -> ui.loadScreen(DisplayWindow.START);
            // Agent config screen
            case "Save Agent Config" -> {
                gameManager.saveAgentConfig(ui.getAgentConfig());
                ui.loadScreen(DisplayWindow.START);
            }
            case "Agent Info" -> {
                gameManager.saveAgentConfig(ui.getAgentConfig());
                ui.loadScreen(DisplayWindow.AGENT_INFO_PAGE_ONE);
            }
            case "Page 1" -> ui.loadScreen(DisplayWindow.AGENT_INFO_PAGE_ONE);
            case "Page 2" -> ui.loadScreen(DisplayWindow.AGENT_INFO_PAGE_TWO);
            case "Page 3" -> ui.loadScreen(DisplayWindow.AGENT_INFO_PAGE_THREE);
            case "Return to Agent Config" -> {
                ui.setAgentConfig(gameManager.getAgentConfig());
                ui.loadScreen(DisplayWindow.AGENT_CONFIG);
            }
            default -> log.info("Button not found: " + buttonName);
        }
    }

    /**
     * This method is called when a chat message is received.
     * It should send the message to the UI to be displayed.
     *
     * @param chatMessage The chat message to be displayed.
     * @author Vasil Verdouw, Jordan Geurtsen
     * @see IUI#addChatMessage(String)
     * @see GameManager#receiveChatMessage(ChatMessage)
     * @see ChatMessage
     */
    public void receiveChatMessage(ChatMessage chatMessage) {
        ui.addChatMessage(chatMessage.message());
    }

    /**
     * Updates the playerlist on the UI.
     * Also takes care of mapping the playerlist to a list of strings.
     * Ignores the current player.
     *
     * @param playerList The list of players to be displayed on the UI.
     * @author Vasil Verdouw
     */
    public void updatePlayerList(List<Player> playerList, Player currentPlayer) {
        List<String> playerNames = playerList.stream().filter(player -> !player.equals(currentPlayer)).map(player -> player.getName() + " (" + player.getHealth().getIntValue() + "hp, x:" + player.getChunk().getCoordinate().x() + ", y:" + player.getChunk().getCoordinate().y() + ", z:" + player.getChunk().getCoordinate().z() + " )").toList();
        ui.setPlayerList(playerNames);
    }

    /**
     * Shows the help screen.
     *
     * @author Jordan Geurtsen
     * @see IUI#overwriteCommandHistory(List)
     */
    public void showHelp() {
        List<String> helpList = new ArrayList<>(List.of("Controls:", "W or Arrow Up: Move up", "A or Arrow Left: Move left", "S or Arrow Down: Move down", "D or Arrow Right: Move right", "E: Interact", "F: Attack"));
        Collections.reverse(helpList);
        ui.overwriteCommandHistory(helpList);
    }

    /**
     * @param lobbyName Loads the screen when joining a game.
     * @author Jochem Kalsbeek
     */
    private void onJoinGame(String lobbyName) {
        gameManager.joinGame(lobbyName);
        ui.loadScreen(DisplayWindow.GAME);
    }

    /**
     * Loads the screen when creating a game.
     *
     * @author Jochem Kalsbeek
     */
    private void onCreateGame() {
        String lobbyName = ui.getNewLobbyName();
        String worldConfig = ui.getWorldConfig();
        GameMode gameMode = GameMode.valueOf(ui.getGameMode());

        gameManager.createGame(lobbyName, gameMode, new Config(UUID.randomUUID(), worldConfig, "worldConfig"));
        ui.loadScreen(DisplayWindow.GAME);
    }

    /**
     * Updates the inventory on the UI.
     * Also takes care of mapping the inventory to a list of strings.
     *
     * @param inventory The inventory to be displayed on the UI.
     * @author Jasper Kooy
     */
    public void updateInventory(List<Item> inventory) {
        List<String> inventoryItems = inventory.stream().map(Item::getName).toList();
        ui.updateInventory(inventory.stream().map(Item::getName).toList());
    }
}
