package nl.han.compiler.ast.actions;

import lombok.Getter;
import lombok.Setter;
import nl.han.compiler.ast.IASTNode;
import nl.han.compiler.ast.Sentence;
import nl.han.compiler.ast.enums.CompilerItem;
import nl.han.compiler.ast.expressions.Existence;
import nl.han.shared.enums.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class that represents the usage of an item in the AST.
 */
@Getter
@Setter
public class Use implements IAction {

    private CompilerItem item;
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Action> getAction() {
        return switch (item) {
            case HEALTH_POTION -> List.of(Action.USE_HEALTH_POTION);
            default -> throw new IllegalStateException("Illegal action");
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void transform(Sentence sentence){
        IASTNode condition = new Existence()
                .addChild(item);

        sentence.addChild(condition);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IASTNode> getChildren() {
        List<IASTNode> children = new ArrayList<>();

        if (item != null) children.add(item);

        return children;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASTNode addChild(IASTNode child) {
        if (child instanceof CompilerItem it) item = it;
        else IAction.super.addChild(child);

        return this;
    }

    /**
     * {@inheritDoc}
     * Is equal when the item is equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Use use = (Use) o;
        return item == use.item;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Use";
    }
}
