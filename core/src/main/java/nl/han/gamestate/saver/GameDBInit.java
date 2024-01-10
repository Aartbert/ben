package nl.han.gamestate.saver;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.java.Log;
import nl.han.HSQLDBUtils;
import nl.han.ISQLUtils;
import nl.han.shared.datastructures.BoundedValue;
import nl.han.shared.datastructures.Config;
import nl.han.shared.datastructures.Item;
import nl.han.shared.datastructures.creature.Player;
import nl.han.shared.datastructures.game.Game;
import nl.han.shared.datastructures.game.Team;
import nl.han.shared.datastructures.world.Chunk;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Initializes the database with the required tables.
 * <br/>
 * This class initializes a connection to an HSQLDB database and creates the required tables.
 *
 * @author Jordan Geurtsen, Rieke Jansen
 * @see HSQLDBUtils
 */
@Log
@Singleton
public class GameDBInit {

    @Inject
    protected ISQLUtils sqlUtils;

    @Inject GlobalConfigRepository globalConfigRepository;

    /**
     * Initializes the database with the required tables.
     * <br/>
     * Calls the methods for creating the tables, adding constraints and inserting the data.
     *
     * @author Jordan Geurtsen, Rieke Jansen
     */
    public void init() {
        try {
            log.info("Starting database");
            if (sqlUtils.tableExists("public", "game")) {
                log.info("Schema found, skipping creation");
                return;
            }
            log.info("No schema found, creating new database");
            Connection connection = sqlUtils.getConnection();
            new Config().tableInit(connection);
            new Game().tableInit(connection);
            new Chunk().tableInit(connection);
            new BoundedValue().tableInit(connection);
            new Team().tableInit(connection);
            new Player().tableInit(connection);
            new Item().tableInit(connection);
            globalConfigRepository.tableInit();

            connection.close();
            log.info("Database created");
        } catch (SQLException e) {
            sqlUtils.logSQLException(e);
        }
    }
}
