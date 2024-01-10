package nl.han;

import com.google.inject.Guice;
import com.google.inject.Injector;
import nl.han.client.INetwork;
import nl.han.client.Network;
import nl.han.compiler.CompilerController;
import nl.han.compiler.ICompiler;
import nl.han.interfaces.IProfile;
import nl.han.interfaces.IUI;
import nl.han.modules.Binding;
import nl.han.modules.MockedBinding;
import nl.han.modules.ModuleFactory;
import nl.han.pathfinding.AStar;
import nl.han.pathfinding.IPathFindingAlgorithm;
import nl.han.pathfinding.exception.NoPathFoundException;
import nl.han.shared.datastructures.Config;
import nl.han.shared.datastructures.Item;
import nl.han.shared.datastructures.creature.Creature;
import nl.han.shared.datastructures.creature.Player;
import nl.han.shared.datastructures.game.Game;
import nl.han.shared.datastructures.world.Chunk;
import nl.han.shared.datastructures.world.Coordinate;
import nl.han.shared.datastructures.world.Tile;
import nl.han.shared.datastructures.world.World;
import nl.han.shared.enums.Action;
import nl.han.shared.enums.GameMode;
import nl.han.shared.enums.ItemData;
import nl.han.shared.enums.TileType;
import nl.han.world.generation.IWorldGeneration;
import nl.han.world.generation.WorldGeneration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static nl.han.shared.enums.Action.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * This class is responsible for testing the game manager <br/>
 *
 * @author Jochem Kalsbeek
 */
class GameManagerTest {
    private final Config config = new Config(UUID.randomUUID(), """
            {
                "seed": 328932193,
                "worldSize": {
                    "width": 5,
                    "height": 21
                },
                "dungeonDepth": 2,
                "itemSpawnRules": {
                    "min": 8,
                    "max": 9
                },
                "monsterSpawnRules": {
                    "min": 10,
                    "max": 11
                }
            }
            """, "a");
    private final World world = new World(123, new ArrayList<>(), config);
    private Game game = new Game(UUID.randomUUID(), "Adil", GameMode.LMS, world);
    private final List<Action> listOfActions = new ArrayList<>();
    private GameManager sut;

    /**
     * This function is responsible for setting up the test class<br/>
     *
     * @author Jochem Kalsbeek
     */
    @BeforeEach
    void setup() {
        ModuleFactory factory = new ModuleFactory();

        // Add bindings for the packages
        factory.add(new MockedBinding<>(IWorldGeneration.class, WorldGeneration.class))
                .add(new MockedBinding<>(IUI.class, UserInterface.class))
                .add(new MockedBinding<>(ICompiler.class, CompilerController.class))
                .add(new MockedBinding<>(IProfile.class, ProfileService.class))
                .add(new MockedBinding<>(ISQLUtils.class, HSQLDBUtils.class))
                .add(new Binding<>(INetwork.class, Network.class))
                .add(new MockedBinding<>(IPathFindingAlgorithm.class, AStar.class));

        // Add bindings for the managers
        factory.add(new MockedBinding<>(WorldManager.class, WorldManager.class))
                .add(new MockedBinding<>(UIManager.class, UIManager.class))
                .add(new MockedBinding<>(CompilerManager.class, CompilerManager.class))
                .add(new MockedBinding<>(ActionManager.class, ActionManager.class))
                .add(new MockedBinding<>(GameStateManager.class, GameStateManager.class))
                .add(new MockedBinding<>(ConfigManager.class, ConfigManager.class))
                .add(new MockedBinding<>(Game.class, Game.class));

        Injector injector = Guice.createInjector(factory.createModules());
        sut = injector.getInstance(GameManager.class);

        World world = new World(config, new ArrayList<>());
        Game game = injector.getInstance(Game.class);
        when(game.getWorld()).thenReturn(world);
        sut.setGame(game);
    }

    /**
     * This method is responsible for testing if the interface gets stopped<br/>
     *
     * @author Jochem Kalsbeek
     */
    @Test
    @DisplayName("test if the user interface gets stopped")
    void testIfUserInterfaceStopped() {
        // Arrange
        listOfActions.add(Action.MOVE_UP);
        when(sut.compilerManager.compileInput("beweeg omhoog.", sut.getCurrentPlayer(), game))
                .thenReturn(listOfActions);

        // Act
        sut.shutdown();

        // Assert
        verify(sut.getUiManager()).stop();
    }

    /**
     * This method is responsible to test if the game gets stopped when the stop
     * command is entered <br/>
     *
     * @author Jochem Kalsbeek
     */
    @Test
    @DisplayName("test if the game gets stopped when stop command is entered.")
    void testIfGameGetsStoppedWhenGivingStopCommand() {
        // Arrange
        listOfActions.add(Action.MOVE_UP);
        when(sut.compilerManager.compileInput("beweeg omhoog.", sut.getCurrentPlayer(), game))
                .thenReturn(listOfActions);

        // Act
        sut.compile("/stop");
        boolean result = sut.isRunning();

        // Assert
        assertFalse(result);
    }

    /**
     * This method is responsible for checking if the user interface gets started
     * <br/>
     *
     * @author Jochem Kalsbeek
     */
    @Test
    @DisplayName("test if the user interface gets started")
    void testIfUserInterfaceStarted() {
        // Arrange

        World world = new World(123, new ArrayList<>(), new Config());
        game = new Game(UUID.randomUUID(), "Adil", GameMode.LMS, world);
        sut.setGame(game);

        // Act
        sut.startUI();

        // Assert
        verify(sut.getUiManager()).start();
    }

    /**
     * This method is responsible for testing if a command gets compiled <br/>
     *
     * @author Jochem Kalsbeek
     */
    @Test
    @DisplayName("test if the game gets compiled")
    void testIfCommandGetsCompiled() {
        // Arrange
        listOfActions.add(Action.MOVE_UP);
        when(sut.compilerManager.compileInput(any(), any(), any()))
                .thenReturn(listOfActions);
        String command = "beweeg omhoog.";

        // Act
        sut.compile(command);

        // Assert
        verify(sut.compilerManager).compileInput(any(), any(), any());
    }

    /**
     * This method is responsible to test if a command gets handled <br/>
     *
     * @author Jochem Kalsbeek
     */
    @Test
    @DisplayName("test if a command gets handled")
    void testIfCommandGetsHandled() {
        // Arrange
        listOfActions.add(Action.MOVE_UP);
        when(sut.compilerManager.compileInput(anyString(), any(), any()))
                .thenReturn(listOfActions);
        String command = "beweeg omhoog.";

        // Act
        sut.compile(command);

        Action actual = sut.nextActions.poll();

        // Assert
        assertEquals(Action.MOVE_UP, actual);
    }

    /**
     * This method is responsible to test if a small health potion is used whenever
     * handleHealthPotion is called <br/>
     * GCGM-10
     *
     * @author Thomas Droppert
     */
    @Test
    @DisplayName("test if the small health potion action is planned")
    void testIfSmallHealthPotionIsUsed() {
        // Arrange
        sut.startGame("Test", GameMode.LMS, config);
        Item expectedItem = new Item(UUID.randomUUID(), ItemData.SMALL_HEALTH_POTION);
        sut.getCurrentPlayer().addItemToInventory(expectedItem);

        // Act
        sut.handleInventoryItem(USE_HEALTH_POTION, sut.getCurrentPlayer());

        // Assert
        assertEquals(USE_SMALL_HEALTH_POTION, sut.nextActions.poll());
        assertTrue(sut.getCurrentPlayer().getInventory().isEmpty());
    }

    /**
     * This method is responsible to test if a medium health potion is used whenever
     * handleHealthPotion is called <br/>
     * GCGM-14
     *
     * @author Thomas Droppert
     */
    @Test
    @DisplayName("test if the medium health potion action is planned")
    void testIfMediumHealthPotionIsUsed() {
        // Arrange
        sut.startGame("Test", GameMode.LMS, config);
        Item expectedItem = new Item(UUID.randomUUID(), ItemData.MEDIUM_HEALTH_POTION);
        sut.getCurrentPlayer().addItemToInventory(expectedItem);

        // Act
        sut.handleInventoryItem(USE_HEALTH_POTION, sut.getCurrentPlayer());

        // Assert
        assertEquals(USE_MEDIUM_HEALTH_POTION, sut.nextActions.poll());
    }

    /**
     * This method is responsible to test if a big health potion is used whenever
     * handleHealthPotion is called <br/>
     * GCGM-15
     *
     * @author Thomas Droppert
     */
    @Test
    @DisplayName("test if the big health potion action is planned")
    void testIfBigHealthPotionIsUsed() {
        // Arrange
        sut.startGame("Test", GameMode.LMS, config);
        Item expectedItem = new Item(UUID.randomUUID(), ItemData.BIG_HEALTH_POTION);
        sut.getCurrentPlayer().addItemToInventory(expectedItem);

        // Act
        sut.handleInventoryItem(USE_HEALTH_POTION, sut.getCurrentPlayer());

        // Assert
        assertEquals(USE_BIG_HEALTH_POTION, sut.nextActions.poll());
    }

    /**
     * This method is responsible to test if the search item actions is added when
     * searching for an item <br/>
     * <p>
     * Test code GCGM-9
     *
     * @author Justin Slijkhuis
     */
    @Test
    @DisplayName("test if actions are added to the list of actions when searching for an item")
    void testIfSearchItemAddsActions() {
        // Arrange
        sut.startGame("Test", GameMode.LMS, config);
        Item item = new Item(UUID.randomUUID(), ItemData.MEDIUM_HEALTH_POTION);
        Tile[][] tiles = new Tile[Chunk.CHUNK_HEIGHT][Chunk.CHUNK_WIDTH];
        for (int i = 0; i < Chunk.CHUNK_HEIGHT; i++) {
            for (int j = 0; j < Chunk.CHUNK_WIDTH; j++) {
                Tile tile = new Tile(TileType.FOREST, new Coordinate(i, j));
                tiles[i][j] = tile;
            }
        }
        Tile tileWithItem = new Tile(TileType.FOREST, new Coordinate(5, 5));
        tileWithItem.addItem(item);
        tiles[5][5] = tileWithItem;
        Chunk chunk = new Chunk(tiles, new Coordinate(0, 0, 0), 0L, UUID.randomUUID());
        sut.getCurrentPlayer().setChunk(chunk);
        sut.getCurrentPlayer().setCoordinate(new Coordinate(4, 5));

        sut.setPathFinder(new AStar());
        // Act
        sut.moveToClosestItem();

        // Assert
        Queue<Action> actions = sut.getNextActions();
        Action firstAction = actions.poll();
        Action lastAction = actions.peek();
        assertEquals(Action.MOVE_DOWN, firstAction);
        assertEquals(Action.PICK_UP, lastAction);
    }

    /**
     * This method is responsible to test if the interact action adds pick up action
     * when standing on an item <br/>
     * <p>
     * Test code GCGM-11
     *
     * @author Justin Slijkhuis
     */
    @Test
    @DisplayName("test if the interact action adds pick up action when standing on an item")
    void testIfPickUpActionIsAddedWhenStandingOnItem() {
        // Arrange
        sut.startGame("Test", GameMode.LMS, config);
        Item item = new Item(UUID.randomUUID(), ItemData.MEDIUM_HEALTH_POTION);
        Tile[][] tiles = new Tile[Chunk.CHUNK_HEIGHT][Chunk.CHUNK_WIDTH];
        for (int i = 0; i < Chunk.CHUNK_HEIGHT; i++) {
            for (int j = 0; j < Chunk.CHUNK_WIDTH; j++) {
                Tile tile = new Tile(TileType.FOREST, new Coordinate(i, j));
                tiles[i][j] = tile;
            }
        }
        Tile tileWithItem = new Tile(TileType.FOREST, new Coordinate(5, 5));
        tileWithItem.addItem(item);
        tiles[5][5] = tileWithItem;
        Chunk chunk = new Chunk(tiles, new Coordinate(0, 0, 0), 0L, UUID.randomUUID());
        sut.getCurrentPlayer().setChunk(chunk);
        sut.getCurrentPlayer().setCoordinate(new Coordinate(5, 5));

        // Act
        sut.interact();

        // Assert
        Queue<Action> actions = sut.getNextActions();
        Action actual = actions.poll();
        assertEquals(Action.PICK_UP, actual);
    }

    /**
     * This method is responsible to test if the interact action adds use staircase
     * action when standing on a staircase <br/>
     * <p>
     * Test code GCGM-12
     *
     * @author Justin Slijkhuis
     */
    @Test
    @DisplayName("test if the interact action adds use staircase action when standing on an item")
    void testIfUseStaircaseActionIsAddedWhenStandingOnStaircase() {
        // Arrange
        sut.startGame("Test", GameMode.LMS, config);
        Tile[][] tiles = new Tile[Chunk.CHUNK_HEIGHT][Chunk.CHUNK_WIDTH];
        for (int i = 0; i < Chunk.CHUNK_HEIGHT; i++) {
            for (int j = 0; j < Chunk.CHUNK_WIDTH; j++) {
                Tile tile = new Tile(TileType.FOREST, new Coordinate(i, j));
                tiles[i][j] = tile;
            }
        }
        Tile tileWithStaircase = new Tile(TileType.STAIRS_UP, new Coordinate(5, 5));
        tiles[5][5] = tileWithStaircase;
        Chunk chunk = new Chunk(tiles, new Coordinate(0, 0, 0), 0L, UUID.randomUUID());
        sut.getCurrentPlayer().setChunk(chunk);
        sut.getCurrentPlayer().setCoordinate(new Coordinate(5, 5));

        // Act
        sut.interact();

        // Assert
        Queue<Action> actions = sut.getNextActions();
        Action actual = actions.poll();
        assertEquals(Action.USE_STAIRCASE, actual);
    }

    /**
     * Test code GCGM-16
     *
     * @throws NoPathFoundException if no path is found
     * @author Adil Sadiki
     */
    @Test
    @DisplayName("test if player finds a path away from enemy")
    void testRunAway() throws NoPathFoundException {
        // Arrange
        game = mock();
        sut.setGame(game);
        LinkedList<Action> expected = new LinkedList<>();
        expected.add(Action.MOVE_DOWN);
        expected.add(Action.MOVE_UP);

        Player current = mock();
        Coordinate start = new Coordinate(5, 5);
        when(current.getCoordinate()).thenReturn(start);
        sut.setCurrentPlayer(current);

        Player other = mock();
        Coordinate end = new Coordinate(5, 5);
        when(other.getCoordinate()).thenReturn(end);

        List<Creature> players = List.of(current, other);
        when(game.getCreatures()).thenReturn(players);

        Chunk chunk = mock();
        Tile initial = mock();

        when(chunk.getTile(start)).thenReturn(initial);
        when(current.getChunk()).thenReturn(chunk);

        IPathFindingAlgorithm algorithm = mock();
        sut.setPathFinder(algorithm);
        when(algorithm.findPath(chunk, initial, initial)).thenReturn(expected);

        // Act
        sut.runAway();
        Queue<Action> actual = sut.getNextActions();

        // Assert
        assertEquals(expected, actual);
    }
}
