package nl.han.screens.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import lombok.Getter;
import lombok.Setter;
import nl.han.interfaces.ISubmitListener;

/**
 * A text box that allows the user to submit the entered text.
 * When the user presses the Enter key, all registered listeners will be
 * notified with the entered text.
 * 
 * @author Vasil Verdouw
 */
@Getter
@Setter
public class SubmittableTextBox extends TextBox {
    private List<ISubmitListener> submitListeners = new ArrayList<>();

    /**
     * A TextBox that can be submitted.
     * 
     * @author Vasil Verdouw
     */
    public SubmittableTextBox(TerminalSize terminalSize) {
        super(terminalSize);
    }

    /**
     * {@inheritDoc}
     * 
     * @author Vasil Verdouw
     */
    @Override
    public synchronized Result handleKeyStroke(KeyStroke keyStroke) {
        if (keyStroke.getKeyType() == KeyType.Enter) {
            for (ISubmitListener listener : submitListeners) {
                listener.onSubmit(getText());
            }

            setText("");
            return Result.HANDLED;
        }
        return super.handleKeyStroke(keyStroke);
    }

    /**
     * Adds a listener that will be called when the user submits the text.
     *
     * @param submitListener the listener to add
     * @author Vasil Verdouw
     */
    public void addSubmitListener(ISubmitListener submitListener) {
        submitListeners.add(submitListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SubmittableTextBox that = (SubmittableTextBox) o;
        return Objects.equals(submitListeners, that.submitListeners);
    }

    /**
     * Returns a hash code value for the object. The hash code is based on the
     * submitListeners field.
     *
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(submitListeners);
    }

}
