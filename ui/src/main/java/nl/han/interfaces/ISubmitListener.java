package nl.han.interfaces;

/**
 * A listener that will be called when a submit event is received.
 * 
 * @author Vasil Verdouw
 */
public interface ISubmitListener {
    /**
     * Called when a submit event is received.
     * 
     * @param text the text that was typed in the text box
     * @author Vasil Verdouw
     */
    void onSubmit(String text);
}
