package nl.han.compiler.ast.actions;

import nl.han.shared.enums.Action;

import java.util.List;

/**
 * This class represents a pickup action in the AST.
 */
public class PickUp implements IAction {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Action> getAction() {
        return List.of(Action.SEARCH_ITEM);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * {@inheritDoc}
     * Is equal when the class is assignable from the other class.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PickUp pickUp = (PickUp) o;

        return getClass().isAssignableFrom(pickUp.getClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "PickUp";
    }
}
