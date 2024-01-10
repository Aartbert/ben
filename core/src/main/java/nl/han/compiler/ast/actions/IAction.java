package nl.han.compiler.ast.actions;

import nl.han.compiler.ast.IASTNode;
import nl.han.compiler.ast.Sentence;
import nl.han.shared.enums.Action;

import java.util.List;

/**
 * This interface represents an action within an AST and should be implemented by all possible actions. This
 * interface requires actions to be able to translate themselves to a String value.
 */
public interface IAction extends IASTNode {

    /**
     * Translates the action variables to a list of actions based on an enum.
     * @return A list of actions.
     */
    List<Action> getAction();

    /**
     * Implement this method to add an extra condition to the action.
     * @param sentence is the sentence where the transformation happens.
     */
    default void transform(Sentence sentence) { }
}
