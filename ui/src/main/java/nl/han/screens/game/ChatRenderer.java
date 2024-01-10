package nl.han.screens.game;

import com.googlecode.lanterna.gui2.Panel;
import lombok.Setter;

/**
 * The ChatRenderer class is responsible for rendering the chat log on the screen.
 * <br/>
 * It contains methods to create the chat log and add chat messages to it.
 *
 * @author Jordan Geurtsen
 * @see HistoryLabel
 * @see Panel
 */
@Setter
public class ChatRenderer {
    private HistoryLabel chatLabel;

    /**
     * Creates a chat log with a history label. The history label is used to display
     * chat messages in an inverted order.
     *
     * @return the panel containing the chat log
     * @author Jordan Geurtsen
     * @see HistoryLabel
     */
    public Panel createChatLog(int height, int width) {
        Panel chatLog = new Panel();
        chatLabel = new HistoryLabel("Start a message with ':' to chat!", height, width, true, "");
        chatLog.addComponent(chatLabel);
        return chatLog;
    }

    /**
     * Adds a chat message to the chat log.
     *
     * @param message the message to be added to the chat log
     * @author Jordan Geurtsen
     * @see HistoryLabel#addToHistory(String)
     */
    public void addChatMessage(String message) {
        chatLabel.addToHistory(message);
    }
}
