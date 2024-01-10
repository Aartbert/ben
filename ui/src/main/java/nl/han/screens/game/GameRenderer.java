package nl.han.screens.game;

import java.awt.Color;

import com.google.inject.Singleton;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import nl.han.shared.datastructures.VisualTile;

/**
 * The GameRenderer class is responsible for rendering the game world on the
 * screen.
 * <br/>
 * It contains methods to create the game screen, update the game screen, and
 * convert colors to text colors.
 * <br/>
 * The class uses a 2D array of Labels to represent the game world tiles and a
 * Panel to display them.
 * 
 * @author Vasil Verdouw
 */
@Getter
@Setter
@ToString
@Singleton
public class GameRenderer {
    private static final int GAME_WIDTH = 80;
    private static final int GAME_HEIGHT = 24;
    private Label[][] worldTiles;
    private Panel gamePanel;

    /**
     * Creates the game screen by creating a Panel with a GridLayout and adding
     * Labels to it.
     * <br/>
     * The GridLayout is set to the size of the game world and the Labels are added
     * to the Panel.
     * <br/>
     * The Panel is then returned.
     * 
     * @return The created game screen to be added to the window.
     * @author Vasil Verdouw
     */
    public Panel createGameScreen() {
        gamePanel = new Panel(new GridLayout(GAME_WIDTH).setHorizontalSpacing(0).setVerticalSpacing(0)
                .setLeftMarginSize(0).setRightMarginSize(0).setTopMarginSize(0).setBottomMarginSize(0));
        gamePanel.setPreferredSize(new TerminalSize(GAME_WIDTH, GAME_HEIGHT));

        setupWorldTiles();

        return gamePanel;
    }

    /**
     * Sets up the world tiles by removing all components from the game panel,
     * creating a new 2D array of labels with the dimensions of GAME_HEIGHT and
     * GAME_WIDTH.
     * <br/>
     * In the end it adds each label to the game panel.
     * 
     * @author Vasil Verdouw, Sem Gerrits
     */
    protected void setupWorldTiles() {
        gamePanel.removeAllComponents();
        worldTiles = new Label[GAME_HEIGHT][GAME_WIDTH];
        for (int y = 0; y < GAME_HEIGHT; y++) {
            for (int x = 0; x < GAME_WIDTH; x++) {
                Label label = new Label(" ");
                label.setForegroundColor(TextColor.ANSI.WHITE);
                label.setBackgroundColor(TextColor.ANSI.BLACK);
                worldTiles[y][x] = label;
                gamePanel.addComponent(label);
            }
        }
    }

    /**
     * Updates the game screen with the given tiles.
     * 
     * @param visualTiles The tiles to update the game screen with.
     * @author Vasil Verdouw, Sem Gerrits
     */
    public void updateGameScreen(VisualTile[][] visualTiles) {
        for (int y = 0; y < GAME_HEIGHT; y++) {
            for (int x = 0; x < GAME_WIDTH; x++) {
                worldTiles[y][x].setText(String.valueOf(visualTiles[y][x].getCharacter()));
                worldTiles[y][x].setForegroundColor(colorToTextColor(visualTiles[y][x].getCharacterColor()));
                worldTiles[y][x].setBackgroundColor(colorToTextColor(visualTiles[y][x].getBackgroundColor()));
            }
        }
    }

    /**
     * Represents a color in the Lanterna text-based interface library.
     * 
     * @param color The color to be converted.
     * @return The converted color.
     * @author Vasil Verdouw
     */
    public TextColor colorToTextColor(Color color) {
        return new TextColor.RGB(color.getRed(), color.getGreen(), color.getBlue());
    }
}
