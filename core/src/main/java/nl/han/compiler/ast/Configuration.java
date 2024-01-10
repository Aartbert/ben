package nl.han.compiler.ast;

import lombok.Getter;
import lombok.Setter;
import nl.han.compiler.IAgent;
import nl.han.shared.datastructures.creature.Creature;
import nl.han.shared.datastructures.game.Game;
import nl.han.shared.enums.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class represents a configuration within an AST and stores the list of sentences that the AST is made up of.
 */
@Getter
@Setter
public class Configuration implements IASTNode, IAgent {

    private List<Sentence> sentences = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Action> determineActions(Creature creature, Game game) {
        for (Sentence sentence : sentences) {
            List<Action> actions = sentence.determineActions(creature, game);

            if (actions != null) {
                return actions;
            }
        }
        return new ArrayList<>();
    }

    /**
     * Transforms the configuration.
     */
    public void transform() {
        getChildren().forEach(this::transform);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void transform(IASTNode current) {
        current.transform(current);

        current.getChildren().forEach(this::transform);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void check() {
        getChildren().forEach(this::check);
    }

    /**
     * Recursively performs a check on the given AST node and its children.
     * This method is called internally during the check process.
     *
     * @param current The AST node to be checked.
     */
    private void check(IASTNode current) {
        current.check();

        current.getChildren().forEach(this::check);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IASTNode> getChildren() {
        return sentences.stream()
                .map(IASTNode.class::cast)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASTNode addChild(IASTNode child) {
        if (child instanceof Sentence sen) sentences.add(sen);
        else IASTNode.super.addChild(child);

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(sentences);
    }

    /**
     * {@inheritDoc}
     * Is equal if the sentences are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Configuration that = (Configuration) o;

        return Objects.equals(sentences, that.sentences);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Configuration");
        builder.append("\n");

        for (IASTNode child : getChildren()) {
            childToString(builder, child, 1);
        }


        return builder.toString();
    }

    /**
     * Recursively builds the toString for each {@link IASTNode node} found in the AST.
     *
     * @param builder The currently used {@link StringBuilder}
     * @param current The current {@link IASTNode node}
     * @param indent The current indentation of the {@link IASTNode node} in tabs
     */
    private void childToString(StringBuilder builder, IASTNode current, int indent) {
        builder.append("\t".repeat(indent));
        builder.append(current);
        builder.append("\n");

        for (IASTNode child : current.getChildren()) {
            childToString(builder, child, indent + 1);
        }
    }
}
