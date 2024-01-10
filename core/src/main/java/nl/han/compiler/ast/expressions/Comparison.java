package nl.han.compiler.ast.expressions;

import lombok.Getter;
import lombok.Setter;
import nl.han.compiler.CompilerException;
import nl.han.compiler.ast.IASTNode;
import nl.han.compiler.ast.enums.Attribute;
import nl.han.compiler.ast.literals.ILiteral;
import nl.han.compiler.ast.literals.Percentage;
import nl.han.compiler.ast.literals.Scalar;
import nl.han.compiler.ast.operators.Operator;
import nl.han.shared.datastructures.BoundedValue;
import nl.han.shared.datastructures.creature.Bot;
import nl.han.shared.datastructures.creature.Creature;
import nl.han.shared.datastructures.creature.Player;
import nl.han.shared.datastructures.game.Game;
import nl.han.shared.datastructures.game.Team;
import nl.han.shared.enums.GameMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class represents a comparison between an attribute and a value within an AST.
 */
@Getter
@Setter
public class Comparison implements IExpression {

    private Attribute attribute;
    private Operator operator;
    private ILiteral value;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validate(Creature creature, Game game) {
        ILiteral lhs = switch (attribute) {
            case HEALTH -> convert(creature.getHealth());
            case STAMINA -> convert(creature.getStamina());
            case POWER -> convert(creature.getPower());
            case PLAYER, MONSTER, ENEMY -> validateDistance(creature, game);
        };

        ILiteral rhs = this.value;

        return operator.evaluate(lhs, rhs);
    }

    /**
     * Filter all Players out
     *
     * @param creature all the creatures in the chuck.
     * @param game The game.
     *
     * @return returns scalar value of the closest distance to an enemy.
     */
    private Scalar validateDistance(Creature creature, Game game) {

        // Filter the current player out of all creatures
        List<Creature> filtered = filterCurrentCreature(game.getCreatures(), creature);

        // Filter out the players that are in the player's team
        filtered = filterTeam(creature, game);

        // Filter out the creatures in the current chunk
        filtered = filterChunk(creature, filtered);

        // Filter out for selected attribute
        filtered = switch (attribute) {
            case PLAYER -> filterCreaturesForPlayers(filtered);
            case MONSTER -> filterCreaturesForMonsters(filtered);
            default -> filtered;
        };

        // Calculate distance
        Double value = filtered.stream()
                .map(creature::getDistance)
                .min(Double::compare)
                .orElse(Double.MAX_VALUE);

        return new Scalar(value);
    }

    private List<Creature> filterCurrentCreature(List<Creature> creatures, Creature current) {
        return creatures
                .stream()
                .filter(another -> !current.equals(another))
                .toList();
    }

    /**
     * Filter all Players out
     *
     * @param creatures all the creatures in the chuck.
     * @return returns a list of players that are the enemy of the current player.
     */
    private List<Creature> filterCreaturesForPlayers(List<Creature> creatures) {
        return creatures.stream()
                .filter(creature -> creature instanceof Player)
                .toList();
    }

    /**
     * Filter all monsters out
     *
     * @param creatures all the creatures in the chuck.
     * @return returns a list of monsters that are the enemy of the current player.
     */
    private List<Creature> filterCreaturesForMonsters(List<Creature> creatures) {
        return creatures.stream()
                .filter(creature -> creature instanceof Bot)
                .toList();
    }

    /**
     * Filter all players that are not in the team of the current player.
     *
     * @param game The game where we get the team of the current player.
     * @param creature The current player.
     * @return returns a list of creatures that are the enemy of the current player.
     */
    private List<Creature> filterTeam(Creature creature, Game game) {
        if (game.getGameMode() != GameMode.CTF || !(creature instanceof Player)) {
            return game.getCreatures();
        }

        Team origin = game.getTeam((Player) creature);

        return game.getPlayers().stream()
                .filter(p -> game.getTeam(p) != origin)
                .map(Creature.class::cast)
                .toList();
    }

    /**
     * Filter all creatures that are in the current chunk of the creature.
     *
     * @param creature The creature for which to retrieve all other creatures in the same chunk.
     * @param creatures The creatures that are part of the game.
     * @return The list of {@link Creature}
     */
    private List<Creature> filterChunk(Creature creature, List<Creature> creatures) {
        return creatures.stream()
                .filter(p -> p.getChunk().equals(creature.getChunk()))
                .toList();
    }

    /**
     * Converts a {@link BoundedValue value} to a {@link ILiteral literal}.
     *
     * @param bounded The value of the game state
     * @return A literal that holds the same value in a recognized form
     * @throws CompilerException when the attribute that was provided is of a type that is not recognized,
     *                           or the name that is stored in the condition that this method is called upon is of a type that is not recognized.
     */
    private ILiteral convert(BoundedValue bounded) {
        if (value instanceof Scalar) return new Scalar(bounded.getIntValue());
        else if (value instanceof Percentage) return new Percentage((int) (bounded.convertToPercentage() * 100));
        else throw new CompilerException("Cannot convert to '" + value.getClass().getSimpleName() + "'.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void check() {
        operator.check(attribute, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IASTNode> getChildren() {
        List<IASTNode> children = new ArrayList<>();

        if (attribute != null) children.add(attribute);
        if (operator != null) children.add(operator);
        if (value != null) children.add(value);

        return children;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASTNode addChild(IASTNode child) {
        if (child instanceof Attribute atr) attribute = atr;
        else if (child instanceof Operator ope) operator = ope;
        else if (child instanceof ILiteral val) value = val;
        else IExpression.super.addChild(child);

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(attribute, operator, value);
    }

    /**
     * {@inheritDoc}
     * Is equal when the attribute, operator and value are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comparison that = (Comparison) o;

        return attribute == that.attribute && Objects.equals(operator, that.operator) && Objects.equals(value, that.value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Comparison";
    }
}
