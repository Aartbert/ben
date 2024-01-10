package nl.han.gamestate.saver;

import com.google.inject.Inject;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import nl.han.HSQLDBUtils;
import nl.han.ISQLUtils;
import nl.han.shared.datastructures.creature.Player;
import nl.han.shared.datastructures.game.Game;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for all database interactions.
 * <br/>
 * Using different functions, this class can get all games, get a specific game and insert a game.
 * <br/>
 * This class uses the {@link HSQLDBUtils} class to get a connection to the database.
 *
 * @author Jordan Geurtsen, Rieke Jansen
 * @see HSQLDBUtils
 * @see Game
 * @see Player
 */
@Singleton
@RequiredArgsConstructor
public class GameRepository {
    @Inject
    private ISQLUtils sqlUtils;

    /**
     * Gets a list of all games to display from the database
     *
     * @return A list of readable games
     */
    public List<List<String>> getSavedGames() {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            String query = "SELECT * FROM game g";
            return gameListMapper(statement.executeQuery(query));
        } catch (SQLException e) {
            sqlUtils.logSQLException(e);
        }
        return List.of();
    }

    /**
     * Makes a list of game_id and game_name.
     *
     * @return A list of game_id and game_name
     * @author Rieke Jansen
     */
    private List<List<String>> gameListMapper(ResultSet gameSet) throws SQLException {
        ArrayList<List<String>> games = new ArrayList<>();
        while (gameSet.next()) {
            ArrayList<String> gameList = new ArrayList<>();
            gameList.add(gameSet.getString("game_id"));
            gameList.add(gameSet.getString("game_name"));
            games.add(gameList);
        }
        return games;
    }

    /**
     * Saves a game to the database.
     *
     * @param game The game to save.
     */
    public void save(Game game) {
        try (Connection connection = getConnection()) {
            game.save(connection);
        } catch (SQLException e) {
            sqlUtils.logSQLException(e);
        }
    }

    /**
     * Gets all games from the database.
     *
     * @return A list of all games in the database.
     */
    public List<Game> getAll() {
        Connection connection = getConnection();
        try (Statement statement = connection.createStatement()) {
            String query = "SELECT * FROM game g";
            try (ResultSet gameSet = statement.executeQuery(query)) {
                return new Game().mapList(gameSet, connection);
            }
        } catch (SQLException e) {
            sqlUtils.logSQLException(e);
        }
        return new ArrayList<>();
    }

    /**
     * Gets a specific game from the database.
     *
     * @param id The id of the game to get.
     * @return The game with the given id.
     */
    public Game load(String id) {
        Game dummyGame = new Game();
        try (Connection connection = getConnection()) {
            return dummyGame.map(dummyGame.getLoad(connection, id), connection);
        } catch (SQLException e) {
            sqlUtils.logSQLException(e);
        }
        return null;
    }

    /**
     * Gets a connection to the database using the {@link HSQLDBUtils} class.
     *
     * @return A connection to the database.
     * @author Jordan Geurtsen
     * @see HSQLDBUtils
     */
    private Connection getConnection() {
        return sqlUtils.getConnection();
    }
}
