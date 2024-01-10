package nl.han;

import java.sql.*;

public interface ISQLUtils {

    Connection getConnection();

    /**
     * Logs an {@link SQLException}.
     *
     * @param ex The SQLException to print to the console.
     * @author Jordan Geurtsen, Djurre Tieman
     * @see SQLException
     */
    void logSQLException(SQLException ex);

    /**
     * Checks if a table exists in the given schema.
     * <br/>
     * This function checks if a table exists by checking the metadata of the database.
     *
     * @param schemaName The name of the schema to check. Non-case-sensitive.
     * @param tableName  The name of the table to check. Non-case-sensitive.
     * @return True if the table exists, false if the table does not exist.
     * @throws SQLException If the query fails.
     * @author Djurre Tieman
     */
    default boolean tableExists(String schemaName, String tableName) throws SQLException {
        DatabaseMetaData metaData = getConnection().getMetaData();
        ResultSet resultSet = metaData.getTables(null, schemaName.toUpperCase(), tableName.toUpperCase(), new String[]{"TABLE"});
        return resultSet.next();
    }
}
