package nl.han.screens.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the HistoryLabel class.
 * <br/>
 * The HistoryLabel class is responsible for displaying a history of lines.
 *
 * TODO: Test codes toevoegen zodra testplan en testrapport zijn gefixt
 *
 * @author Jordan Geurtsen
 * @see <a href="https://confluenceasd.aimsites.nl/x/owXjGQ">Testrapport</a>
 * @see HistoryLabel
 */
class HistoryLabelTest {
    private HistoryLabel sut;

    @BeforeEach
    void setUp() {
        sut = new HistoryLabel("", 5, 80, false, "");
    }

    /**
     * Test code {UI##}
     */
    @Test
    @DisplayName("Test adding valid line to history")
    void testAddToHistoryWithValidLine() {
        // Act
        sut.addToHistory("Test");

        // Assert
        assertTrue(sut.getText().contains("Test"));
    }

    /**
     * Test code {UI##}
     */
    @Test
    @DisplayName("Test adding line to history with inverted set to true")
    void testAddToHistoryWithInvertedTrue() {
        // Arrange
        sut = new HistoryLabel("", 5, 80, true, "");

        // Act
        sut.addToHistory("Test");

        // Assert
        assertTrue(sut.getText().contains("Test"));
    }

    /**
     * Test code {UI##}
     */
    @Test
    @DisplayName("Test adding lines exceeding max history height")
    void testAddToHistoryExceedingMaxHistoryHeight() {
        // Arrange
        for (int i = 0; i < 5; i++) {
            sut.addToHistory("Test" + i);
        }

        // Act
        sut.addToHistory("TestExceeding");

        // Assert
        assertFalse(sut.getText().contains("Test0"));
        assertTrue(sut.getText().contains("TestExceeding"));
    }

    /**
     * Test code {UI##}
     */
    @Test
    @DisplayName("Test adding lines exceeding max history height with inverted set to true")
    void testAddToHistoryExceedingMaxHistoryHeightWithInvertedTrue() {
        // Arrange
        sut = new HistoryLabel("", 5, 80, true, "");
        for (int i = 0; i < 5; i++) {
            sut.addToHistory("Test" + i);
        }

        // Act
        sut.addToHistory("TestExceeding");

        // Assert
        assertFalse(sut.getText().contains("Test0"));
        assertTrue(sut.getText().contains("TestExceeding"));
    }
}