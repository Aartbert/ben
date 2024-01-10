package nl.han;

import java.io.IOException;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.bundle.LanternaThemes;
import com.googlecode.lanterna.gui2.DefaultWindowManager;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import lombok.extern.java.Log;

/**
 * The screenmaker class is supposed to make a terminal screen.
 * The terminal screen is the actual window that is displayed.
 * 
 * @author Vasil Verdouw
 */
@Log
public class ScreenMaker {
    private static final int WINDOW_WIDTH = 140;
    private static final int WINDOW_HEIGHT = 60;

    /**
     * Creates a TerminalScreen and returns it.
     *
     * @return the created screen
     * @author Vasil Verdouw
     */
    public TerminalScreen createScreen() {
        try {
            TerminalSize terminalSize = new TerminalSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            // Sonarlint comment: Add try and catch to close the screen or close it manually
            // DON'T DO THIS. It will immediately close the screen and entire terminal
            // application after running this code. You should call the closeScreen method
            // after you are done with the screen.
            TerminalScreen terminalScreen = new DefaultTerminalFactory() // NOSONAR (see comment above)
                    .setInitialTerminalSize(terminalSize)
                    .createScreen();
            terminalScreen.setCursorPosition(null);
            terminalScreen.startScreen();
            terminalScreen.refresh();
            return terminalScreen;
        } catch (IOException e) {
            log.log(java.util.logging.Level.SEVERE, "Screen not created", e);
            return null;
        }
    }

    /**
     * Creates a multi window text GUI and returns it.
     * multi window text gui has windows, a window will be "gamescreen" or
     * "startscreen"
     * 
     * @param terminalScreen the screen to create the multi window text gui for
     * @return the created multi window text gui
     * @author Vasil Verdouw
     */
    public MultiWindowTextGUI createMultiWindowTextGUI(TerminalScreen terminalScreen) {
        MultiWindowTextGUI multiWindowTextGUI = new MultiWindowTextGUI(terminalScreen, new DefaultWindowManager(),
                new EmptySpace(TextColor.ANSI.BLACK));
        multiWindowTextGUI.setTheme(LanternaThemes.getRegisteredTheme("businessmachine"));
        return multiWindowTextGUI;
    }

}
