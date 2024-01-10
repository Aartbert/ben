package nl.han.gamestate.saver;

import nl.han.shared.datastructures.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Testing class for {@link Config}.
 *
 * @see <a href="https://confluenceasd.aimsites.nl/display/ASDS1G2/Testrapport+game+core">Testrapport</a>
 */
class ConfigTest {
    private final String className = this.getClass().getSimpleName();
    private Connection connection;

    @BeforeEach
    void setup() throws SQLException {
        connection = DriverManager.getConnection("jdbc:hsqldb:mem:" + className + ";create=true", "SA", "SA");
    }

    /**
     * Test code GCGS-3
     */
    @Test
    @DisplayName("test saving and loading the config gives the same config")
    void testUpdateSameConfig() throws SQLException {
        //Arrange
        Config expected = new Config(UUID.randomUUID(), "a", "b");
        new Config().tableInit(connection);

        //Act
        expected.save(connection);
        Config newConfig = expected.map(expected.getLoad(connection, expected.getId().toString()), connection);

        //Assert
        assertEquals(expected.getRules(), newConfig.getRules());
    }

    /**
     * Test code GCGS-4
     */
    @Test
    @DisplayName("test saving and loading another config gives another config")
    void testUpdateOtherConfig() throws SQLException {
        //Arrange
        Config sut = new Config(UUID.randomUUID(), "a", "b");
        sut.tableInit(connection);

        //Act
        sut.save(connection);
        Config loadedSut = sut.map(sut.getLoad(connection, sut.getId().toString()), connection);

        //Assert
        assertEquals(sut.getRules(), loadedSut.getRules());

        //Act
        sut.setRules("c");
        sut.save(connection);
        Config newLoadedSut = sut.map(sut.getLoad(connection, sut.getId().toString()), connection);

        //Assert
        assertNotEquals(loadedSut.getRules(), newLoadedSut.getRules());
        assertEquals(sut.getRules(), newLoadedSut.getRules());
    }
}
