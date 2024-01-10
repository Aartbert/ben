package nl.han;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;
import nl.han.compiler.CompilerException;
import nl.han.compiler.IAgent;
import nl.han.compiler.ICompiler;
import nl.han.shared.datastructures.creature.Creature;
import nl.han.shared.datastructures.game.Game;
import nl.han.shared.enums.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is a management class for handling compilation related actions within the project. Its main functionality includes compiling player inputs into a list of
 * actions, as well as several conversion methods for aligning shared data structures with project specific equivalents.<br/>
 * Note: The conversion methods are temporary and will be removed when the compiler is fully utilised and in sync with shared folders.
 *
 * @author Jochem Kalsbeek
 * @see Logger
 * @see ICompiler
 * @see Level
 * @see CompilerException
 */
@Log
@Setter
@Singleton
@RequiredArgsConstructor
public class CompilerManager {

    @Inject
    private ICompiler compiler;

    public List<Action> compileInput(String input, Creature creature, Game game) {
        try {
            IAgent agent = compiler.compileHandler(input);

            return agent.determineActions(creature, game);
        } catch (CompilerException e) {
            //TODO foutmelding tonen op het scherm en de game laten doorgaan
            log.log(Level.SEVERE, e.getMessage(), e);
        }

        return new ArrayList<>();
    }
}