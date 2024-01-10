package nl.han;

import com.google.inject.Guice;
import com.google.inject.Injector;
import nl.han.client.INetwork;
import nl.han.client.Network;
import nl.han.compiler.CompilerController;
import nl.han.compiler.ICompiler;
import nl.han.shared.enums.Key;
import nl.han.interfaces.IProfile;
import nl.han.interfaces.IUI;
import nl.han.modules.Binding;
import nl.han.modules.MockedBinding;
import nl.han.modules.ModuleFactory;
import nl.han.pathfinding.AStar;
import nl.han.pathfinding.IPathFindingAlgorithm;
import nl.han.shared.enums.Action;
import nl.han.world.generation.IWorldGeneration;
import nl.han.world.generation.WorldGeneration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.verify;

/**
 * This class is responsible for testing the ui manager <br/>
 *
 * @author Jochem Kalsbeek
 */
class UIManagerTest {
    private UIManager sut;

    /**
     * This function is responsible for setting up the test class<br/>
     *
     * @author Jochem Kalsbeek
     */
    @BeforeEach
    void setup() {
        ModuleFactory factory = new ModuleFactory();

        factory.add(new MockedBinding<>(IWorldGeneration.class, WorldGeneration.class))
                .add(new MockedBinding<>(IUI.class, UserInterface.class))
                .add(new MockedBinding<>(ICompiler.class, CompilerController.class))
                .add(new MockedBinding<>(IProfile.class, ProfileService.class))
                .add(new MockedBinding<>(ISQLUtils.class, HSQLDBUtils.class))
                .add(new Binding<>(INetwork.class, Network.class))
                .add(new MockedBinding<>(IPathFindingAlgorithm.class, AStar.class));

        // Add bindings for the managers
        factory.add(new MockedBinding<>(WorldManager.class, WorldManager.class))
                .add(new MockedBinding<>(CompilerManager.class, CompilerManager.class))
                .add(new MockedBinding<>(ActionManager.class, ActionManager.class))
                .add(new MockedBinding<>(GameManager.class, GameManager.class))
                .add(new MockedBinding<>(NetworkManager.class, NetworkManager.class));

        Injector injector = Guice.createInjector(factory.createModules());
        sut = injector.getInstance(UIManager.class);
    }

    /**
     * This method is responsible for testing if the move up key is recognized <br/>
     *
     * @author Jochem Kalsbeek
     */
    @Test
    @DisplayName("test if move up key is recognized")
    void testIfMoveUpKeyIsRecognized() {
        // Arrange
        Action expected = Action.MOVE_UP;

        // Act
        sut.onKeyStroke(Key.W);

        // Act & Assert
        verify(sut.gameManager).planAction(expected);
    }

    /**
     * This method is responsible for testing if the move down key is recognized <br/>
     *
     * @author Jochem Kalsbeek
     */
    @Test
    @DisplayName("test if move down key is recognized")
    void testIfMoveDownKeyIsRecognized() {
        // Arrange
        Action expected = Action.MOVE_DOWN;

        // Act
        sut.onKeyStroke(Key.S);

        // Assert
        verify(sut.gameManager).planAction(expected);
    }

    /**
     * This method is responsible for testing if the move left key is recognized <br/>
     *
     * @author Jochem Kalsbeek
     */
    @Test
    @DisplayName("test if move left key is recognized")
    void testIfMoveLeftKeyIsRecognized() {
        // Arrange
        Action expected = Action.MOVE_LEFT;

        // Act
        sut.onKeyStroke(Key.A);

        // Assert
        verify(sut.gameManager).planAction(expected);
    }

    /**
     * This method is responsible for testing if the move right key is recognized <br/>
     *
     * @author Jochem Kalsbeek
     */
    @Test
    @DisplayName("test if move right key is recognized")
    void testIfMoveRightKeyIsRecognized() {
        // Arrange
        Action expected = Action.MOVE_RIGHT;

        // Act
        sut.onKeyStroke(Key.D);

        // Assert
        verify(sut.gameManager).planAction(expected);
    }

    /**
     * This method is responsible for testing if the stopping key is recognized <br/>
     *
     * @author Jochem Kalsbeek
     */
    @Test
    @DisplayName("test if stopping key gets recognized")
    void testIfStoppingKeyGetsRecognized() {
        // Arrange
        Action expected = Action.QUIT_GAME;

        // Act
        sut.onKeyStroke(Key.ESCAPE);

        // Assert
        verify(sut.gameManager).planAction(expected);
    }

    /**
     * This method is responsible for testing if the search item key is recognized <br/>
     * GCUIM-1
     * @author Justin Slijkhuis
     */
    @Test
    @DisplayName("test if the search key gets recognized")
    void testIfSearchItemKeyGetsRecognized() {
        // Arrange
        Action expected = Action.INTERACT;

        // Act
        sut.onKeyStroke(Key.E);

        // Assert
        verify(sut.gameManager).planAction(expected);
    }
}
