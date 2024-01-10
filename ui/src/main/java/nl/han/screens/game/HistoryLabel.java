package nl.han.screens.game;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.Label;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The HistoryLabel class represents a label that displays a history of lines.
 * It provides methods for adding lines to the history and updating the label.
 *
 * @author Jordan Geurtsen
 * @see Label
 * @see TerminalSize
 */
public class HistoryLabel extends Label {
    private final List<String> history = new LinkedList<>();
    private final int maxHistoryHeight;
    private final int historyWidth;
    private final boolean inverted;
    private final String prefix;

    /**
     * Main constructor, creates a new Label displaying a specific text.
     *
     * @param initialText Text the label will display
     */
    public HistoryLabel(String initialText, int maxHistoryHeight, int historyWidth, boolean inverted, String prefix) {
        super(initialText);
        this.maxHistoryHeight = maxHistoryHeight;
        this.historyWidth = historyWidth;
        this.inverted = inverted;
        this.prefix = prefix;
    }

    /**
     * Adds a line to the history of the console.
     * If the history exceeds the maximum height, the oldest line is removed.
     *
     * @param line the line to be added to the history
     * @author Vasil Verdouw, Jordan Geurtsen
     */
    public void addToHistory(String line) {
        if (line == null || line.isEmpty())
            return;

        String overFlow = "";
        int maxLineLength = historyWidth - prefix.length() * 2;

        if (line.length() >= maxLineLength) {
            overFlow = line.substring(maxLineLength);
            line = line.substring(0, maxLineLength);
        }

        if (history.size() >= maxHistoryHeight) {
            if (inverted) {
                history.remove(history.size() - 1);
            } else {
                history.remove(0);
            }
        }

        if (inverted) {
            history.add(0, line);
            addToHistory(overFlow);
        } else {
            addToHistory(overFlow);
            history.add(line);
        }

        updateHistoryLabel();
    }

    /**
     * Sets the history of the console. Adds all lines to the history 1 by 1.
     * Keep in mind that it might add the lines the other way around if inverted is
     * enabled.
     * 
     * @param history the history to be set to the console
     * @author Vasil Verdouw
     */
    public void setHistory(List<String> history) {
        this.history.clear();
        for (String line : history) {
            addToHistory(line);
        }
        updateHistoryLabel();
    }

    /**
     * Clears the history of the console.
     * 
     * @author Vasil Verdouw
     */
    public void clearHistory() {
        history.clear();
        updateHistoryLabel();
    }

    /**
     * Updates the history label with the current history.
     *
     * @author Vasil Verdouw, Sem Gerrits, Jordan Geurtsen
     */
    private void updateHistoryLabel() {
        StringBuilder sb = new StringBuilder();
        if (inverted) {
            sb.append("\n".repeat(Math.max(0, maxHistoryHeight - history.size())));
        }
        for (int i = history.size() - 1; i >= 0; i--) {
            sb.append(prefix);
            sb.append(history.get(i));
            sb.append("\n");
        }
        setText(sb.toString());
        setPreferredSize(new TerminalSize(historyWidth, maxHistoryHeight));
    }

    /**
     * Set the items visible in the interface
     *
     * @param inventoryItems the items to be displayed
     * @author Jasper Kooy
     */
    public void setItemHistory(List<String> inventoryItems) {
        StringBuilder sb = new StringBuilder();
        if (inverted) {
            sb.append("\n".repeat(Math.max(0, maxHistoryHeight - history.size())));
        }
        HashMap<String, Integer> itemCount = new HashMap<>();
        for (String item : inventoryItems) {
            itemCount.put(item, itemCount.getOrDefault(item, 0) + 1);
        }

        for (Map.Entry<String, Integer> entry : itemCount.entrySet()) {
            String itemName = entry.getKey();
            int quantity = entry.getValue();

            String displayString = itemName + " x" + quantity;
            sb.append(displayString);
            sb.append("\n");
        }
        setText(sb.toString());
        setPreferredSize(new TerminalSize(historyWidth, maxHistoryHeight));
    }
}
