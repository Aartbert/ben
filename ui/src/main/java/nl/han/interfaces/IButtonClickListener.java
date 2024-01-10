package nl.han.interfaces;

/**
 * A listener that will be called when a submit event is received.
 * 
 * @author Lars Meijerink, Vasil Verdouw
 */
public interface IButtonClickListener {
    /**
     * This method is called when a "click" on a button is received.
     * 
     * @param buttonName the name of the button that was clicked
     * @author Lars Meijerink, Vasil Verdouw
     */
    void onButtonClick(String buttonName, String argument);
}
