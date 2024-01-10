package nl.han.screens.game;

import com.googlecode.lanterna.gui2.Panel;

import java.util.List;

/**
 * The InventoryRenderer class is responsible for rendering the inventory in the UI.
 *
 * @Author Jasper Kooy
 */
public class InventoryRenderer {
    private HistoryLabel inventoryLabel;

    /**
     * Renders the current player's inventory in the UI
     * @param height the height of the inventory panel
     * @param width the width of the inventory panel
     * @return the panel containing the inventory
     *
     * @author Jasper Kooy
     */
    public Panel createInventory(int height, int width) {
        Panel inventoryLog = new Panel();
        inventoryLabel = new HistoryLabel("You do not have any items currently.", height, width, false, "");
        inventoryLog.addComponent(inventoryLabel);
        return inventoryLog;
    }

    /**
     * Updates the inventory with the current player items
     * @param inventoryItems the items to be displayed in the inventory
     *
     * @author Jasper Kooy
     */
    public void updateInventory(List<String> inventoryItems) {
        inventoryLabel.setItemHistory(inventoryItems);
    }
}
