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
 * Used to make the agent info screen page three.
 * This is the window that shows the agent info.
 * The user can read the agent info and return to the agent config screen.
 * 
 * @author Sem Gerrits
 */
public class AgentInfoScreenPageThree extends BasicWindow {

    private static final int HORIZONTAL_PANEL_WIDTH = 98;

    @Inject
    private ButtonMaker buttonMaker;

    /**
     * Creates a grid layout with 4 components and puts it on a window.
     *
     * @author Sem Gerrits
     */
    public void createAgentInfoScreenPageThree() {
        Panel headerPanel = new Panel(new GridLayout(2));
        headerPanel.addComponent(new Label("Page 3").withBorder(Borders.singleLineReverseBevel()).setPreferredSize(new TerminalSize(8, 3)));
        headerPanel.addComponent(new Label("Agent instructions information").withBorder(Borders.doubleLineReverseBevel()).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH - 9, 3)));

        Panel operationPanel = new Panel(new GridLayout(1));
        operationPanel.addComponent(new Label("'of' -> Use this 'or' operator when you want one of the conditions to be true").withBorder(Borders.singleLine("Or")).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH, 3)));
        operationPanel.addComponent(new Label("'en' -> Use this 'and' operator when you want multiple conditions to be true or multiple actions to happen").withBorder(Borders.singleLine("And")).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH, 3)));
        operationPanel.addComponent(new Label("'groter dan' -> Use this 'greater than' operator when you want to check if an attribute is greater than a value").withBorder(Borders.singleLine("Greater than")).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH, 4)));
        operationPanel.addComponent(new Label("'kleiner dan' -> Use this 'less than' operator when you want to check if an attribute is less than a value").withBorder(Borders.singleLine("Less than")).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH, 4)));
        operationPanel.addComponent(new Label("'is' -> Use this 'equal to' operator when you want to check if an attribute is equal to a value").withBorder(Borders.singleLine("Equal to")).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH, 4)));
        Panel firstHorizontalPanel = new Panel(new GridLayout(1));
        firstHorizontalPanel.addComponent(operationPanel.withBorder(Borders.doubleLine("Operations")).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH, 20)));

        Panel literalPanel = new Panel(new GridLayout(1));
        literalPanel.addComponent(new Label("'[any number]' -> Use a number to create specific conditions").withBorder(Borders.singleLine("Scalar")).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH, 3)));
        literalPanel.addComponent(new Label("'%', 'procent' -> Use a percentage sign combined with a scalar to create specific conditions").withBorder(Borders.singleLine("Percentage")).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH, 3)));
        literalPanel.addComponent(new Label("'waar', 'wel' -> Use this 'true' literal when you want to check if something is true").withBorder(Borders.singleLine("True")).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH, 3)));
        literalPanel.addComponent(new Label("'niet waar', 'geen' -> Use this 'false' literal when you want to check if something is false").withBorder(Borders.singleLine("False")).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH, 3)));
        Panel secondHorizontalPanel = new Panel(new GridLayout(1));
        secondHorizontalPanel.addComponent(literalPanel.withBorder(Borders.doubleLine("Literals")).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH, 14)));

        Panel mainPanel = new Panel(new GridLayout(1));
        Panel bottomPanel = new Panel(new GridLayout(1));

        List<Button> buttons = buttonMaker.createButtons("Page 1", "Page 2", "Page 3", "Return to Agent Config");

        GridLayout gridLayout = new GridLayout(4).setHorizontalSpacing(15);

        bottomPanel.addComponent(buttonMaker.addButtonsToPanel(buttons).setLayoutManager(gridLayout).withBorder(Borders.doubleLine()).setPreferredSize(new TerminalSize(HORIZONTAL_PANEL_WIDTH, 3)));

        setComponent(mainPanel.addComponent(headerPanel).addComponent(firstHorizontalPanel).addComponent(secondHorizontalPanel).addComponent(bottomPanel));
    }

}
