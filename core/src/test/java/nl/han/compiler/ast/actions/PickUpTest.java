package nl.han.compiler.ast.actions;

import nl.han.shared.enums.Action;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testing class for {@link PickUp}.
 * @see <a href="https://confluenceasd.aimsites.nl/display/ASDS1G2/Testrapport+Onderzoek+Programmeren+Agents">Testrapport</a>
 */
class PickUpTest {

    private PickUp sut;

    @BeforeEach
    void setUp() {
        sut = new PickUp();
    }

    /**
     * Test code COM14
     */
    @Test
    @DisplayName("test if a pickup action returns PAK_ITEM_OP")
    void getAction() {
        // Arrange
        List<Action> expected = List.of(Action.SEARCH_ITEM);

        // Act
        List<Action> actual = sut.getAction();

        // Assert
        assertEquals(expected, actual);
    }
}
