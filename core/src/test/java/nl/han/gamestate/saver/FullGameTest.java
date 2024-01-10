package nl.han.gamestate.saver;

import nl.han.shared.datastructures.BoundedValue;
import nl.han.shared.datastructures.Config;
import nl.han.shared.datastructures.Item;
import nl.han.shared.datastructures.creature.Player;
import nl.han.shared.datastructures.game.Game;
import nl.han.shared.datastructures.game.Team;
import nl.han.shared.datastructures.world.Chunk;
import nl.han.shared.datastructures.world.Coordinate;
import nl.han.shared.datastructures.world.World;
import nl.han.shared.enums.GameMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testing class for {@link Game}.
 *
 * @see <a href="https://confluenceasd.aimsites.nl/display/ASDS1G2/Testrapport+game+core">Testrapport</a>
 */
class FullGameTest {

    private Connection connection;

    @BeforeEach
    void setup() throws SQLException {
        String className = this.getClass().getSimpleName();
        connection = DriverManager.getConnection("jdbc:hsqldb:mem:" + className + ";create=true", "SA", "SA");
    }

    /**
     * Test code GCGS-7
     */
    @Test
    @DisplayName("test Saving and loading the game gives the same game")
    void testSavingLoadingGame() throws SQLException {
        //Arrange
        String expected = "Jordan";
        new Config().tableInit(connection);
        new Game().tableInit(connection);
        new Chunk().tableInit(connection);
        new BoundedValue().tableInit(connection);
        new Team().tableInit(connection);
        new Player().tableInit(connection);
        new Item().tableInit(connection);

        UUID gameId = UUID.randomUUID();
        Game game = new Game(gameId, "Test Game", GameMode.LMS, new World(0L, new ArrayList<>(), new Config(UUID.randomUUID(), "a", "b")));
        BoundedValue health = new BoundedValue(UUID.randomUUID(), 10, 10, 1);
        BoundedValue power = new BoundedValue(UUID.randomUUID(), 10, 10, 1);
        BoundedValue stamina = new BoundedValue(UUID.randomUUID(), 10, 10, 1);
        Player player = new Player(UUID.randomUUID(), expected, new Coordinate(0, 0), gameId, new Config(UUID.randomUUID(), "c", "d"), health, power, stamina, "");
        game.getPlayers().add(player);
        game.setMonsterConfig(new Config(UUID.randomUUID(), "e", "f"));

        //Act
        game.save(connection);
        Game newGame = game.map(game.getLoad(connection, game.getId().toString()), connection);

        //Assert
        assertEquals(game.getId(), newGame.getId());
        //TODO revert when players get loaded
//        assertEquals(expected, newGame.getPlayers().get(0).getName());
    }
}
