package nl.han;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import nl.han.messages.AudioMessage;
import nl.han.pathfinding.IPathFindingAlgorithm;
import nl.han.pathfinding.exception.NoPathFoundException;
import nl.han.shared.datastructures.BoundedValue;
import nl.han.shared.datastructures.Config;
import nl.han.shared.datastructures.Item;
import nl.han.shared.datastructures.creature.Bot;
import nl.han.shared.datastructures.creature.Creature;
import nl.han.shared.datastructures.creature.Player;
import nl.han.shared.datastructures.game.ChatMessage;
import nl.han.shared.datastructures.game.Game;
import nl.han.shared.datastructures.game.Team;
import nl.han.shared.datastructures.world.Chunk;
import nl.han.shared.datastructures.world.Coordinate;
import nl.han.shared.datastructures.world.Tile;
import nl.han.shared.datastructures.world.World;
import nl.han.shared.enums.Action;
import nl.han.shared.enums.GameMode;
import nl.han.shared.enums.TileType;
import nl.han.tick.GameLoop;
import nl.han.world.population.spawner.IPlayerSpawner;
import nl.han.world.population.spawner.PlayerSpawner;
import org.ini4j.Ini;

import javax.sound.sampled.AudioInputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

import static java.lang.System.exit;
import static nl.han.CryptsOfChaos.seededRandom;
import static nl.han.shared.enums.Action.*;
import static nl.han.shared.datastructures.world.Chunk.CHUNK_HEIGHT;
import static nl.han.shared.datastructures.world.Chunk.CHUNK_WIDTH;

/**
 * The GameManager class handles the core game mechanics and loops, including
 * but not limited to:
 * key presses, game flow control and interaction management with
 * the UIManager, CompilerManager and ActionManager components.
 * <br/>
 * It's also responsible for handling player actions and their effects on the
 * game world.
 *
 * @author Jochem Kalsbeek
 * @see UIManager
 * @see CompilerManager
 * @see ActionManager
 */
@Getter
@Setter
@Singleton
@Log
public class GameManager extends GameLoop {

    private static final String STOP_COMMAND = "/stop";
    private static final String HELP_COMMAND = "/help";

    @Inject
    protected UIManager uiManager;
    @Inject
    protected AudioManager audioManager;

    @Inject
    protected CompilerManager compilerManager;

    @Inject
    protected ActionManager actionManager;

    protected WorldManager worldManager;

    @Inject
    protected NetworkManager networkManager;

    @Inject
    private IPathFindingAlgorithm pathFinder;

    private IPlayerSpawner playerSpawner;

    @Inject
    private GameStateManager gameStateManager;

    private boolean useNetwork;

    @Inject
    private ConfigManager configManager;

    @Setter
    private Game game;
    protected final Queue<Action> nextActions = new LinkedList<>();

    private Player currentPlayer;
    private long tickCount = 0;

    private static final String[] testLobbies = new String[]{"dungeon crawler 1", "dungeon crawler 2",
            "dungeon crawler 3"};

    /**
     * Starts the UI and sets the available lobbies.
     * This method should be called from the Main class.
     * This is somewhat of an entrypoint for the game.
     *
     * @author Vasil Verdouw
     * @see CryptsOfChaos#main
     */
    public void startUI() {
        gameStateManager.init();
        configManager.initialLoad();
        uiManager.start();
        setIsPrototype();
        setLobbies();
    }

    public List<Player> getPlayers() {
        return game.getPlayers();
    }

    /**
     * Starts the game. Will be called everytime a player joins or creates a lobby.
     * This is a seperate method so game related methods don't run before a game is
     * actually created.
     *
     * @author Vasil Verdouw
     * @see GameManager#createGame(String)
     * @see GameManager#joinGame(String)
     */
    public void startGame(String gameName, GameMode gameMode, Config worldConfig) {
        createGameInstance(worldConfig);
        start();
    }

    /**
     * Creates a new game instance with a world, players and teams.
     * This method should still be changed to include loading from DB and creating a
     * new game.
     */
    public void createGameInstance(Config worldConfig) {
        this.game = new Game(
                UUID.randomUUID(), "Test Game", GameMode.LMS,
                new World(worldConfig, new ArrayList<>()));
        this.playerSpawner = new PlayerSpawner();
        this.currentPlayer = new Player(
                UUID.randomUUID(), configManager.getUserName(),
                new Coordinate(0, 0), game.getId(), new Config(UUID.randomUUID(), UUID.randomUUID().toString().substring(0, 6), "AGENT"),
                new BoundedValue(UUID.randomUUID(), 10, 10, 0), new BoundedValue(UUID.randomUUID(), 10, 10, 0),
                new BoundedValue(UUID.randomUUID(), 10, 10, 0), "");
        Chunk chunk = new Chunk(new Tile[CHUNK_HEIGHT][CHUNK_WIDTH], new Coordinate(0, 0, 0), game.getWorld().getSeed(),
                game.getId());
        currentPlayer.setChunk(chunk);
        this.game.setMonsterConfig(new Config(UUID.randomUUID(), configManager.getMonsterConfig(), "MONSTER"));
        this.game.getPlayers().add(this.currentPlayer);
        this.game.setTeams(List.of(new Team(UUID.randomUUID(), chunk, game.getId())));
        this.game.getTeams().get(0).getPlayers().addAll(game.getPlayers());

        //TODO dit is raar
        audioManager.setGame(game);
        audioManager.setCurrentPlayer(currentPlayer);
    }

    public void updateOnNetwork(Player player) {
        networkManager.sendGameState(player);
    }


    /**
     * This method is responsible for updating or adding the player in the game.
     *
     * @param player The player to update or add.
     */
    public void onUpdatePlayer(Player player) {
        game.getPlayers().remove(player);
        game.addPlayer(player);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setup() {
        setWorldManager(new WorldManager(game));
        setIsPrototype();
        Chunk spawnChunk = getInitialChunk(new Coordinate(0, 0));
        game.getWorld().getChunks().add(spawnChunk);
        seededRandom.setSeed(game.getWorld().getSeed());
        this.game.getPlayers().forEach(player -> player.setChunk(spawnChunk));

        Coordinate playerSpawnCoordinate = spawnChunk.getRandomPassableTile().getCoordinate();
        currentPlayer.setCoordinate(playerSpawnCoordinate);

        networkManager.start(UUID.randomUUID().toString().substring(0, 5));
        this.game.getTeams().forEach(team -> team.setSpawnChunk(spawnChunk));

        gameStateManager.setGame(this.game);
        gameStateManager.startSavingTimer();
    }

    /**
     * Saves the configuration by calling the saveConfig method of the ConfigManager.
     *
     * @param config The configuration to be saved.
     * @author Sem Gerrits
     */
    public void saveAgentConfig(String config) {
        configManager.saveAgentConfig(config);
    }

    public String getAgentConfig() {
        return configManager.getAgentConfig();
    }

    /**
     * Saves the configuration by calling the saveMonsterConfig method from ConfigManager.
     *
     * @param config The monster configuration to be saved
     */
    public void saveMonsterConfig(String config) {
        configManager.saveMonsterConfig(config);
    }

    /**
     * Gets the monster configuration by calling the getMonsterConfig method from configManager.
     *
     * @return The monster configuration formatted as a String
     */
    public String getMonsterConfig() {
        return configManager.getMonsterConfig();
    }

    private void setLobbies() {
        if (useNetwork) {
            List<String> lobbies = networkManager.getLobbies();
            uiManager.addLobbies(lobbies);
        } else {
            uiManager.addLobbies(Arrays.stream(testLobbies).toList());
        }
    }

    private void setIsPrototype() {
        try {
            File fileToParse = new File("config.ini");
            useNetwork = Boolean.parseBoolean(new Ini(fileToParse).get("Prototype", "useNetwork"));

       } catch (IOException e) {
            useNetwork = false;
            log.log(Level.INFO, "Could not read ini file, defaulting to useNetwork = false", e);
            }
    }

    /**
     * {@inheritDoc}
     * <p>
     * QAS-06
     */
    @Override
    protected void tick() {
        tickCount++;
        if(currentPlayer.getHealth().getValue().intValue()<=0){
            //TODO death screen
            exit(0);
        }
        game.getMonsters().removeIf(monster -> monster.getHealth().getValue().intValue() <= 0);
        game.getPlayers().removeIf(player -> player.getHealth().getValue().intValue() <= 0);
        uiManager.update();
//        audioManager.updateAudio(tickCount);
        currentPlayer.increaseStamina(1);

        if (!nextActions.isEmpty()) {
            doAction();
        }

        if (tickCount % 10 == 0) {
            uiManager.updatePlayerList(game.getPlayers(), currentPlayer);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void shutdown() {

        uiManager.stop();


        if(isRunning()) {
            stop();
        }

        if (game != null) {
            gameStateManager.stopSavingTimer();
        }
    }

    /**
     * Plans an action to be performed in the next tick.
     * All previous planned actions will be discarded.
     *
     * @param action The action to be performed.
     */
    public void planAction(Action action) {
        nextActions.clear();
        nextActions.add(action);
    }

    /**
     * Plans a list of actions to be performed in the next couple of ticks.
     * All previous planned actions will be discarded. The actions will be executed
     * one by one in the order they are provided.
     *
     * @param actions The actions to be performed.
     */
    public void planAction(List<Action> actions) {
        nextActions.clear();
        nextActions.addAll(actions);
    }

    /**
     * Makes the current player run away from other players within the game environment.
     * The player calculates the opposite direction from other player and attempts to find a path to escape.
     * If a direct path is not available, the player explores nearby coordinates to find an escape route.
     *
     * @author Adil Sadiki, Justin Slijkhuis
     */
    public void runAway() {
        Creature creature = null;
        for (Creature creatureInGame : game.getCreatures()) {
            if (!creatureInGame.equals(currentPlayer)) {
                creature = creatureInGame;
                break;
            }
        }

        if (creature == null) {
            return;
        }

        Coordinate currentPlayerCoordinate = currentPlayer.getCoordinate();
        Coordinate otherPlayerCoordinate = creature.getCoordinate();

        // Calculate the opposite direction
        Coordinate oppositeCoordinate = calculateOppositeCoordinate(currentPlayerCoordinate, otherPlayerCoordinate);

        // Ensure the new coordinates are within the valid chunk bounds
        Coordinate validOppositeCoordinate = getValidChunkCoordinate(oppositeCoordinate);

        Chunk chunk = currentPlayer.getChunk();

        try {
            // Try finding a path to the opposite direction
            Tile startTile = chunk.getTile(currentPlayer.getCoordinate());
            Tile endTile = chunk.getTile(validOppositeCoordinate);

            List<Action> actions = pathFinder.findPath(chunk, startTile, endTile);
            planAction(actions);
        } catch (NoPathFoundException e) {
            log.log(Level.INFO, e.getMessage(), e);
            // Handle the case where a path to the opposite direction is not found
            exploreAndPlanActions(chunk, currentPlayerCoordinate, validOppositeCoordinate, 10);
        }
    }

    /**
     * Calculates the opposite coordinate relative to the current player's coordinate based on another player's coordinate.
     *
     * @param currentPlayerCoordinate The coordinate of the current player.
     * @param otherPlayerCoordinate   The coordinate of another player.
     * @return The calculated opposite coordinate.
     * @author Adil Sadiki, Justin Slijkhuis
     */
    private Coordinate calculateOppositeCoordinate(Coordinate currentPlayerCoordinate, Coordinate otherPlayerCoordinate) {
        int deltaX = otherPlayerCoordinate.x() - currentPlayerCoordinate.x();
        int deltaY = otherPlayerCoordinate.y() - currentPlayerCoordinate.y();

        int oppositeX = currentPlayerCoordinate.x() - deltaX;
        int oppositeY = currentPlayerCoordinate.y() - deltaY;

        return new Coordinate(oppositeX, oppositeY);
    }

    /**
     * Ensures that the provided coordinate is within the valid chunk bounds.
     *
     * @param coordinate The coordinate to be validated.
     * @return The validated coordinate within the chunk bounds.
     * @author Adil Sadiki, Justin Slijkhuis
     */
    private Coordinate getValidChunkCoordinate(Coordinate coordinate) {
        int newX = Math.max(0, Math.min(coordinate.x(), CHUNK_WIDTH - 1));
        int newY = Math.max(0, Math.min(coordinate.y(), CHUNK_HEIGHT - 1));

        return new Coordinate(newX, newY);
    }

    /**
     * Explores nearby coordinates from a target coordinate within a specified radius.
     * Tries to find a path to each explored coordinate, planning actions if a reachable coordinate is found.
     *
     * @param chunk                   The chunk in which exploration is performed.
     * @param currentPlayerCoordinate The current player's coordinate.
     * @param targetCoordinate        The target coordinate around which exploration is centered.
     * @param exploreRadius           The radius within which exploration occurs.
     * @author Adil Sadiki, Justin Slijkhuis
     */
    private void exploreAndPlanActions(Chunk chunk, Coordinate currentPlayerCoordinate, Coordinate targetCoordinate,
                                       int exploreRadius) {
        for (int i = 1; i <= exploreRadius; i++) {
            for (int dx = -i; dx <= i; dx++) {
                for (int dy = -i; dy <= i; dy++) {
                    int exploreX = targetCoordinate.x() + dx;
                    int exploreY = targetCoordinate.y() + dy;

                    Coordinate exploreCoordinate = new Coordinate(exploreX, exploreY);

                    if (isValidChunkCoordinate(exploreCoordinate)) {
                        Tile startExploreTile = chunk.getTile(currentPlayerCoordinate);
                        Tile endExploreTile = chunk.getTile(exploreCoordinate);

                        try {
                            // Try finding a path to the explored coordinate
                            List<Action> exploreActions = pathFinder.findPath(chunk, startExploreTile, endExploreTile);
                            planAction(exploreActions);
                            return;
                        } catch (NoPathFoundException e) {
                            log.log(Level.INFO, e.getMessage(), e);
                            // Continue to the next iteration if a path to the explored coordinate is not found
                        }
                    }
                }
            }
        }
    }

    /**
     * Checks if a coordinate is within the valid chunk bounds.
     *
     * @param coordinate The coordinate to be checked.
     * @return True if the coordinate is within the valid chunk bounds, false otherwise.
     * @author Adil Sadiki, Justin Slijkhuis
     */
    private boolean isValidChunkCoordinate(Coordinate coordinate) {
        return coordinate.x() >= 0 && coordinate.x() < CHUNK_WIDTH && coordinate.y() >= 0 && coordinate.y() < CHUNK_HEIGHT;
    }

    public void joinGame(String lobbyName) {
        startGame(lobbyName, GameMode.LMS, new Config(UUID.randomUUID(), """
                {
                    "seed": 328932193,
                    "worldSize": {
                        "width": -1,
                        "height": -1
                    },
                    "dungeonDepth": -1,
                    "itemSpawnRules": {
                        "min": 8,
                        "max": 9
                    },
                    "monsterSpawnRules": {
                        "min": 10,
                        "max": 11
                    }
                }
                """, "ben"));

        if (useNetwork) {
            networkManager.joinLobby(lobbyName);
        }
    }

    public void createGame(String lobbyName, GameMode gameMode, Config worldConfig) {
        startGame(lobbyName, gameMode, worldConfig);
        if (useNetwork) {
            networkManager.createLobby(lobbyName);
        }
    }

    /**
     * This method compiles a string command to a list of Actions objects using
     * CompilerManager.
     * <br/>
     * Then invokes doActions method to perform these actions.
     *
     * @param command The command string to compile.
     * @author Jochem Kalsbeek
     * @see CompilerManager
     * @see Action
     */
    public void compile(String command) {
        if (command.equals(STOP_COMMAND)) {
            shutdown();
            return;
        }

        if (command.equals(HELP_COMMAND)) {
            uiManager.showHelp();
            return;
        }

        List<Action> actions = compilerManager.compileInput(command, currentPlayer, game);
        planAction(actions);
    }

    /**
     * This method performs the next action on the current player using
     * ActionManager.
     *
     * @author Jochem Kalsbeek
     * @see ActionManager
     * @see Action
     */
    public void doAction() {
        if (nextActions.isEmpty())
            return;
        try {
            actionManager.handleAction(nextActions.poll(), currentPlayer);
            updateCurrentPlayerChunk();
            if (useNetwork) {
                updateOnNetwork(currentPlayer);
            }
        } catch (IllegalStateException e) {
            log.log(Level.WARNING, e.getMessage(), e);
        }
    }

    /**
     * This method checks whether the current player has left the chunk they were
     * in,
     * and updates their chunk if necessary.
     */
    private void updateCurrentPlayerChunk() {
        int chunkX = currentPlayer.getChunk().getCoordinate().x();
        int chunkY = currentPlayer.getChunk().getCoordinate().y();
        int chunkZ = currentPlayer.getChunk().getCoordinate().z();
        if (currentPlayer.getCoordinate().x() >= CHUNK_WIDTH) {
            currentPlayer.setChunk(worldManager.loadChunk(new Coordinate(chunkX + 1, chunkY, chunkZ)));
            currentPlayer.setCoordinate(
                    new Coordinate(0, currentPlayer.getCoordinate().y(), currentPlayer.getCoordinate().z()));
        }
        if (currentPlayer.getCoordinate().x() < 0) {
            currentPlayer.setChunk(worldManager.loadChunk(new Coordinate(chunkX - 1, chunkY, chunkZ)));
            currentPlayer.setCoordinate(new Coordinate(CHUNK_WIDTH - 1, currentPlayer.getCoordinate().y(),
                    currentPlayer.getCoordinate().z()));
        }
        if (currentPlayer.getCoordinate().y() >= CHUNK_HEIGHT) {
            currentPlayer.setChunk(worldManager.loadChunk(new Coordinate(chunkX, chunkY + 1, chunkZ)));
            currentPlayer.setCoordinate(
                    new Coordinate(currentPlayer.getCoordinate().x(), 0, currentPlayer.getCoordinate().z()));
        }
        if (currentPlayer.getCoordinate().y() < 0) {
            currentPlayer.setChunk(worldManager.loadChunk(new Coordinate(chunkX, chunkY - 1, chunkZ)));
            currentPlayer.setCoordinate(new Coordinate(currentPlayer.getCoordinate().x(), CHUNK_HEIGHT - 1,
                    currentPlayer.getCoordinate().z()));
        }
        if (currentPlayer.getCoordinate().z() > currentPlayer.getChunk().getCoordinate().z()) {
            currentPlayer.setChunk(worldManager.loadChunk(new Coordinate(chunkX, chunkY, chunkZ + 1)));
        }
        if (currentPlayer.getCoordinate().z() < currentPlayer.getChunk().getCoordinate().z()) {
            currentPlayer.setChunk(worldManager.loadChunk(new Coordinate(chunkX, chunkY, chunkZ - 1)));
        }
    }

    /**
     * This method is responsible for generating the initial chunk in the game.
     *
     * @return The Chunk object representing the initial chunk in the game.
     * @author Jordan Geurtsen
     * @see Chunk
     * @see WorldManager
     */
    private Chunk getInitialChunk(Coordinate coordinate) {
        return worldManager.loadChunk(coordinate);
    }

    /**
     * This method is responsible for getting the current chunk in the game where
     * the player is located. <br/>
     * It uses the World Manager to generate a new chunk based on the coordinates of
     * the current player.
     *
     * @return The Chunk object representing the current chunk in the game.
     * @author Jordan Geurtsen
     * @see Chunk
     * @see WorldManager
     * @see Player
     */
    public Chunk getCurrentChunk(Coordinate coordinate) {
        return worldManager.loadChunk(coordinate);
    }

    /**
     * Searches for enemy players in the current chunk and plans an attack action.
     *
     * @author Adil Sadiki
     */
    public void searchPlayer() {
        List<Creature> allCreaturesInChuck = allCreaturesInChunk();
        List<Creature> allEnemyPlayers = allEnemyPlayersInChuck(allCreaturesInChuck);
        searchAndAttack(allEnemyPlayers, Action.ATTACK_PLAYER);
    }

    /**
     * Searches for monsters in the current chunk and plans an attack action.
     *
     * @author Adil Sadiki
     */
    public void searchMonster() {
        List<Creature> allCreaturesInChuck = allCreaturesInChunk();
        List<Creature> allMonsters = allMonsterInChuck(allCreaturesInChuck);
        searchAndAttack(allMonsters, Action.ATTACK_MONSTER);
    }

    /**
     * Searches for any creatures in the current chunk and plans an attack action.
     *
     * @author Adil Sadiki
     */
    public void searchEnemy() {
        List<Creature> allCreaturesInChuck = allCreaturesInChunk();
        searchAndAttack(allCreaturesInChuck, Action.ATTACK_ENEMY);
    }


    /**
     * Helper method to search and attack creatures of a specific type.
     *
     * @param creatures    The list of creatures to search for and attack.
     * @param attackAction The action to perform when a reachable creature is found.
     * @author Adil Sadiki
     */
    private void searchAndAttack(List<Creature> creatures, Action attackAction) {
        for (Creature creature : creatures) {
            List<Action> actions = searchCreatures(creature);

            if (actions != null) {
                actions.remove(actions.size() - 1);
                actions.add(attackAction);
                planAction(actions);
                return;
            }
        }
    }

    /**
     * Searches for creatures and returns the actions to reach the first creature in the list.
     *
     * @param creature The creature to search for.
     * @return The list of actions to reach the creature, or null if no path is found.
     * @author Adil Sadiki
     */
    public List<Action> searchCreatures(Creature creature) {
        try {
            Tile startTile = creature.getChunk().getTile(currentPlayer.getCoordinate());
            Tile endTile = creature.getChunk().getTile(creature.getCoordinate());

            return pathFinder.findPath(currentPlayer.getChunk(), startTile, endTile);

        } catch (NoPathFoundException e) {
            log.log(Level.INFO, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Retrieves a list of all creatures in the current chunk.
     *
     * @return A list of all creature creatures in the current chunk.
     * @author Adil Sadiki
     */
    public List<Creature> allCreaturesInChunk() {
        Chunk currentChuck = currentPlayer.getChunk();
        return game.getCreatures().stream().filter(creature -> creature.getChunk().equals(currentChuck))
                .filter(creature -> !currentPlayer.equals(creature))
                .map(Creature.class::cast).toList();
    }

    /**
     * Retrieves a list of all players in the current chunk.
     *
     * @param creatures The list of creatures to filter.
     * @return A list of player creatures in the current chunk.
     * @author Adil Sadiki
     */
    public List<Creature> allPlayersInChuck(List<Creature> creatures) {
        List<Creature> players = creatures.stream().filter(creature -> {
            if (creature instanceof Player) {
                return !currentPlayer.equals(creature);
            }
            return true;
        }).toList();

        return allEnemyPlayersInChuck(players);
    }

    /**
     * Retrieves a list of all enemy players in the current chunk.
     *
     * @param creatures The list of creatures to filter.
     * @return A list of enemy player creatures in the current chunk.
     * @author Adil Sadiki
     */
    public List<Creature> allEnemyPlayersInChuck(List<Creature> creatures) {
        Team currentPlayerTeam = game.getTeam(currentPlayer);
        return creatures.stream()
                .filter(creature -> creature instanceof Player)
                .filter(creature -> !game.getTeam((Player) creature)
                        .equals(currentPlayerTeam)).toList();
    }

    /**
     * Retrieves a list of all monsters in the current chunk.
     *
     * @param creatures The list of creatures to filter.
     * @return A list of monster creatures in the current chunk.
     */
    public List<Creature> allMonsterInChuck(List<Creature> creatures) {
        return creatures.stream().filter(creature -> creature instanceof Bot).toList();
    }

    /**
     * Sends a chat message to the server coming from the current player.
     * The message will be broadcasted to all players in the team.
     *
     * @param text the text to send
     * @author Vasil Verdouw
     * @see NetworkManager#sendChatMessage(ChatMessage, Player, Team)
     * @see UIManager#onSubmit(String)
     * @see ChatMessage
     */
    public void sendChatMessage(String text) {
        if (useNetwork) {
            try {
                Team team = game.getTeams().stream().filter(t -> t.getPlayers()
                        .contains(currentPlayer)).findFirst().orElse(null);

                if (team == null) {
                    throw new IllegalStateException("Player is not in a team");
                }

                networkManager.sendChatMessage(new ChatMessage(text), currentPlayer, team);
            } catch (IllegalStateException e) {
                log.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }


    /**
     * Sends a audio input stream to the server coming from the current player.
     * The message will be broadcasted to all players in the team.
     *
     * @param audioInputStream the audioInputStream to send
     * @author Lucas van Steveninck
     * @see NetworkManager#sendAudioInputStream(AudioInputStream, Player, Team)
     * @see UIManager#onSubmit(String)
     */
    public void sendAudioInputStream(AudioInputStream audioInputStream) {
        if (useNetwork) {
            try {
                Team team = game.getTeams().stream().filter(t -> t.getPlayers()
                        .contains(currentPlayer)).findFirst().orElse(null);

                if (team == null) {
                    throw new IllegalStateException("Player is not in a team");
                }

                networkManager.sendAudioInputStream(audioInputStream, currentPlayer, team);
            } catch (IllegalStateException e) {
                log.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }


    /**
     * This method will move the player to the closest item
     *
     * @author Justin Slijkhuis
     */
    public void moveToClosestItem() {
        Chunk chunk = currentPlayer.getChunk();
        Tile tileWithItem = chunk.getClosestTileWithItem(currentPlayer.getCoordinate());

        Tile currentTile = chunk.getTile(currentPlayer.getCoordinate());
        Tile targetTile = chunk.getTile(tileWithItem.getCoordinate());

        try {
            List<Action> actions = pathFinder.findPath(chunk, currentTile, targetTile);
            actions.add(Action.PICK_UP);
            planAction(actions);
        } catch (NoPathFoundException e) {
            log.log(Level.WARNING, e.getMessage(), e);
        }
    }

    /**
     * Receives a chat message from the server and displays it in the chat box.
     *
     * @param chatMessage the chat message to receive
     * @author Vasil Verdouw
     * @see UIManager#receiveChatMessage(ChatMessage)
     * @see ChatMessage
     */
    public void receiveChatMessage(ChatMessage chatMessage) {
        uiManager.receiveChatMessage(chatMessage);
    }

    /**
     * Receives an audio message from the server and plays it.
     *
     * @param audioMessage the audio message to receive
     * @author Lucas van Steveninck
     */
    public void receiveAudioMessage(AudioMessage audioMessage) {
        audioManager.playAudioAsCreature(getPlayers().stream()
                .filter(player -> player.getIpAddress().equals(audioMessage.getSenderIpAdress()))
                .findFirst().orElse(null),
                audioMessage.getMessage(),
                tickCount);
    }

    /**
     * This method will check which item the player will use.
     * It checks the players inventory for every item and assigns them to the right
     * action that needs to be taken.
     * The action will be taken with the planAction method.
     *
     * @param action   determines which kinds of item has to be handled.
     * @param creature makes sure that the correct inventory is opened.
     * @author Thomas Droppert
     */
    //NOSONAR - SonarQube doesn't recognize the switch statement
    public void handleInventoryItem(Action action, Creature creature) {

        Player player = (Player) creature;
        for (Item item : player.getInventory()) {
            if (action == USE_HEALTH_POTION) {
                switch (item.getItemData()) {
                    case SMALL_HEALTH_POTION:
                        handleItemAction(player, item, USE_SMALL_HEALTH_POTION);
                        return;
                    case MEDIUM_HEALTH_POTION:
                        handleItemAction(player, item, USE_MEDIUM_HEALTH_POTION);
                        return;
                    case BIG_HEALTH_POTION:
                        handleItemAction(player, item, USE_BIG_HEALTH_POTION);
                        return;
                    default:
                        return;
                }
            }
        }
    }

    /**
     * This function calls on a method that takes away the item from the players inventory and plans the action of the item.
     *
     * @param player     determines for which player the item will be taken away from.
     * @param itemUsed   gives the item that will be taken out of the players inventory.
     * @param actionItem determines the action that now has to be taken.
     */
    private void handleItemAction(Player player, Item itemUsed, Action actionItem) { //NOSONAR - SonarQube doesn't recognize the switch statement
        player.removeInventoryItem(itemUsed.getId());
        uiManager.updateInventory(currentPlayer.getInventory());
        planAction(actionItem);
    }

    /**
     * This method is responsible for the interaction action
     * It currently consists of PICK_UP and USE_STAIRCASE
     *
     * @author Justin Slijkhuis
     */
    public void interact() {
        Chunk chunk = currentPlayer.getChunk();
        Tile tile = chunk.getTile(currentPlayer.getCoordinate());
        TileType tileType = tile.getType();

        if (tileType == TileType.STAIRS_UP || tileType == TileType.STAIRS_DOWN) {
            planAction(Action.USE_STAIRCASE);
        } else if (tile.hasItems()) {
            planAction(Action.PICK_UP);
        }
    }

    /**
     *
     * @return the adjacent creature
     * @author Rieke Jansen
     */
    public Creature getAdjacentCreature(){
        return actionManager.getAdjacentCreature(allCreaturesInChunk());
    }

    public void startAudioRecording() {
        audioManager.startAudioRecording();
    }

    public void stopAudioRecording() {
        audioManager.stopAudioRecording();
    }

    public AudioInputStream getMostRecentRecording() {
        return audioManager.getMostRecentRecording();
    }
}
