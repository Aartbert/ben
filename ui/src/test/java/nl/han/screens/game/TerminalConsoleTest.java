package nl.han.screens.game;

import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.TerminalScreen;
import nl.han.interfaces.IKeyStrokeListener;
import nl.han.interfaces.ISubmitListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the TerminalConsole class.
 *
 * @see <a href="https://confluenceasd.aimsites.nl/x/owXjGQ">Testrapport</a>
 */
class TerminalConsoleTest {
    TerminalConsole sut;
    HistoryLabel mockHistoryLabel;

    @BeforeEach
    void setUp() {
        TerminalScreen mockTerminalScreen = mock();
        sut = spy(new TerminalConsole(mockTerminalScreen));

        mockHistoryLabel = mock();
        SubmittableTextBox mockSubmittableTextBox = mock();
        List<IKeyStrokeListener> mockKeyStrokeListeners = mock();

        sut.setHistoryLabel(mockHistoryLabel);
        sut.setConsole(mockSubmittableTextBox);
        sut.setKeyStrokeListeners(mockKeyStrokeListeners);
    }

    @Test
    @DisplayName("Tests if a panel is created.")
    void testCreateConsole() {
        // Arrange

        // Act
        var result = sut.createConsole();

        // Assert
        assertNotNull(result);
        assertInstanceOf(Panel.class, result);
    }

    @Test
    @DisplayName("Tests if the HistoryLabel is updated when method updateHistoryLabel is called.")
    void testUpdateHistoryLabel() {
        // Arrange
        sut.setHistoryLabel(mockHistoryLabel);

        // Act
        sut.updateCommandLog("Test");

        // Assert
        verify(mockHistoryLabel).addToHistory(any());
    }

    @Test
    @DisplayName("Tests if KeyStroke handle happens in TerminalConsole")
    void testPollInput() throws IOException {
        // Arrange
        when(sut.getTerminalScreen().pollInput()).thenReturn(new KeyStroke(KeyType.Enter));
        sut.setTyping(false);

        // // Act
        sut.pollInput();

        // Assert
        verify(sut).handleKeyStroke(new KeyStroke(KeyType.Enter));
    }

    @Test
    @DisplayName("Tests if any SubmitListener is added to TerminalConsole")
    void testAddSubmitListener() {
        // Arrange
        ISubmitListener mockedTextChangeListener = mock();
        SubmittableTextBox mockSubmittableTextBox = mock();
        sut.setConsole(mockSubmittableTextBox);

        // Act
        sut.addSubmitListener(mockedTextChangeListener);

        // Assert
        verify(mockSubmittableTextBox).addSubmitListener(any());
    }

    @Test
    @DisplayName("Tests if KeyStroke with enter is handled and text box takes focus.")
    void testHandleKeyStrokeWithEnter() {
        // Arrange
        SubmittableTextBox mockSubmittableTextBox = mock();
        sut.setConsole(mockSubmittableTextBox);

        // Act
        sut.handleKeyStroke(new KeyStroke(KeyType.Enter));

        // Assert
        verify(mockSubmittableTextBox).setEnabled(true);
        verify(mockSubmittableTextBox).takeFocus();
    }
}