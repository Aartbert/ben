package nl.han.gamestate.saver;

import com.google.inject.Inject;
import jakarta.inject.Singleton;
import nl.han.ISQLUtils;
import nl.han.shared.datastructures.GlobalConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Singleton
public class GlobalConfigRepository {

    @Inject
    private ISQLUtils sqlUtils;

    /**
     * Initializes the global_config table if it does not exist.
     */
    public void tableInit() {
        final String query = "CREATE TABLE IF NOT EXISTS global_config(" +
                "user_name varchar(255) NULL," +
                "agent_config_rules varchar(8000) NULL," +
                "monster_config_rules varchar(8000) NULL)";
        try (Connection connection = sqlUtils.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.execute();
            }
        } catch (SQLException e) {
            sqlUtils.logSQLException(e);
        }
    }

    /**
     * Saves the globalConfig to the database.
     * If the globalConfig already exists, it will be updated.
     *
     * @param globalConfig The globalConfig to be saved.
     */
    public void save(GlobalConfig globalConfig) {
        if (exists()) {
            update(globalConfig);
        } else {
            insert(globalConfig);
        }
    }

    /**
     * Inserts the globalConfig into the database.
     *
     * @param globalConfig The globalConfig to be inserted.
     */
    private void insert(GlobalConfig globalConfig) {
        final String query = "INSERT INTO global_config(user_name,agent_config_rules,monster_config_rules) VALUES (?,?,?)";

        try (Connection connection = sqlUtils.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                setParams(statement, globalConfig);
                statement.execute();
            }
        } catch (SQLException e) {
            sqlUtils.logSQLException(e);
        }
    }

    /**
     * Updates the globalConfig in the database.
     *
     * @param globalConfig The globalConfig to be updated.
     */
    private void update(GlobalConfig globalConfig) {
        final String query = "UPDATE global_config SET user_name = ?, agent_config_rules = ?, monster_config_rules = ?";

        try (Connection connection = sqlUtils.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                setParams(statement, globalConfig);
                statement.execute();
            }
        } catch (SQLException e) {
            sqlUtils.logSQLException(e);
        }
    }

    /**
     * Sets the parameters of the prepared statement.
     *
     * @param statement The prepared statement.
     * @param globalConfig The globalConfig to be saved.
     * @throws SQLException If the query fails.
     */
    private void setParams(PreparedStatement statement, GlobalConfig globalConfig) throws SQLException {
        statement.setString(1, globalConfig.getUserName());
        statement.setString(2, globalConfig.getAgentConfigRules());
        statement.setString(3, globalConfig.getMonsterConfigRules());
    }

    /**
     * Gets the globalConfig from the database.
     *
     * @return The globalConfig.
     */
    public GlobalConfig getFirst() {
        final String query = "SELECT * FROM global_config";

        try (Connection connection = sqlUtils.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                return map(statement.executeQuery());
            }
        } catch (SQLException e) {
            sqlUtils.logSQLException(e);
        }
        return null;
    }

    /**
     * Maps the result set to a globalConfig.
     *
     * @param resultSet The result set.
     * @return The globalConfig.
     * @throws SQLException If the query fails.
     */
    public GlobalConfig map(ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) return null;
        return new GlobalConfig(
                resultSet.getString("user_name"),
                resultSet.getString("agent_config_rules"),
                resultSet.getString("monster_config_rules")
        );
    }

    /**
     * Checks if the globalConfig exists in the database.
     *
     * @return True if the globalConfig exists, false otherwise.
     */
    public boolean exists() {
        final String sql = "SELECT * FROM global_config";

        try (Connection connection = sqlUtils.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    return resultSet.next();
                }
            }
        } catch (SQLException e) {
            sqlUtils.logSQLException(e);
        }
        return false;
    }
}
