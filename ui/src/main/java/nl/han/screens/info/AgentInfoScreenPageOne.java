package nl.han.screens.info;

import java.util.List;

import com.google.inject.Inject;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;

import nl.han.ButtonMaker;

/**
 * Used to make the agent info screen page one.
 * This is the window that shows the agent info.
 * The user can read the agent info and return to the agent config screen.
 * 
 * @author Sem Gerrits
 */
public class AgentInfoScreenPageOne extends BasicWindow {

    private static final int HORIZONTAL_PANEL_WIDTH = 98;

    @Inject
    private ButtonMaker buttonMaker;

    /** 
     *  Creates a grid layout with 4 components and puts it on a window.
     * 
     * @author Sem Gerrits
     */
    public void createAgentInfoScreenPageOne() {
        Panel headerPanel = new Panel(new GridLayout(2));
        headerPanel.addComponent(new Label("Page 1").withBorder(Borders.doubleLineReverseBevel()).setPreferredSize(new TerminalSize(8, 3)));
        headerPanel.addComponent(new Label("Agent instructions information").withBorder(Borders.singleLineReverseBevel()).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH - 9, 3)));

        Panel actionPanel = new Panel(new GridLayout(1));
        actionPanel.addComponent(new Label("'loop [direction].', 'beweeg [direction]' -> The player moves in the given direction\nFor [direction] use one of the directions from the 'Directions' box").withBorder(Borders.singleLine("Move")).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH, 4)));
        actionPanel.addComponent(new Label("'loop rond.', 'dwaal', 'zwerf' -> The player wanders around").withBorder(Borders.singleLine("Wander")).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH, 3)));
        actionPanel.addComponent(new Label("'ren weg.', 'wegrennen' -> The player runs away from a creature").withBorder(Borders.singleLine("Retreat")).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH, 3)));
        actionPanel.addComponent(new Label("'val [creature] aan.', '[creature] aanvallen' -> The player attacks the given creature\nfor [creature] use one of the creatures given in the 'Creatures' box").withBorder(Borders.singleLine("Attack")).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH, 3)));
        actionPanel.addComponent(new Label("'pak [item] op.', '[item] oprapen' -> The player picks up an item,'\nFor [item] use one of the items given in the 'Items' box").withBorder(Borders.singleLine("Pick up")).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH, 4)));
        actionPanel.addComponent(new Label("'gebruik [item]' -> The player uses an item\nFor [item] use one of the items given in the 'Items' box").withBorder(Borders.singleLine("Use item")).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH, 4)));
        Panel firstHorizontalPanel = new Panel(new GridLayout(1));
        firstHorizontalPanel.addComponent(actionPanel.withBorder(Borders.doubleLine("Actions")).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH, 23)));

        Panel creaturePanel = new Panel(new GridLayout(1));
        creaturePanel.addComponent(new Label("'speler' -> A player").withBorder(Borders.singleLine("Player")).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH, 3)));
        creaturePanel.addComponent(new Label("'monster' -> A monster").withBorder(Borders.singleLine("Monster")).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH, 3)));
        creaturePanel.addComponent(new Label("'vijand' -> An enemy").withBorder(Borders.singleLine("Enemy")).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH, 3)));
        Panel secondHorizontalPanel = new Panel(new GridLayout(1));
        secondHorizontalPanel.addComponent(creaturePanel.withBorder(Borders.doubleLine("Creatures")).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH, 11)));

        Panel bottomPanel = new Panel(new GridLayout(1));
        List<Button> buttons = buttonMaker.createButtons("Page 1", "Page 2", "Page 3", "Return to Agent Config");
        GridLayout gridLayout = new GridLayout(4).setHorizontalSpacing(15);
        bottomPanel.addComponent(buttonMaker.addButtonsToPanel(buttons).setLayoutManager(gridLayout).withBorder(Borders.doubleLine()).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH, 3)));

        Panel mainPanel = new Panel(new GridLayout(1));
        setComponent(mainPanel.addComponent(headerPanel).addComponent(firstHorizontalPanel).addComponent(secondHorizontalPanel).addComponent(bottomPanel));
    }

}
