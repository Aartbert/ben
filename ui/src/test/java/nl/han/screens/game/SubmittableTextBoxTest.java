package nl.han.screens.game;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.Interactable.Result;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import nl.han.interfaces.ISubmitListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the SubmittableTextBox class.
 *
 * @see <a href="https://confluenceasd.aimsites.nl/x/owXjGQ">Testrapport</a>
 */
class SubmittableTextBoxTest {
    SubmittableTextBox sut;
    List<ISubmitListener> submitListeners;
    ISubmitListener mockedSubmitListener;

    @BeforeEach
    void setUp() {
        sut = spy(new SubmittableTextBox(new TerminalSize(80, 24)));
        mockedSubmitListener = mock();
        submitListeners = new ArrayList<>();
        submitListeners.add(mockedSubmitListener);
        sut.setSubmitListeners(submitListeners);

    }

    @Test
    @DisplayName("Testing if the enter keystroke calls the submitListener.")
    void testHandleKeyStrokeWithEnter() {

        // Arrange
        sut.setText("TEST");

        KeyStroke keyStrokeToHandle = new KeyStroke(KeyType.Enter);
        Result expectedResult = Result.HANDLED;

        // Act
        Result actual = sut.handleKeyStroke(keyStrokeToHandle);

        // Assert
        verify(mockedSubmitListener).onSubmit("TEST");
        assertEquals(expectedResult, actual);
    }

    @Test
    @DisplayName("Testing if other keys are also handled correctly.")
    void testHandleKeyStrokeWithoutEnter() {

        // Arrange
        KeyStroke keyStrokeToHandle = mock();
        Result expectedResult = Result.HANDLED;

        when(keyStrokeToHandle.getKeyType()).thenReturn(KeyType.Character);

        // Act
        Result actual = sut.handleKeyStroke(keyStrokeToHandle);

        // Assert
        assertEquals(expectedResult, actual);

    }
}
