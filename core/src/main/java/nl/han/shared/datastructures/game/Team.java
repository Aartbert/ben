package nl.han.shared.datastructures.game;

import lombok.*;
import nl.han.ISavable;
import nl.han.shared.datastructures.creature.Player;
import nl.han.shared.datastructures.world.Chunk;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Represents a Team. A team is a collection of players, each having a role to play.
 * Team also maintains a chat history and spawn point.
 *
 * @author Jordan Geurtsen
 * @see Player
 * @see ChatMessage
 * @see Chunk
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Team implements ISavable<Team> {
    private UUID id;
    private final Deque<ChatMessage> chats = new ArrayDeque<>();
    private final List<Player> players = new ArrayList<>();
    private Chunk spawnChunk;
    private UUID gameId;

    /**
     * Check of the player contains in team.
     *
     * @param player The current {@link Player player}
     * @return A boolean is the player is part of this team
     */
    public boolean search(Player player) {
        return players.contains(player);
    }

    /**
     * {@inheritDoc}
     * @param connection The {@link Connection} to the database.
     * @throws SQLException
     */
    @Override
    public void tableInit(Connection connection) throws SQLException {
        String creationString =
                "CREATE TABLE IF NOT EXISTS team (" +
                        "team_id UUID PRIMARY KEY NOT NULL," +
                        "chunk_id UUID FOREIGN KEY REFERENCES chunk(chunk_id) ON DELETE CASCADE NULL," +
                        "game_id UUID FOREIGN KEY REFERENCES game(game_id) ON DELETE CASCADE NULL)";
        try (PreparedStatement statement = connection.prepareStatement(creationString)) {
            statement.execute();
        }
    }

    @Override
    public void insert(Connection connection) throws SQLException {
        String query = "INSERT INTO team(team_id,chunk_id,game_id) VALUES (?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, String.valueOf(id));
            statement.setString(2, String.valueOf(spawnChunk.getId()));
            statement.setString(3, String.valueOf(gameId));
            statement.execute();
        }
    }

    @Override
    public void update(Connection connection) throws SQLException {
        String query = "UPDATE team SET chunk_id = ? WHERE team_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, String.valueOf(spawnChunk.getId()));
            statement.setString(2, String.valueOf(id));
            statement.execute();
        }
    }

    @Override
    public ResultSet getLoad(Connection connection, String id) throws SQLException {
        String query = "SELECT * FROM team WHERE team_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, id);
            return statement.executeQuery();
        }
    }

    @Override
    public List<Team> mapList(ResultSet resultSet, Connection connection) throws SQLException {
        ArrayList<Team> mapped = new ArrayList<>();
        while (resultSet.next()) {
            UUID teamId = UUID.fromString(resultSet.getString("team_id"));
            UUID loadedGameId = UUID.fromString(resultSet.getString("game_id"));
            mapped.add(new Team(teamId, null, loadedGameId));
        }
        return mapped;
    }

    @Override
    public Team map(ResultSet resultSet, Connection connection) throws SQLException {
        if (resultSet.next()) {
            UUID teamId = UUID.fromString(resultSet.getString("team_id"));
            UUID loadedGameId = UUID.fromString(resultSet.getString("game_id"));
            return new Team(teamId, null, loadedGameId);
        }
        return null;
    }

    @Override
    public boolean exists(Connection connection) throws SQLException {
        String sql = "SELECT 1 FROM team WHERE team_id=?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, String.valueOf(id));
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
}