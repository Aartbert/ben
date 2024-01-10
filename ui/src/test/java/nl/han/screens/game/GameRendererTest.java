package nl.han.screens.game;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Panel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the GameRenderer class.
 *
 * @see <a href="https://confluenceasd.aimsites.nl/x/owXjGQ">Testrapport</a>
 */
class GameRendererTest {
    GameRenderer sut;
    Panel mockedPanel;

    @BeforeEach
    void setUp() {
        sut = spy();
        mockedPanel = mock();
        sut.setGamePanel(mockedPanel);
    }

    @Test
    @DisplayName("Testing if setupWorldTiles is called and if the returned panel is as expected.")
    void testCreateGameScreen() {
        // Arrange
        Panel actualPanel;
        Panel expectedPanel = new Panel(new GridLayout(80).setHorizontalSpacing(0).setVerticalSpacing(0)
                .setLeftMarginSize(0).setRightMarginSize(0).setTopMarginSize(0).setBottomMarginSize(0));
        expectedPanel.setPreferredSize(new TerminalSize(80, 24));
        doNothing().when(sut).setupWorldTiles();

        // Act
        actualPanel = sut.createGameScreen();

        // Assert
        verify(sut).setupWorldTiles();
        assertEquals(expectedPanel.getPreferredSize(), actualPanel.getPreferredSize());
    }

}