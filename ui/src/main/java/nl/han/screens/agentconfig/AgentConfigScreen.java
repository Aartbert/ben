package nl.han.screens.agentconfig;

import java.util.List;

import com.google.inject.Inject;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;

import nl.han.ButtonMaker;
import nl.han.enums.ProfileType;
import nl.han.interfaces.IButtonClickListener;
import nl.han.interfaces.IProfile;

/**
 * Represents a screen for configuring an agent.
 * This screen contains a grid layout with 6 components, including buttons for different agent profiles.
 * The user can input their agent code in a text box.
 * The screen also provides a save button to save the agent configuration.
 *
 * @author Sem Gerrits, Lars Meijerink
 */
public class AgentConfigScreen extends BasicWindow implements IButtonClickListener{

    private static final int TEXT_BOX_WIDTH = 58;
    private static final int TEXT_BOX_HEIGHT = 35;
    private static final int INSTRUCTIONS_WIDTH = 35;
    private final TextBox leftTop = new TextBox(new TerminalSize(TEXT_BOX_WIDTH, TEXT_BOX_HEIGHT));

    @Inject
    private IProfile profileService;
    @Inject
    private ButtonMaker buttonMaker;

    /**
     * Creates a grid layout with 6 components and puts it on a window.
     *
     * @author Sem Gerrits, Lars Meijerink
     */
    public void createAgentConfigScreen() {
        buttonMaker.addListener(this);

        Panel rightTopPanel = new Panel(new GridLayout(1));
        rightTopPanel.addComponent(new Label("Als hp minder dan 50% is, gebruik dan een health potion. Als hp meer dan 50% is, loop dan rond.").withBorder(Borders.singleLine("Example sentence")).setPreferredSize(new TerminalSize(INSTRUCTIONS_WIDTH, 6)));
        rightTopPanel.addComponent(new Label("'hp minder dan 50%' is the condition in the first example instruction. A condition is a check, which will validate if the action will be executed.").withBorder(Borders.doubleLine("Condition")).setPreferredSize(new TerminalSize(INSTRUCTIONS_WIDTH, 7)));
        rightTopPanel.addComponent(new Label("'gebruik health potion' is the action in the first example instruction. The action that will be executed by the agent whenever the condition is met and the action can be executed.").withBorder(Borders.doubleLine("Action")).setPreferredSize(new TerminalSize(INSTRUCTIONS_WIDTH, 8)));
        rightTopPanel.addComponent(new Label("The actions will be executed in the order of the written instructions. So the first instruction which condition is met, will be executed first. In the example instruction it will first check if your health is below 50%, only if its not below 50% it will look at the second instruction.").withBorder(Borders.doubleLine("Priority!")).setPreferredSize(new TerminalSize(INSTRUCTIONS_WIDTH, 12)));
        rightTopPanel.addComponent(new Label("Must: End a sentence with a dot!!").setPreferredSize(new TerminalSize(INSTRUCTIONS_WIDTH, 1)));
        rightTopPanel.addComponent(new Label("Tip: Use the profile buttons. :)").setPreferredSize(new TerminalSize(INSTRUCTIONS_WIDTH, 1)));

        rightTopPanel.setPreferredSize(new TerminalSize(INSTRUCTIONS_WIDTH, TEXT_BOX_HEIGHT));

        Panel mainPanel = new Panel(new GridLayout(1));
        Panel topPanel = new Panel(new GridLayout(2));
        Panel bottomPanel = new Panel(new GridLayout(5));

        topPanel.addComponent(leftTop.withBorder(Borders.doubleLine("Add your agent code here")));
        topPanel.addComponent(rightTopPanel.withBorder(Borders.doubleLine("Instructions")));

        List<Button> buttons = buttonMaker.createButtons("Defensive", "Neutral", "Offensive", "Agent Info", "Save Agent Config");

        GridLayout gridLayout = new GridLayout(5).setHorizontalSpacing(8);

        bottomPanel.addComponent(buttonMaker.addButtonsToPanel(buttons).setLayoutManager(gridLayout).withBorder(Borders.doubleLine("Profile + save")));

        setComponent(mainPanel.addComponent(topPanel).addComponent(bottomPanel));
    }

    /**
     * Gets the agent code from the text box.
     *
     * @return the agent code from the text box
     * @author Sem Gerrits, Lars Meijerink
     */
    public String getAgentConfig() {
        return leftTop.getText();
    }

    /**
     * Sets the agent code in the text box.
     *
     * @param content the agent code to set in the text box
     * @author Sem Gerrits, Lars Meijerink
     */
    public void setAgentConfig(String content) {
        leftTop.setText(content);
    }

    /**
     * {@inheritDoc}
     *
     * @author Lars Meijerink, Sem Gerrits
     */
    @Override
    public void onButtonClick(String buttonName, String argument) {
        if ("Defensive".equals(buttonName)) {
            leftTop.setText(profileService.loadProfile(ProfileType.DEFENSIVE));
        } else if ("Neutral".equals(buttonName)) {
            leftTop.setText(profileService.loadProfile(ProfileType.NEUTRAL));
        } else if ("Offensive".equals(buttonName)) {
            leftTop.setText(profileService.loadProfile(ProfileType.OFFENSIVE));
        }
    }
}
