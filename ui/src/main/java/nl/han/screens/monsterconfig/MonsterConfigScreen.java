package nl.han.screens.monsterconfig;

import com.google.inject.Inject;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import nl.han.ButtonMaker;

import java.util.List;

public class MonsterConfigScreen extends BasicWindow {
    private static final int TEXT_BOX_WIDTH = 58;
    private static final int TEXT_BOX_HEIGHT = 35;
    private static final int INSTRUCTIONS_WIDTH = 35;
    private final TextBox leftTop = new TextBox(new TerminalSize(TEXT_BOX_WIDTH, TEXT_BOX_HEIGHT));

    @Inject
    private ButtonMaker buttonMaker;

    /**
     * Creates a grid layout with 6 components and puts it on a window.
     *
     * @author Laurens van Brecht
     */
    public void createMonsterConfigScreen() {

        Panel rightTopPanel = new Panel(new GridLayout(1));
        rightTopPanel.addComponent(new Label("'Als monster 4 vakjes in de buurt is van een speler, val aan.'" ).withBorder(Borders.singleLine("Example sentence")).setPreferredSize(new TerminalSize(INSTRUCTIONS_WIDTH, 6)));
        rightTopPanel.addComponent(new Label("'Als monster 4 vakjes in de buurt is van een speler' is the condition in the first example instruction. A condition is a check, which will validate if the action will be executed.").withBorder(Borders.doubleLine("Condition")).setPreferredSize(new TerminalSize(INSTRUCTIONS_WIDTH, 7)));
        rightTopPanel.addComponent(new Label("'val aan' is the action in the first example instruction. The action that will be executed by the agent whenever the condition is met and the action can be executed.").withBorder(Borders.doubleLine("Action")).setPreferredSize(new TerminalSize(INSTRUCTIONS_WIDTH, 8)));
        rightTopPanel.addComponent(new Label("The actions will be executed in the order of the written instructions. So the first instruction which condition is met, will be executed first. In the example instruction it will first check if your monster is 4 tiles with player reach, only if its not within 4 tiles it will look at the second instruction.").withBorder(Borders.doubleLine("Priority!")).setPreferredSize(new TerminalSize(INSTRUCTIONS_WIDTH, 12)));

        rightTopPanel.setPreferredSize(new TerminalSize(INSTRUCTIONS_WIDTH, TEXT_BOX_HEIGHT));

        Panel mainPanel = new Panel(new GridLayout(1));
        Panel topPanel = new Panel(new GridLayout(2));
        Panel bottomPanel = new Panel(new GridLayout(5));

        topPanel.addComponent(leftTop.withBorder(Borders.doubleLine("Add your monster code here")));
        topPanel.addComponent(rightTopPanel.withBorder(Borders.doubleLine("Instructions")));

        List<Button> buttons = buttonMaker.createButtons("Defensive", "Neutral", "Offensive", "Save Monster Config");

        GridLayout gridLayout = new GridLayout(5).setHorizontalSpacing(14);

        bottomPanel.addComponent(buttonMaker.addButtonsToPanel(buttons).setLayoutManager(gridLayout).withBorder(Borders.doubleLine("Profile + save")));

        setComponent(mainPanel.addComponent(topPanel).addComponent(bottomPanel));
    }

    /**
     * Gets the monster config from the text box.
     *
     * @return the monster config from the text box
     * @author Laurens van Brecht
     */
    public String getMonsterConfig() {
        return leftTop.getText();
    }

    /**
     * Sets the Monster Config from the text box.
     *
     * @param content Monster configuration.
     * @author Laurens van Brecht
     */
    public void setMonsterConfig(String content) {
        leftTop.setText(content);
    }

}
