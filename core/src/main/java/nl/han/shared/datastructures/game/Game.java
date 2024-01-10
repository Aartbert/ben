package nl.han.shared.datastructures.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import nl.han.ISavable;
import nl.han.shared.datastructures.Config;
import nl.han.shared.datastructures.creature.Bot;
import nl.han.shared.datastructures.creature.Creature;
import nl.han.shared.datastructures.creature.Player;
import nl.han.shared.datastructures.world.Chunk;
import nl.han.shared.datastructures.world.World;
import nl.han.shared.enums.GameMode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This is the Game class which includes information about a game like its id, name, game mode,
 * the world where it occurs, teams, players, and monsters.
 *
 * @author Jordan Geurtsen
 * @see ISavable
 * @see GameMode
 * @see World
 * @see Team
 * @see Player
 * @see Bot
 */
@Getter
@Setter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class Game implements ISavable<Game> {
    private final UUID id;
    private final String name;
    private final GameMode gameMode;
    private final World world;
    private List<Team> teams = new ArrayList<>();
    private final List<Player> players = new ArrayList<>();
    private final List<Bot> monsters = new ArrayList<>();
    private Config monsterConfig;

    /**
     * {@inheritDoc}
     * Creates the prepared statement for the game table.
     * <br/>
     * This table has the following columns:
     * <ul>
     *     <li>game_id</li>
     *     <li>seed</li>
     *     <li>game_name</li>
     *     <li>config_id</li>
     *     <li>gamemode</li>
     * </ul>
     * <br/>
     * The game_id is the primary key of the table and is a varchar(255).
     *
     * @param connection The connection.
     * @throws SQLException If the query fails.
     * @author Jordan Geurtsen, Rieke Jansen
     */
    @Override
    public void tableInit(Connection connection) throws SQLException {
        String creationString = "CREATE TABLE IF NOT EXISTS game (" +
                "game_id UUID PRIMARY KEY NOT NULL," +
                "seed BIGINT NOT NULL," +
                "game_name varchar(255) NOT NULL," +
                "gamemode varchar(255) NOT NULL," +
                "world_config_id UUID FOREIGN KEY REFERENCES config(config_id) NULL, " +
                "monster_config_id UUID FOREIGN KEY REFERENCES config(config_id) NULL)";
        try (PreparedStatement statement = connection.prepareStatement(creationString)) {
            statement.execute();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws SQLException If the query fails.
     * @author Jordan Geurtsen, Rieke Jansen
     */
    @Override
    public void insert(Connection connection) throws SQLException {
        Config worldConfig = world.getConfig();
        worldConfig.save(connection);
        if (monsterConfig != null) {
            monsterConfig.save(connection);
        }

        String gameQuery = "INSERT INTO game(game_id,seed,game_name,world_config_id,gamemode,monster_config_id) " +
                "VALUES(?,?,?,?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(gameQuery)) {
            statement.setString(1, String.valueOf(id));
            statement.setLong(2, world.getSeed());
            statement.setString(3, name);
            statement.setString(4, String.valueOf(world.getConfig().getId()));
            statement.setString(5, gameMode.toString());
            statement.setString(6, String.valueOf(monsterConfig.getId()));
            statement.execute();
        }

        saveGameAttributes(connection);
    }

    /**
     * {@inheritDoc}
     *
     * @author Rieke Jansen
     */
    @Override
    public void update(Connection connection) throws SQLException {
        saveGameAttributes(connection);
    }

    private void saveGameAttributes(Connection connection) throws SQLException {
        assert world != null;
        for (Chunk chunk : world.getChunks()) {
            chunk.save(connection);
        }
        if (teams != null) {
            for (Team team : teams) {
                team.save(connection);
            }
        }
        assert players != null;
        for (Player player : players) {
            player.save(connection);
        }
        assert monsters != null;
        for (Bot monster : monsters) {
            monster.save(connection);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws SQLException If the query fails.
     * @author Rieke Jansen
     * @see ISavable
     * @see Connection
     */
    @Override
    public ResultSet getLoad(Connection connection, String id) throws SQLException {
        String query = "SELECT g.game_id, g.game_name, g.seed, g.world_config_id, wc.config_rules as world_config_rules, g.gamemode, g.monster_config_id, mc.config_rules as monster_config_rules FROM game g INNER JOIN config wc ON g.world_config_id = wc.config_id INNER JOIN config mc on g.monster_config_id = mc.config_id WHERE g.game_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, id);
            return statement.executeQuery();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @author Rieke Jansen
     */
    @Override
    public Game map(ResultSet resultSet, Connection connection) throws SQLException {
        resultSet.next();
        UUID gameId = UUID.fromString(resultSet.getString("game_id"));
        String gameName = resultSet.getString("game_name");
        long seed = resultSet.getLong("seed");
        Config config = new Config(UUID.fromString(resultSet.getString("world_config_id")), resultSet.getString("world_config_rules"), "WORLD");
        World world1 = new World(seed, new ArrayList<>(), config);
        Game game = new Game(gameId, gameName, GameMode.CTF, world1);
        game.setMonsterConfig(new Config(UUID.fromString(resultSet.getString("monster_config_id")), resultSet.getString("monster_config_rules"), "MONSTER"));
        return game;
    }

    /**
     * {@inheritDoc}
     *
     * @param connection
     * @return
     * @author Rieke Jansen
     */
    @Override
    public boolean exists(Connection connection) throws SQLException {
        String sql = "SELECT 1 FROM game WHERE game_id=?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, String.valueOf(id));
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param resultSet  The {@link ResultSet} to map to an object.
     * @param connection The {@link Connection} to the database.
     * @return ArrayList of all games in the ResultSet.
     * @throws SQLException
     */
    @Override
    public List<Game> mapList(ResultSet resultSet, Connection connection) throws SQLException {
        List<Game> games = new ArrayList<>();
        while (resultSet.next()) {
            UUID gameId = UUID.fromString(resultSet.getString("game_id"));
            String gameName = resultSet.getString("game_name");
            long seed = resultSet.getLong("seed");
            World world1 = new World(seed, new ArrayList<>(), new Config(UUID.randomUUID(), "dslkfj", "world"));
            games.add(new Game(gameId, gameName, GameMode.CTF, world1));
        }
        return games;
    }

    /**
     * {@link String} representation of the {@link Game game}.
     *
     * @return {@link String} representation of the {@link Game game}.
     */
    @Override
    public String toString() {
        return "Game: " + name + " with id: " + id + " and gamemode: " + gameMode;
    }

    /**
     * Get the {@link Team team} in which the {@link Player player} is a part of.
     *
     * @param player The current {@link Player player}
     * @return The {@link Team team}
     */
    public Team getTeam(Player player) {
        return teams.stream()
                .filter(team -> team.search(player))
                .findFirst()
                .orElse(null);
    }

    /**
     * Combines all {@link Bot monsters} and {@link Player players} into one list
     * for further use and filtering.
     *
     * @return A {@link List list} of {@link Creature creatures}
     */
    public List<Creature> getCreatures() {
        List<Creature> creatures = new ArrayList<>();
        creatures.addAll(monsters);
        creatures.addAll(players);
        return creatures;
    }

    /**
     * Add a player to the game
     *
     * @param player The player to add to the game
     * @author Justin Slijkhuis
     */
    public void addPlayer(Player player) {
        players.add(player);
    }

    /**
     * Add a monster to the game
     *
     * @param monster to add to the game
     * @author Adil Sadiki
     */
    public void addMonster(Bot monster) {
        monsters.add(monster);
    }
}
