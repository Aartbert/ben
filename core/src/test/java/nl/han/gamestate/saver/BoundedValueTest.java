package nl.han.gamestate.saver;

import nl.han.shared.datastructures.BoundedValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Testing class for {@link BoundedValue}.
 *
 * @see <a href="https://confluenceasd.aimsites.nl/display/ASDS1G2/Testrapport+game+core">Testrapport</a>
 */
class BoundedValueTest {
    private Connection connection;

    @BeforeEach
    void setup() throws SQLException {
        String className = this.getClass().getSimpleName();
        connection = DriverManager.getConnection("jdbc:hsqldb:mem:" + className + ";create=true", "SA", "SA");
    }

    /**
     * Test code GCGS-1
     */
    @Test
    @DisplayName("test Saving and loading the bounded value gives the same value")
    void updateSameBoundedValue() throws SQLException {
        //Arrange
        BoundedValue sut = new BoundedValue(10, 10, 1);
        sut.tableInit(connection);

        //Act
        sut.save(connection);
        BoundedValue loadedSut = sut.map(sut.getLoad(connection, sut.getId().toString()), connection);

        //Assert
        assertEquals(sut.getValue(), loadedSut.getValue());
    }

    /**
     * Test code GCGS-2
     */
    @Test
    @DisplayName("test saving and loading another bounded value gives another value")
    void updateOtherBoundedValue() throws SQLException {
        //Arrange
        BoundedValue sut = new BoundedValue(10, 10, 1);
        sut.tableInit(connection);

        //Act
        sut.save(connection);
        BoundedValue loadedSut = sut.map(sut.getLoad(connection, sut.getId().toString()), connection);

        //Assert
        assertEquals(sut.getValue(), loadedSut.getValue());

        //Act
        sut.decreaseValue(1);
        sut.save(connection);
        BoundedValue newLoadedSut = sut.map(sut.getLoad(connection, sut.getId().toString()), connection);

        //Assert
        assertNotEquals(loadedSut.getValue(), newLoadedSut.getValue());
        assertEquals(sut.getValue(), newLoadedSut.getValue());
    }
}
