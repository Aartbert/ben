package nl.han;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Setter;
import lombok.extern.java.Log;
import nl.han.gamestate.saver.GameDBInit;
import nl.han.gamestate.saver.GameRepository;
import nl.han.shared.datastructures.game.Game;

import java.util.Timer;
import java.util.TimerTask;

@Singleton
@Log
public class GameStateManager {

    private final Timer timer = new Timer();
    private static final int SAVING_INTERVAL = 10000;
    private static final int INITIAL_DELAY = 10000;
    @Inject
    private GameRepository gameRepository;

    @Inject
    private GameDBInit gameDBInit;

    @Setter
    private Game game;

    /**
     * This method initializes the database. This method uses the {@link GameDBInit} to initialize the database.
     */
    public void init() {
        gameDBInit.init();
    }

    /**
     * This method saves the game to the database. This method uses the {@link GameRepository} to save the game.
     */
    public void saveGame() {
        log.info("Saving game...");
        gameRepository.save(game);
    }

    /**
     * This method loads a game from the database. This method uses the {@link GameRepository} to load the game.
     *
     * @param gameId The id of the game that needs to be loaded.
     * @return The loaded game.
     */
    public Game loadGame(String gameId) {
        return gameRepository.load(gameId);
    }

    /**
     * This method starts a timer in a new thread that saves the game every 10 seconds.
     */
    public void startSavingTimer() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                saveGame();
            }
        }, INITIAL_DELAY, SAVING_INTERVAL);
    }

    /**
     * This method stops the timer that saves the game.
     */
    public void stopSavingTimer() {
        timer.cancel();
    }
}
