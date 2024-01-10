package nl.han;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This interface is used to make objects savable to a database. It provides methods to create the table, insert and update
 * the object. It also provides a method to map a ResultSet to an object.
 *
 * @author Djurre Tieman
 */
public interface ISavable<T> {
    /**
     * The SQL query to create the table for this object.
     *
     * @param connection The {@link Connection} to the database.
     * @author Djurre Tieman
     */
    void tableInit(Connection connection) throws SQLException;

    /**
     * Insert this object into the database.
     *
     * @param connection The {@link Connection} to the database.
     * @author Djurre Tieman, Rieke Jansen
     */
    void insert(Connection connection) throws SQLException;

    /**
     * Update this object in the database.
     *
     * @param connection The {@link Connection} to the database.
     * @author Djurre Tieman, Rieke Jansen
     */
    void update(Connection connection) throws SQLException;

    /**
     * Get a {@link ResultSet} of this object loaded from the database.
     *
     * @param connection The {@link Connection} to the database.
     * @return The {@link ResultSet} of this object loaded from the database.
     * @author Rieke Jansen
     */
    ResultSet getLoad(Connection connection, String id) throws SQLException;

    /**
     * Map a {@link ResultSet} to a list of an object.
     *
     * @param resultSet The {@link ResultSet} to map to an object.
     * @return The list mapped from the {@link ResultSet}.
     * @author Djurre Tieman
     */
    default List<T> mapList(ResultSet resultSet, Connection connection) throws SQLException {
        return new ArrayList<>();
    }

    /**
     * Map a {@link ResultSet} to an object.
     *
     * @param resultSet The {@link ResultSet} to map to an object.
     * @return The object mapped from the {@link ResultSet}.
     * @author Djurre Tieman
     */
    default T map(ResultSet resultSet, Connection connection) throws SQLException{
        return null;
    }

    /**
     * Saves the object to the database.
     * @param connection The {@link Connection} to the database.
     * @throws SQLException if the query fails.
     * @author Rieke Jansen
     */
    default void save(Connection connection) throws SQLException {
        if(exists(connection)){
            update(connection);
        } else{
            insert(connection);
        }
    }

    /**
     * Checks if the object already exists in the database.
     * @param connection The {@link Connection} to the database.
     * @return true if the object exists in the database.
     * @throws SQLException if the query fails.
     * @author Rieke Jansen
     */
    boolean exists(Connection connection) throws SQLException;
}
