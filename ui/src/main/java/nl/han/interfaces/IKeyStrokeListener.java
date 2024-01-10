package nl.han.interfaces;

import nl.han.shared.enums.Key;

/**
 * A listener that will be called when a key stroke is received.
 * 
 * @author Vasil Verdouw
 */
public interface IKeyStrokeListener {
    /**
     * Called when a key stroke is received.
     * 
     * @param keyStroke the key stroke that was received
     * @author Vasil Verdouw
     */
    void onKeyStroke(Key keyStroke);
}
