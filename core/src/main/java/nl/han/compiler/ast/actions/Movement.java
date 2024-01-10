package nl.han.compiler.ast.actions;

import lombok.Getter;
import lombok.Setter;
import nl.han.compiler.ast.IASTNode;
import nl.han.compiler.ast.Sentence;
import nl.han.compiler.ast.enums.Attribute;
import nl.han.compiler.ast.enums.Direction;
import nl.han.compiler.ast.expressions.Comparison;
import nl.han.compiler.ast.literals.Scalar;
import nl.han.compiler.ast.operators.GreaterThanOperator;
import nl.han.shared.enums.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class represents a movement action within an AST.
 */
@Getter
@Setter
public class Movement implements IAction {

    private Direction direction;
    private Scalar scalar = new Scalar(1);

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Action> getAction() {
        Action action = switch (direction) {
            case UP -> Action.MOVE_UP;
            case DOWN -> Action.MOVE_DOWN;
            case LEFT -> Action.MOVE_LEFT;
            case RIGHT -> Action.MOVE_RIGHT;
        };

        List<Action> actions = new ArrayList<>();

        for (int i = 0; i < scalar.getValue(); i++) {
            actions.add(action);
        }

        return actions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void transform(Sentence sentence){
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
        if (direction != null) children.add(direction);
        if (scalar != null) children.add(scalar);

        return children;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASTNode addChild(IASTNode child) {
        if (child instanceof Scalar sca) this.scalar = sca;
        else if (child instanceof Direction dir) this.direction = dir;
        else IAction.super.addChild(child);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(direction);
    }

    /**
     * {@inheritDoc}
     * Is equal when the direction is equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Movement movement = (Movement) o;

        return Objects.equals(direction, movement.direction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Movement";
    }
}
