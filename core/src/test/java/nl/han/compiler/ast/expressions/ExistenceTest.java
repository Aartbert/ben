package nl.han.compiler.ast.expressions;

import nl.han.compiler.CompilerException;
import nl.han.compiler.ast.IASTNode;
import nl.han.compiler.ast.IASTNodeTest;
import nl.han.compiler.ast.enums.CompilerItem;
import nl.han.compiler.ast.literals.Bool;
import nl.han.compiler.ast.literals.ILiteral;
import nl.han.compiler.ast.literals.Percentage;
import nl.han.compiler.ast.literals.Scalar;
import nl.han.compiler.ast.operators.GreaterThanOperator;
import nl.han.compiler.ast.operators.Operator;
import nl.han.compiler.utils.Getter;
import nl.han.compiler.utils.Setter;
import nl.han.compiler.utils.Volume;
import nl.han.shared.datastructures.BoundedValue;
import nl.han.shared.datastructures.Item;
import nl.han.shared.datastructures.creature.Player;
import nl.han.shared.datastructures.game.Game;
import nl.han.shared.enums.GameMode;
import nl.han.shared.enums.ItemData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.UUID;

import static nl.han.compiler.utils.Point.point;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing class for {@link Existence}.
 *
 * @see <a href="https://confluenceasd.aimsites.nl/display/ASDS1G2/Testrapport+Onderzoek+Programmeren+Agents">Testrapport</a>
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ExistenceTest implements IASTNodeTest {

    private Existence sut;

    private final Volume<IASTNode, Getter, Setter> instructions = Volume.of(
            point(CompilerItem.HEALTH_POTION, () -> sut.getItem(), node -> sut.setItem((CompilerItem) node)),
            point(new GreaterThanOperator(), () -> sut.getOperator(), node -> sut.setOperator((Operator) node)),
            point(new Bool(true), () -> sut.getLiteral(), node -> sut.setLiteral((ILiteral) node))

    );

    @BeforeEach
    void setup() {
        sut = new Existence();
    }

    /**
     * Test code COM30
     */
    @Test
    @DisplayName("test if using a literal other than Scalar and Bool will throw an error")
    void testCheckError() {
        // Arrange
        String expected = "Cannot use a literal other than a value or a boolean.";

        ILiteral literal = new Percentage("50%");
        sut.setLiteral(literal);

        // Act
        CompilerException exception = assertThrows(CompilerException.class, () -> sut.check());

        // Assert
        assertEquals(expected, exception.getMessage());
    }

    /**
     * Test code COM31
     */
    @Test
    @DisplayName("test if checking if the player's inventory does not have the specific item returns true")
    void testValidateEmptyNo() {
        // Arrange
        CompilerItem item = CompilerItem.HEALTH_POTION;
        sut.setItem(item);

        ILiteral literal = new Bool(false);
        sut.setLiteral(literal);

        BoundedValue bounded = new BoundedValue(100, 100, 0);

        Player player = new Player(UUID.randomUUID(), "LOL", null, UUID.randomUUID(), null, bounded, bounded, bounded, "");

        // Act
        boolean actual = sut.validate(player, new Game(UUID.randomUUID(), "LOL", GameMode.LMS, null));

        // Assert
        assertTrue(actual);
    }

    /**
     * Test code COM32
     */
    @Test
    @DisplayName("test if checking the player's inventory does not have the specific item returns false")
    void testValidateEmptyOne() {
        // Arrange
        CompilerItem compilerItem = CompilerItem.HEALTH_POTION;
        sut.setItem(compilerItem);

        ILiteral literal = new Bool(false);
        sut.setLiteral(literal);

        BoundedValue bounded = new BoundedValue(100, 100, 0);

        Player player = new Player(UUID.randomUUID(), "LOL", null, UUID.randomUUID(), null, bounded, bounded, bounded, "");
        player.addItemToInventory(new Item(UUID.randomUUID(), ItemData.SMALL_HEALTH_POTION));

        // Act
        boolean actual = sut.validate(player, new Game(UUID.randomUUID(), "LOL", GameMode.LMS, null));
        // Assert
        assertFalse(actual);
    }

    /**
     * Test code COM33
     */
    @Test
    @DisplayName("test if checking if the player's inventory has the specific item returns true")
    void testValidateFilledOneTrue() {
        // Arrange
        CompilerItem compilerItem = CompilerItem.HEALTH_POTION;
        sut.setItem(compilerItem);

        ILiteral literal = new Bool(true);
        sut.setLiteral(literal);

        BoundedValue bounded = new BoundedValue(100, 100, 0);

        Player player = new Player(UUID.randomUUID(), "LOL", null, UUID.randomUUID(), null, bounded, bounded, bounded, "");
        player.addItemToInventory(new Item(UUID.randomUUID(), ItemData.SMALL_HEALTH_POTION));

        // Act
        boolean actual = sut.validate(player, new Game(UUID.randomUUID(), "LOL", GameMode.LMS, null));

        // Assert
        assertTrue(actual);
    }

    /**
     * Test code COM34
     */
    @Test
    @DisplayName("test if checking if the player's inventory has two of the specific item returns false")
    void testValidateFilledTwoFalse() {
        // Arrange
        CompilerItem item = CompilerItem.HEALTH_POTION;
        sut.setItem(item);

        ILiteral literal = new Scalar("2");
        sut.setLiteral(literal);

        BoundedValue bounded = new BoundedValue(100, 100, 0);

        Player player = new Player(UUID.randomUUID(), "LOL", null, UUID.randomUUID(), null, bounded, bounded, bounded, "");
        player.addItemToInventory(new Item(UUID.randomUUID(), ItemData.SMALL_HEALTH_POTION));

        // Act
        boolean actual = sut.validate(player, new Game(UUID.randomUUID(), "LOL", GameMode.LMS, null));

        // Assert
        assertFalse(actual);
    }

    /**
     * Test code COM35
     */
    @Test
    @DisplayName("test if checking if the player's inventory has two of the specific item returns false")
    void testValidateFilledTwoTrue() {
        // Arrange
        CompilerItem item = CompilerItem.HEALTH_POTION;
        sut.setItem(item);

        ILiteral literal = new Scalar("1");
        sut.setLiteral(literal);

        BoundedValue bounded = new BoundedValue(100, 100, 0);

        Player player = new Player(UUID.randomUUID(), "LOL", null, UUID.randomUUID(), null, bounded, bounded, bounded, "");
        player.addItemToInventory(new Item(UUID.randomUUID(), ItemData.SMALL_HEALTH_POTION));
        player.addItemToInventory(new Item(UUID.randomUUID(), ItemData.SMALL_HEALTH_POTION));

        // Act
        boolean actual = sut.validate(player, new Game(UUID.randomUUID(), "LOL", GameMode.LMS, null));

        // Assert
        assertTrue(actual);
    }

    /**
     * Test code COM36
     */
    @ParameterizedTest
    @DisplayName("test if children can be retrieved from an existence expression")
    @MethodSource("provider")
    void testGetChildren(IASTNode expected, Getter getter, Setter setter) {
        sut.setLiteral(null);
        testGetChildren(sut, expected, getter, setter);
    }

    /**
     * Test code COM37
     */
    @ParameterizedTest
    @DisplayName("test if children can be added to an existence expression")
    @MethodSource("provider")
    void testAddChild(IASTNode expected, Getter getter, Setter setter) {
        testAddChild(sut, expected, getter, setter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Volume<IASTNode, Getter, Setter> instructions() {
        return instructions;
    }
}
