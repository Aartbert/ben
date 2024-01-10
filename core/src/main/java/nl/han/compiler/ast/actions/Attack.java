package nl.han.compiler.ast.actions;

import lombok.Getter;
import lombok.Setter;
import nl.han.compiler.CompilerException;
import nl.han.compiler.ast.IASTNode;
import nl.han.compiler.ast.Sentence;
import nl.han.compiler.ast.enums.Attribute;
import nl.han.compiler.ast.expressions.Comparison;
import nl.han.compiler.ast.literals.Scalar;
import nl.han.compiler.ast.operators.GreaterThanOperator;
import nl.han.shared.enums.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class represents an attack action within an AST.
 */
@Getter
@Setter
public class Attack implements IAction {

    private Attribute creatureType;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Action> getAction() {
        return switch (creatureType) {
            case PLAYER -> List.of(Action.SEARCH_PLAYER);
            case MONSTER -> List.of(Action.SEARCH_MONSTER);
            case ENEMY -> List.of(Action.SEARCH_ENEMY);
            default -> throw new CompilerException("Unexpected value: " + creatureType);
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void transform(Sentence sentence) {
        IASTNode condition = new Comparison()
                .addChild(Attribute.STAMINA)
                .addChild(new GreaterThanOperator())
                .addChild(new Scalar(0));

        sentence.addChild(condition);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IASTNode> getChildren() {
        List<IASTNode> children = new ArrayList<>();

        if (creatureType != null) children.add(creatureType);

        return children;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASTNode addChild(IASTNode child) {
        if (child instanceof Attribute atr) creatureType = atr;
        else IAction.super.addChild(child);

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(creatureType);
    }

    /**
     * {@inheritDoc}
     * Is equal when the creatureType is equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Attack attack = (Attack) o;

        return Objects.equals(creatureType, attack.creatureType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Attack";
    }
}
