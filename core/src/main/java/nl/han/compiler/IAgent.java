package nl.han.compiler;

import nl.han.shared.datastructures.creature.Creature;
import nl.han.shared.datastructures.game.Game;
import nl.han.shared.enums.Action;

import java.util.List;

/**
 * This interface should be used by external components to communicate with the compiler and is responsible for
 * determining what actions should be taken based on the current game state and an AST that has previously
 * been compiled.
 */
public interface IAgent {

    /**
     * Determines actions based on the provided creature and chunk it is in for each sentence in the configuration.
     *
     * @param creature The creature for which the actions are to be determined
     * @param game The game the creature is standing in
     * @return A list of actions that the creature must do
     */
    List<Action> determineActions(Creature creature, Game game);
}
