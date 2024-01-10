package nl.han.screens.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the ChatRenderer class.
 * <br/>
 * The ChatRenderer class is responsible for displaying chat messages.
 *
 * TODO: Test codes toevoegen zodra testplan en testrapport zijn gefixt
 *
 * @author Jordan Geurtsen
 * @see <a href="https://confluenceasd.aimsites.nl/x/owXjGQ">Testrapport</a>
 * @see ChatRenderer
 */
class ChatRendererTest {
    ChatRenderer sut;
    HistoryLabel mockedHistoryLabel;

    @BeforeEach
    void setUp() {
        sut = new ChatRenderer();
        mockedHistoryLabel = mock(HistoryLabel.class);
        sut.setChatLabel(mockedHistoryLabel);
    }

    /**
     * Test code {UI##}
     */
    @Test
    @DisplayName("Test adding chat message")
    void testAddChatMessage() {
        // Arrange
        String testMessage = "Test message";

        // Act
        sut.addChatMessage(testMessage);

        // Assert
        verify(mockedHistoryLabel).addToHistory(testMessage);
    }
}