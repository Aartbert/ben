
package nl.han;

import com.google.inject.Singleton;
import nl.han.interfaces.IButtonClickListener;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Panel;

/**
 * Used to make buttons and add them to a panel.
 * It also notifies all listeners when a button is clicked. Make sure to use the
 * same ButtonMaker for all buttons that use the same listener.
 * 
 * @author Vasil Verdouw
 */
@Singleton
public class ButtonMaker {
    private final List<IButtonClickListener> listeners = new ArrayList<>();

    /**
     * Creates a list of buttons with the given names
     * 
     * @param names
     * @return the list of buttons
     * @author Vasil Verdouw
     */
    public List<Button> createButtons(String... names) {
        List<Button> buttons = new ArrayList<>();
        for (String name : names) {
            buttons.add(new Button(name, () -> notifyListeners(name, "")));
        }
        return buttons;
    }

    /**
     * Creates a button with the given name
     *
     * @param name
     * @return the button with the given name
     * @author Vasil Verdouw
     */
    public Button createButton(String name, String argument) {
        return new Button(name, () -> notifyListeners(name, argument));
    }

    /**
     * Adds all buttons to a panel
     *
     * @param buttons the buttons to add
     * @return the buttonPanel with the buttons added to it
     * @author Vasil Verdouw, Lars Meijerink
     */
    public Panel addButtonsToPanel(List<Button> buttons) {
        Panel panel = new Panel();
        GridLayout gridLayout = new GridLayout(1).setVerticalSpacing(7).setTopMarginSize(5);

        panel.setLayoutManager(gridLayout);
        for (Button button : buttons) {
            panel.addComponent(button);
        }
        return panel;
    }

    /**
     * Adds a button to a panel
     *
     * @param button the button to add
     * @return the buttonPanel with the button added to it
     * @author Vasil Verdouw
     */
    public Panel addButtonsToPanel(Button button) {
        return addButtonsToPanel(List.of(button));
    }

    /**
     * Notifies all listeners that a button has been clicked
     *
     * @param name the name of the button that has been clicked
     * @author Lars Meijerink
     */
    public void notifyListeners(String name, String argument) {
        for (IButtonClickListener listener : listeners) {
            listener.onButtonClick(name, argument);
        }
    }

    /**
     * Adds a listener to the button maker
     *
     * @param listener the listener to add
     * @author Lars Meijerink
     */
    public void addListener(IButtonClickListener listener) {
        listeners.add(listener);
    }
}
