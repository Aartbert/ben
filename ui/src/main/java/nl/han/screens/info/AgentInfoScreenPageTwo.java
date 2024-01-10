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
 * Used to make the agent info screen page two.
 * This is the window that shows the agent info.
 * The user can read the agent info and return to the agent config screen.
 * 
 * @author Sem Gerrits
 */
public class AgentInfoScreenPageTwo extends BasicWindow {

    private static final int HORIZONTAL_PANEL_WIDTH = 98;

    @Inject
    private ButtonMaker buttonMaker;

    /**
     * Creates a grid layout with 4 components and puts it on a window.
     *
     * @author Sem Gerrits
     */
    public void createAgentInfoScreenPageTwo() {
        Panel headerPanel = new Panel(new GridLayout(2));
        headerPanel.addComponent(new Label("Page 2").withBorder(Borders.doubleLineReverseBevel()).setPreferredSize(new TerminalSize(8, 3)));
        headerPanel.addComponent(new Label("Agent instructions information").withBorder(Borders.singleLineReverseBevel()).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH - 9, 3)));
        
        Panel itemPanel = new Panel(new GridLayout(1));
        itemPanel.addComponent(new Label("'health potion' -> Healing potion to restore health").withBorder(Borders.singleLine("Health")).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH, 3)));
        Panel firstHorizontalPanel = new Panel(new GridLayout(1));
        firstHorizontalPanel.addComponent(itemPanel.withBorder(Borders.doubleLine("Items")).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH, 6)));

        Panel attributePanel = new Panel(new GridLayout(1));
        attributePanel.addComponent(new Label("'levens', 'hp' -> Health of the player").withBorder(Borders.singleLine("Health")).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH, 3)));
        attributePanel.addComponent(new Label("'energie', 'stamina' -> Stamina of the player").withBorder(Borders.singleLine("Stamina")).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH, 3)));
        attributePanel.addComponent(new Label("'kracht', 'strength' -> Strength of the player").withBorder(Borders.singleLine("Strength")).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH, 3)));

        Panel secondHorizontalPanel = new Panel(new GridLayout(1));
        secondHorizontalPanel.addComponent(attributePanel.withBorder(Borders.doubleLine("Attributes")).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH, 12)));

        Panel directionPanel = new Panel(new GridLayout(1));
        directionPanel.addComponent(new Label("'noord', 'omhoog' 'boven' -> North").withBorder(Borders.singleLine("North")).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH, 3)));
        directionPanel.addComponent(new Label("'oost', 'rechts' -> East").withBorder(Borders.singleLine("East")).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH, 3)));
        directionPanel.addComponent(new Label("'zuid', 'omlaag', 'beneden' -> South").withBorder(Borders.singleLine("South")).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH, 3)));
        directionPanel.addComponent(new Label("'west', 'links' -> West").withBorder(Borders.singleLine("West")).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH, 3)));
        Panel thirdHorizontalPanel = new Panel(new GridLayout(1));
        thirdHorizontalPanel.addComponent(directionPanel.withBorder(Borders.doubleLine("Directions")).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH, 16)));

        Panel mainPanel = new Panel(new GridLayout(1));
        Panel bottomPanel = new Panel(new GridLayout(1));

        List<Button> buttons = buttonMaker.createButtons("Page 1", "Page 2", "Page 3", "Return to Agent Config");

        GridLayout gridLayout = new GridLayout(4).setHorizontalSpacing(15);

        bottomPanel.addComponent(buttonMaker.addButtonsToPanel(buttons).setLayoutManager(gridLayout).withBorder(Borders.doubleLine()).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH, 3)));

        setComponent(mainPanel.addComponent(headerPanel).addComponent(firstHorizontalPanel).addComponent(secondHorizontalPanel).addComponent(thirdHorizontalPanel).addComponent(bottomPanel));
    }

}
