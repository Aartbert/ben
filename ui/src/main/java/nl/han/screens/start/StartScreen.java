package nl.han.screens.start;

import com.google.inject.Inject;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Panel;
import java.util.List;

import nl.han.ButtonMaker;
import nl.han.Logo;

/**
 * The start screen displays the options:
 * Play, World config, Agent config, Exit.
 * It also contains the game logo.
 *
 * @author Vasil Verdouw, Lars Meijerink, Sem Gerrits
 */
public class StartScreen extends BasicWindow {

    @Inject
    private ButtonMaker buttonMaker;

    /**
     * Creates a grid layout with 4 components and puts it on a window.
     *
     * @author Vasil Verdouw, Lars Meijerink
     */
    public void createStartScreen() {
        GridLayout gridLayout = new GridLayout(2).setRightMarginSize(2);

        Panel panel = new Panel().setLayoutManager(gridLayout).addComponent(new Logo());
        List<Button> buttons = buttonMaker.createButtons("Play", "Agent config", "Monster config", "Exit");

        panel.addComponent(buttonMaker.addButtonsToPanel(buttons));
        setComponent(panel);
    }
}
