package nl.han.shared.datastructures;

import lombok.*;
import nl.han.ISavable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Configuration class responsible for keeping settings and rules
 *
 * @author Jordan Geurtsen
 */
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Config implements ISavable<Config> {
    private final UUID id;
    private String rules;
    private String type;

    /**
     * {@inheritDoc}
     * @param connection The {@link Connection} to the database.
     * @throws SQLException
     * @author Rieke Jansen
     */
    @Override
    public void tableInit(Connection connection) throws SQLException {
        String creationString =
                "CREATE TABLE IF NOT EXISTS config (" +
                        "config_id UUID PRIMARY KEY NOT NULL," +
                        "config_rules varchar(8000) NOT NULL," +
                        "type varchar(255) NOT NULL)";
        try (PreparedStatement statement = connection.prepareStatement(creationString)) {
            statement.execute();
        }
    }

    /**
     * {@inheritDoc}
     * @param connection The {@link Connection} to the database.
     * @throws SQLException
     * @author Rieke Jansen
     */
    @Override
    public void insert(Connection connection) throws SQLException {
        String query = "INSERT INTO config(config_id,config_rules,type) VALUES (?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, String.valueOf(id));
            statement.setString(2, rules);
            statement.setString(3, type);
            statement.execute();
        }
    }

    /**
     * checks if the config exists in the database.
     * @param connection The {@link Connection} to the database.
     * @return true if the config exists.
     * @throws SQLException if the query fails.
     * @author Rieke Jansen
     */
    @Override
    public boolean exists(Connection connection) throws SQLException {
        String sql = "SELECT 1 FROM config WHERE config_id=?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, String.valueOf(id));
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    /**
     * {@inheritDoc}
     * @param connection The {@link Connection} to the database.
     * @throws SQLException
     * @author Rieke Jansen
     */
    @Override
    public void update(Connection connection) throws SQLException {
        String query = "UPDATE config SET config_rules=? WHERE config_id=?";
        try (PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, rules);
            statement.setString(2, String.valueOf(id));
            statement.execute();
        }
    }

    /**
     * {@inheritDoc}
     * @param connection The {@link Connection} to the database.
     * @param id
     * @return ResultSet of the config.
     * @throws SQLException
     * @author Rieke Jansen
     */
    @Override
    public ResultSet getLoad(Connection connection, String id) throws SQLException {
        String query = "SELECT config_id,config_rules,type FROM config WHERE config_id=?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, id);
            return statement.executeQuery();
        }
    }

    @Override
    public Config map(ResultSet resultSet, Connection connection) throws SQLException {
        if (resultSet.next()) {
            UUID configId = UUID.fromString(resultSet.getString("config_id"));
            String configRules = resultSet.getString("config_rules");
            String configType = resultSet.getString("type");
            return new Config(configId, configRules, configType);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * @param resultSet The {@link ResultSet} to map to an object.
     * @param connection
     * @return ArrayList of all configs in the ResultSet.
     * @throws SQLException
     * @author Rieke Jansen
     */
    @Override
    public ArrayList<Config> mapList(ResultSet resultSet, Connection connection) throws SQLException {
        ArrayList<Config> mapped = new ArrayList<>();
        while (resultSet.next()) {
            UUID configId = UUID.fromString(resultSet.getString("config_id"));
            String configRules = resultSet.getString("rules");
            String configType = resultSet.getString("type");
            Config config = new Config(configId, configRules, configType);

            mapped.add(config);
        }
        return mapped;
    }

    @Override
    public String toString() {
        return "Config{" +
                "id=" + id +
                ", rules='" + rules + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public String toJson() {
        return "{" +
                "\"id\":\"" + id + "\"," +
                "\"rules\":\"" + rules + "\"," +
                "\"type\":\"" + type + "\"" +
                "}";
    }
}
