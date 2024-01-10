package nl.han;

import lombok.extern.java.Log;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

/**
 * This class is responsible for getting a connection to the database.
 * <br/>
 * By declaring the jdbcURL, jdbcUsername, and jdbcPassword variables, this class can get a connection to the database.
 *
 * @author Jordan Geurtsen
 */
@Log
public class HSQLDBUtils implements ISQLUtils {
    private Properties properties;
    private Connection connection;

    /**
     * Gets a property from the db.properties file.
     * <br/>
     * This function is used to get the jdbcURL, jdbcUsername, and jdbcPassword.
     *
     * @param property The property to get from the db.properties file.
     * @return The value of the property or null if the property does not exist.
     */
    public String getPropertyString(String property) {
        if (properties == null) {
            try {
                properties = new Properties();
                properties.load(getClass().getClassLoader().getResourceAsStream("db.properties"));
            } catch (IOException e) { log.log(Level.SEVERE,e.getMessage(),e); }
        }
        return properties.getProperty(property);
    }

    /**
     * Gets a connection to the database.
     *
     * @return A connection to the database or null if the connection fails.
     * @author Jordan Geurtsen, Rieke Jansen, Djurre Tieman
     * @see DriverManager
     */
    @Override
    public Connection getConnection() {
        try {
            if(connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(getPropertyString("connectionString"), getPropertyString("username"), getPropertyString("password"));
            }
        } catch (SQLException e) {
            logSQLException(e);
        }
        return connection;
    }

    /**
     * {@inheritDoc}
     *
     * @param ex The SQLException to print to the console.
     * @author Jordan Geurtsen, Djurre Tieman
     * @see SQLException
     */
    @Override
    public void logSQLException(SQLException ex) {
        log.log(Level.SEVERE, ex.getMessage(), ex);
    }
}