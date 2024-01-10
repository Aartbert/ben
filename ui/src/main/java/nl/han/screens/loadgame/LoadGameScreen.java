package nl.han.screens.loadgame;

import java.util.List;

import com.google.inject.Inject;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Panel;

import nl.han.ButtonMaker;
import nl.han.Logo;

/**
 * Used to make the load game screen.
 * This is the window that shows the options:
 * Join Game, Create Game, Load Game, Return to Start.
 *
 * @author Vasil Verdouw, Lars Meijerink, Sem Gerrits
 */
public class LoadGameScreen extends BasicWindow {

    @Inject
    private ButtonMaker buttonMaker;

    /**
     * Creates a grid layout with 4 components and puts it on a window.
     *
     * @return the created window
     * @author Vasil Verdouw, Lars Meijerink
     */
    public void createLoadGameScreen() {
        GridLayout gridLayout = new GridLayout(2).setRightMarginSize(1);

        Panel panel = new Panel().setLayoutManager(gridLayout).addComponent(new Logo());
        List<Button> buttons = buttonMaker.createButtons("Join Game", "Create Game", "Load Game", "Return to Start");

        panel.addComponent(buttonMaker.addButtonsToPanel(buttons));
        setComponent(panel);
    }
}
