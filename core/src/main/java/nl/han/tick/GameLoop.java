package nl.han.tick;

import java.util.Timer;
import java.util.TimerTask;

/**
 * The GameLoop class is the key engine driving the gameplay.
 * It implements an abstract game loop mechanism that updates at a fixed rate
 * dictated by the TPS (ticks per second).
 * It ensures that the game runs smoothly irrespective of variations in the
 * system clock speed.
 *
 * @author Vasil Verdouw
 */
public abstract class GameLoop {
    private static final double TPS = 10;
    private static final double TIME_BETWEEN_UPDATES = 1000 / TPS;
    private final Timer timer = new Timer();
    private boolean keepRunning = false;

    public boolean isRunning() {
        return keepRunning;
    }

    /**
     * This method automatically sets up and starts the game loop
     *
     * @author Vasil Verdouw
     */
    public void start() {
        setup();
        keepRunning = true;
        run();
    }

    /**
     * This method implements the core loop that keeps the game running.
     * It maintains a smooth flow of updates or ticks based on the TPS (ticks per
     * second) to ensure a steady game pace.
     *
     * @author Djurre Tieman en Vasil Verdouw
     */
    private void run() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (keepRunning) {
                    tick();
                    return;
                }
                shutdown();
                timer.cancel();
            }
        }, 0, (int) TIME_BETWEEN_UPDATES);
    }

    /**
     * This method stops the game by setting the keepRunning variable to false.
     *
     * @author Vasil Verdouw
     */
    public void stop() {
        keepRunning = false;
    }

    /**
     * This abstract method is to be implemented in any class extending GameLoop.
     * The setup() method is expected to contain all initialization processes
     * required before the game loop runs.
     *
     * @author Vasil Verdouw
     */
    protected abstract void setup();

    /**
     * This abstract method is to be implemented in any class extending GameLoop.
     * The tick() method is expected to contain all game update processes to be
     * executed on each tick of the game loop.
     *
     * @author Vasil Verdouw
     */
    protected abstract void tick();

    /**
     * This method is to be implemented in any class extending GameLoop with
     * shutdown functionality.
     * The shutdown() method is expected to contain all shutdown processes to be
     * executed when the game loop stops.
     *
     * @author Vasil Verdouw
     */
    protected abstract void shutdown();
}