package nl.han.gamestate.saver;

import com.google.inject.Guice;
import com.google.inject.Injector;
import nl.han.HSQLDBUtils;
import nl.han.ISQLUtils;
import nl.han.modules.MockedBinding;
import nl.han.modules.ModuleFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testing class for {@link GameDBInit}.
 *
 * @see <a href="https://confluenceasd.aimsites.nl/display/ASDS1G2/Testrapport+game+core">Testrapport</a>
 */
class DBInitTest {
    private final String className = this.getClass().getSimpleName();
    private GameDBInit sut;
    private Connection connection;

    @BeforeEach
    void setup() throws SQLException {
        connection = DriverManager.getConnection("jdbc:hsqldb:mem:" + className + ";create=true", "SA", "SA");

        ModuleFactory factory = new ModuleFactory(new MockedBinding<>(ISQLUtils.class, HSQLDBUtils.class));
        Injector injector = Guice.createInjector(factory.createModules());

        sut = injector.getInstance(GameDBInit.class);

        when(sut.sqlUtils.getConnection()).thenReturn(connection);
    }

    /**
     * Test code GCGS-5
     */
    @ParameterizedTest
    @ValueSource(strings = {"BOUNDED_VALUE", "CHUNK", "CONFIG", "CREATURE", "GAME", "ITEM", "PLAYER"})
    @DisplayName("test tables exists after table init")
    void testTableInitNoExists(String tableName) throws SQLException {
        // Arrange
        when(sut.sqlUtils.tableExists(anyString(), anyString())).thenReturn(false);

        // Act
        sut.init();

        // Assert
        connection = DriverManager.getConnection("jdbc:hsqldb:mem:" + className + ";create=true", "SA", "SA");
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet resultSet = metaData.getTables(null, "PUBLIC", tableName, new String[]{"TABLE"});
        assertTrue(resultSet.next());
    }

    /**
     * Test code GCGS-6
     */
    @Test
    @DisplayName("test if tables already exists init does not create them")
    void testNoInitWhenExists() throws SQLException {
        // Arrange
        when(sut.sqlUtils.tableExists(anyString(), anyString())).thenReturn(true);

        // Act
        sut.init();

        // Assert
        verify(sut.sqlUtils, never()).getConnection();
    }
}
