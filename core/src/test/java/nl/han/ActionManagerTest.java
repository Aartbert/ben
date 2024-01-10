package nl.han;

import nl.han.client.INetwork;
import nl.han.client.Network;
import nl.han.compiler.CompilerController;
import nl.han.compiler.ICompiler;
import nl.han.pathfinding.AStar;
import nl.han.pathfinding.IPathFindingAlgorithm;
import nl.han.shared.datastructures.BoundedValue;
import nl.han.shared.datastructures.Config;
import nl.han.shared.datastructures.Item;
import nl.han.shared.datastructures.creature.Creature;
import nl.han.shared.datastructures.world.Chunk;
import nl.han.shared.datastructures.world.Coordinate;
import nl.han.shared.datastructures.world.Tile;
import nl.han.shared.enums.Action;
import nl.han.shared.enums.ItemData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import nl.han.interfaces.IProfile;
import nl.han.interfaces.IUI;
import nl.han.modules.Binding;
import nl.han.modules.MockedBinding;
import nl.han.modules.ModuleFactory;
import nl.han.shared.datastructures.creature.Player;
import nl.han.shared.datastructures.game.Game;
import nl.han.shared.datastructures.world.World;
import nl.han.shared.enums.TileType;
import nl.han.world.generation.IWorldGeneration;
import nl.han.world.generation.WorldGeneration;

import java.util.UUID;

import static nl.han.shared.datastructures.world.Chunk.CHUNK_HEIGHT;
import static nl.han.shared.datastructures.world.Chunk.CHUNK_WIDTH;
import static nl.han.shared.enums.Action.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.mockito.Mockito.*;

/**
 * This class is responsible for testing the action manager<br/>
 *
 * @see <a href="{https://confluenceasd.aimsites.nl/display/ASDS1G2/Testrapport+game+core}">Testrapport</a>
 * @author Jochem Kalsbeek, Rieke Jansen, Justin Slijkhuis, Thomas Droppert
 */
class ActionManagerTest {

    private ActionManager sut;
    private final Creature creature = mock(Creature.class);
    private Player player;
    private Chunk chunk;

    /**
     * This function is responsible for setting up the test class<br/>
     *
     * @author Justin Slijkhuis
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
                .add(new MockedBinding<>(GameManager.class, GameManager.class))
                .add(new MockedBinding<>(NetworkManager.class, NetworkManager.class));

        // Add bindings for the domains
        factory.add(new MockedBinding<>(Game.class, Game.class))
                .add(new MockedBinding<>(World.class, World.class))
                .add(new MockedBinding<>(Chunk.class, Chunk.class));

        // Create the injector and get its instances
        Injector injector = Guice.createInjector(factory.createModules());
        WorldManager worldManager = injector.getInstance(WorldManager.class);
        GameManager gameManager = injector.getInstance(GameManager.class);
        Game game = injector.getInstance(Game.class);
        World world = injector.getInstance(World.class);
        chunk = injector.getInstance(Chunk.class);
        sut = injector.getInstance(ActionManager.class);
        BoundedValue bv = new BoundedValue(100, 100, 0);
        player = new Player(UUID.randomUUID(), "Jordan", new Coordinate(0, 0), UUID.randomUUID(), null, bv, bv, bv, "");
        Tile[][] tiles = new Tile[CHUNK_HEIGHT][CHUNK_WIDTH];
        for (int x = 0; x < CHUNK_WIDTH; x++) {
            for (int y = 0; y < CHUNK_HEIGHT; y++) {
                if (x <= 1 || y >= CHUNK_HEIGHT - 2) {
                    tiles[y][x] = new Tile(TileType.WATER, new Coordinate(x, y));
                } else {
                    tiles[y][x] = new Tile(TileType.GRASS, new Coordinate(x, y));
                }
            }
        }
        Item item = new Item(UUID.randomUUID(), ItemData.MEDIUM_HEALTH_POTION);
        Tile tile = new Tile(TileType.GRASS, new Coordinate(5, 5));
        tile.addItem(item);
        chunk.setTile(tile);
        Coordinate coordinate = new Coordinate(2, 0);
        player.setChunk(chunk);
        player.setCoordinate(coordinate);
        world.addChunk(chunk);

        when(chunk.getTiles()).thenReturn(tiles);
        when(chunk.getCoordinate()).thenReturn(coordinate);
        when(gameManager.getGame()).thenReturn(game);

        when(game.getWorld()).thenReturn(world);
        when(world.getConfig()).thenReturn(new Config(UUID.randomUUID(), """
                {
                    "seed": 328932193,
                    "worldSize": {
                        "width": 5,
                        "height": 6
                    },
                    "dungeonDepth": 7,
                    "itemSpawnRules": {
                        "min": 8,
                        "max": 9
                    },
                    "monsterSpawnRules": {
                        "min": 10,
                        "max": 11
                    }
                }
                """, "worldRules"));
        when(worldManager.generateChunk(any(Coordinate.class))).thenReturn(chunk);
        when(gameManager.getCurrentChunk(any(Coordinate.class))).thenReturn(chunk);

        when(creature.getChunk()).thenReturn(chunk);
        when(creature.getCoordinate()).thenReturn(new Coordinate(2, 2));
        when(creature.getHealth()).thenReturn(bv);

        when(gameManager.getCurrentPlayer()).thenReturn(player);
        when(chunk.getTile(any())).thenReturn(tile);
    }

    /**
     * This method is responsible for testing if the player can move<br/>
     *
     * Test code GCAC-1
     *
     * @author Jochem Kalsbeek
     */
    @Test
    @DisplayName("test if action is handled")
    void testIfActionIsHandled() {
        // Arrange
        when(creature.getStamina()).thenReturn(new BoundedValue(100, 100, 0));
        // Act
        sut.handleAction(Action.MOVE_RIGHT, creature);
        // Assert
        verify(creature).moveRight();
    }

    /**
     * This method is responsible for testing if the player cannot move when his stamina is too low.<br/>
     *
     * Test code GCAC-2
     *
     * @author Jochem Kalsbeek
     */
    @Test
    @DisplayName("test if action is not handled because of too low stamina")
    void testIfActionIsNotHandledBecauseOfTooLowStamina() {
        // Arrange
        when(creature.getStamina()).thenReturn(new BoundedValue(0, 100, 0));
        // Act
        sut.handleAction(Action.MOVE_RIGHT, creature);
        // Assert
        verify(creature, never()).moveRight();
    }

    /**
     * This method is responsible for testing if the player cannot move through water.<br/>
     *
     * Test code GCAC-3
     *
     * @author Rieke Jansen
     */
    @Test
    @DisplayName("test if the player cannot move through water")
    void validateWaterMovementTest() {
        // arrange
        Coordinate expected = new Coordinate(2, 0);
        // act
        sut.handleAction(MOVE_LEFT, player);
        // assert
        assertEquals(expected, player.getCoordinate());
    }

    /**
     * This method is responsible for testing if the player cannot move on water if its on a different chunk.<br/>
     *
     * Test code GCAC-4
     *
     * @author Rieke Jansen
     */
    @Test
    @DisplayName("test if the player cannot move through water if its in a different chunk")
    void validateWaterMovementDifferentChunkTest() {
        // arrange
        Coordinate expected = new Coordinate(2, 0);
        // act
        sut.handleAction(MOVE_UP, player);
        // assert
        assertEquals(expected, player.getCoordinate());
    }

    /**
     * This method is responsible for testing if the player cannot move when his health is 0.<br/>
     *
     * Test code GCAC-5
     *
     * @author Rieke Jansen
     */
    @Test
    @DisplayName("test if action is not handled because of 0 health")
    void testIfActionIsNotHandledBecauseOfHealth() {
        // Arrange
        when(creature.getHealth()).thenReturn((new BoundedValue(0, 100, 0)));
        // Act
        sut.handleAction(Action.MOVE_RIGHT, creature);

        // Assert
        verify(creature, never()).moveRight();
    }

    /**
     * This method is responsible for testing if the player can use an item.<br/>
     *
     * Test code GCAC-6
     *
     * @author Rieke Jansen, Thomas Droppert
     */
    @Test
    @DisplayName("test if the item is used")
    void testItemUsage() {
        // Arrange
        Player player = mock();
        player.getInventory().add(new Item(UUID.randomUUID(), ItemData.SMALL_HEALTH_POTION));
        when(player.getHealth()).thenReturn(new BoundedValue(10, 10, 0));

        // Act
        sut.handleAction(USE_HEALTH_POTION, player);

        // Assert
        verify(player, atLeastOnce()).getInventory();
    }

    /**
     * This method is responsible for testing if the player cannot use an item if its not in the inventory.<br/>
     *
     * Test code GCAC-7
     *
     * @author Rieke Jansen, Thomas Droppert
     */
    @Test
    @DisplayName("test if the item is not used")
    void testItemUsageWhenNotInInventory() {
        // Arrange
        Player creature = mock(Player.class);
        when(creature.getHealth()).thenReturn(new BoundedValue(10, 10, 0));

        // Act
        sut.handleAction(USE_HEALTH_POTION, creature);

        // Assert
        verify(creature, never()).removeInventoryItem(any());
    }

    /**
     * This function is responsible for testing if the action manager can handle a pickup action<br/>
     *
     * Test code GCAC-8
     *
     * @author Justin Slijkhuis
     */
    @Test
    @DisplayName("test if an item is removed from the chunk whenever it is picked up")
    void testItemIsRemovedFromChunk() {
        // Arrange

        // Act
        sut.handleAction(Action.PICK_UP, player);

        // Assert
        assertEquals(0, chunk.getTile(new Coordinate(5, 5)).getItems().size());
    }

    /**
     * This function is responsible for testing if the action manager can handle a pickup action<br/>
     *
     * Test code GCAC-9
     *
     * @author Justin Slijkhuis
     */
    @Test
    @DisplayName("test if the player receives the item after the pickup action is completed")
    void testPlayerReceivesItemAfterPickup() {
        // Arrange

        // Act
        sut.handleAction(Action.PICK_UP, player);

        // Assert
        assertEquals(1, player.getInventory().size());
    }
}
