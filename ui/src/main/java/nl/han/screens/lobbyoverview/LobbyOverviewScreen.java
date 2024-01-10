package nl.han.screens.lobbyoverview;

import com.google.inject.Inject;
import com.googlecode.lanterna.gui2.*;
import nl.han.ButtonMaker;

import java.util.List;
import java.util.ArrayList;



/**
 * This screen displays all the lobbies that are available to join.
 * It also has a button to return to the start screen.
 *
 * @author Vasil Verdouw, Lars Meijerink, Sem Gerrits
 */
public class LobbyOverviewScreen extends BasicWindow {
    Panel lobbyPanel = new Panel();
    List<String> lobbies = new ArrayList<>();


    @Inject
    private ButtonMaker buttonMaker;

    /**
     * Creates a grid layout with 4 components and puts it on a window.
     *
     * @author Vasil Verdouw, Lars Meijerink
     */
    public void createLobbyScreen() {
        GridLayout gridLayout = new GridLayout(1);
        Panel panel = new Panel().setLayoutManager(gridLayout);

        Button returnButton = buttonMaker.createButton("Return to Start", "");

        panel.addComponent(lobbyPanel.setLayoutManager(new GridLayout(2)));
        panel.addComponent(buttonMaker.addButtonsToPanel(returnButton));

        setComponent(panel);
    }

    public void addLobbies(List<String> names) {
        lobbies.addAll(names);
    }

    public void addLobby(String name) {lobbies.add(name);}

    /**
     * Displays all the lobbies in the lobby list.
     *
     * @author Vasil Verdouw
     */
    public void updateLobbies(ButtonMaker buttonMaker) {
        lobbyPanel.removeAllComponents();
        for (String lobby : lobbies) {
            lobbyPanel.addComponent(new Label("Name: " + lobby));
            lobbyPanel.addComponent(buttonMaker.createButton("Join Lobby", lobby));
        }
    }
}
