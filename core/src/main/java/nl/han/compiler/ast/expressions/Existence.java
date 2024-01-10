package nl.han.compiler.ast.expressions;

import lombok.Getter;
import lombok.Setter;
import nl.han.compiler.CompilerException;
import nl.han.compiler.ast.IASTNode;
import nl.han.compiler.ast.enums.CompilerItem;
import nl.han.compiler.ast.literals.Bool;
import nl.han.compiler.ast.literals.ILiteral;
import nl.han.compiler.ast.literals.Scalar;
import nl.han.compiler.ast.operators.Operator;
import nl.han.shared.datastructures.Item;
import nl.han.shared.datastructures.creature.Creature;
import nl.han.shared.datastructures.creature.Player;
import nl.han.shared.datastructures.game.Game;
import nl.han.shared.datastructures.world.Coordinate;
import nl.han.shared.datastructures.world.Tile;
import nl.han.shared.enums.ItemType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * An expression that checks of a certain item is present in the inventory of a creature.
 */
@Getter
@Setter
public class Existence implements IExpression {

    private CompilerItem item;
    private Operator operator;
    private ILiteral literal = new Bool(true);

    /**
     * {@inheritDoc}
     */
    @Override
    public void check() {
        if (!(literal instanceof Scalar) && !(literal instanceof Bool)) throw new CompilerException("Cannot use a literal other than a value or a boolean.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validate(Creature creature, Game game) {
        if (operator == null) return validateInventory(creature);

        return validateRange(creature);
    }

    /**
     * <p>Checks if the {@link Player} has the required {@link #literal amount} of the specified {@link #item item}
     * in its inventory.</p>
     *
     * <p>This will return true when:
     * <ul>
     *     <li>The asked amount is 0, equally to an inventory with not the specified item.</li>
     *     <li>The asked amount is greater than 0, with an inventory equal or greater than the asked amount.</li>
     * </ul>
     * </p>
     *
     * <p>This will return false when:
     * <ul>
     *     <li>The asked amount is 0, not equally to an inventory with the specified item(s).</li>
     *     <li>The asked amount is greater than 0, with an inventory lower than the asked amount.</li>
     *     <li>The {@link Creature creature} is no {@link Player player} and does not have an inventory.</li>
     * </ul>
     * </p>
     *
     * @param creature The {@link Creature creature} to check the inventory with
     * @return A {@link Boolean boolean}
     * @author Tom Gerritsen
     */
    private boolean validateInventory(Creature creature) {
        if (creature instanceof Player player) {
            double expected = convert(literal);

            int actual = 0;

            for (Item item : player.getInventory()) {
                CompilerItem origin = convert(item.getType());

                if (this.item == origin) actual++;
            }

            if (expected == 0) return actual == 0;

            return actual >= expected;
        }

        return false;
    }

    /**
     * <p>Checks if the requested {@link #item item} is within the set {@link #literal range}.</p>
     *
     * <p>This method will return true when <b>one</b> item of the specified {@link #item kind}
     * is found within the {@link #literal range}.</p>
     *
     * @param creature The {@link Creature creature} to center the range
     * @return A boolean if the {@link #item item} is within the range or not
     */
    private boolean validateRange(Creature creature) {
        Scalar expected = new Scalar(shortestDistanceToItem(creature));

        Scalar actual = new Scalar(convert(literal));

        return operator.evaluate(expected, actual);
    }

    /**
     * Calculates the shortest distance from the creature to any item of the specified type
     * within the creature's chunk.
     *
     * @param creature The creature for which to find the shortest distance to an item.
     * @return The shortest distance from the creature to an item of the specified type.
     */
    private double shortestDistanceToItem(Creature creature) {
        double distance = Integer.MAX_VALUE;

        for (Tile[] tiles : creature.getChunk().getTiles()) {
            for (Tile tile : tiles) {
                for (Item found : tile.getItems()) {
                    CompilerItem type = convert(found.getType());

                    if (type == item) {
                        Coordinate point1 = creature.getCoordinate();
                        Coordinate point2 = tile.getCoordinate();

                        double calculateDistance = point1.calculateDistance(point2);

                        if (calculateDistance < distance) {
                            distance = calculateDistance;
                        }
                    }
                }
            }

        }

        return distance;
    }

    /**
     * Method to convert a {@link ILiteral literal} to its {@link Integer integer} representative.
     *
     * @param literal The {@link ILiteral literal} to be converted
     * @return A {@link Integer integer}
     */
    private double convert(ILiteral literal) {
        if (literal instanceof Scalar scalar) return scalar.getValue();
        else if (literal instanceof Bool bool) return bool.convert();
        else return 0;
    }

    /**
     * Convert a {@link ItemType item} to a {@link CompilerItem item} that the compiler understands.
     *
     * @param type The {@link ItemType} to be converted
     * @return The converted {@link CompilerItem item}
     */
    private CompilerItem convert(ItemType type) {
        return switch (type) {
            case HEALTH -> CompilerItem.HEALTH_POTION;
            default -> throw new CompilerException("Unimplemented item type '" + type + "'");
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IASTNode> getChildren() {
        List<IASTNode> children = new ArrayList<>();

        if (item != null) children.add(item);
        if (literal != null) children.add(literal);
        if (operator != null) children.add(operator);

        return children;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASTNode addChild(IASTNode child) {
        if (child instanceof CompilerItem it) item = it;
        else if (child instanceof ILiteral lit) literal = lit;
        else if (child instanceof Operator op) operator = op;
        else IExpression.super.addChild(child);

        return this;
    }

    /**
     * {@inheritDoc}
     * Is equal when the item and literal are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Existence existence = (Existence) o;

        return item == existence.item && Objects.equals(literal, existence.literal);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(item, literal);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Existence";
    }
}
