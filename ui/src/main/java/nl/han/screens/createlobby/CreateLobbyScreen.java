package nl.han.screens.createlobby;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.googlecode.lanterna.gui2.*;
import lombok.Getter;
import lombok.Setter;
import nl.han.ButtonMaker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This is the create lobby screen class.
 *
 * @author Lars Meijerink & Jochem Kalsbeek
 */
@Getter
@Setter
public class CreateLobbyScreen extends BasicWindow {
    private final ComboBox<String> gameModeBox = new ComboBox<String>()
            .addItem("LMS")
            .addItem("CTF");
    private final ComboBox<String> worldSizeBox = new ComboBox<String>()
            .addItem("Tiny")
            .addItem("Small")
            .addItem("Normal")
            .addItem("Large")
            .addItem("Infinite");
    private final ComboBox<String> dungeonDepthBox = new ComboBox<String>()
            .addItem("Shallow")
            .addItem("Deep")
            .addItem("Humongous")
            .addItem("Infinite");
    private final ComboBox<String> itemSpawnRulesBox = new ComboBox<String>()
            .addItem("Poor")
            .addItem("Normal")
            .addItem("Generous");
    private final ComboBox<String> monsterSpawnRulesBox = new ComboBox<String>()
            .addItem("Less")
            .addItem("Normal")
            .addItem("Many");
    private final Random random = new Random();
    private final TextBox seedInput = new LongTextBox(String.valueOf(random.nextLong()));
    private final TextBox lobbyInput = new TextBox();
    private final Panel mainPanel = new Panel();
    private final Panel worldPanel = new Panel();
    private final Panel populationPanel = new Panel();
    private final Gson gson = new Gson();
    @Inject
    private ButtonMaker buttonMaker;

    /**
     * Creates the lobby screen.
     *
     * @author Fabian van Os & Sven van Hoof
     */
    public void createCreateLobby() {
        List<Button> buttons = new ArrayList<>();
        buttons.add(buttonMaker.createButton("Create", ""));
        buttons.add(buttonMaker.createButton("Return to Start", ""));

        mainPanel.addComponent(lobbyInput.withBorder(Borders.singleLine("Lobby name")))
                .addComponent(gameModeBox.withBorder(Borders.singleLine("Game mode")))
                .addComponent(seedInput.withBorder(Borders.singleLine("Seed")))
                .addComponent(worldPanel.withBorder(Borders.singleLine("World")))
                .addComponent(populationPanel.withBorder(Borders.singleLine("Population")))
                .addComponent(buttonMaker.addButtonsToPanel(buttons)
                        .setLayoutManager(new GridLayout(2)
                                .setHorizontalSpacing(3)
                                .setTopMarginSize(1)));

        worldPanel.addComponent(new Label("World size"))
                .addComponent(worldSizeBox
                        .withBorder(Borders.singleLine()))
                .addComponent(new Label("Dungeon depth"))
                .addComponent(dungeonDepthBox
                        .withBorder(Borders.singleLine()));

        populationPanel.addComponent(new Label("Item spawn rate"))
                .addComponent(itemSpawnRulesBox
                        .withBorder(Borders.singleLine()))
                .addComponent(new Label("Monster spawn rate"))
                .addComponent(monsterSpawnRulesBox
                        .withBorder(Borders.singleLine()));

        setComponent(mainPanel);
    }

    /**
     * @return string The name of the lobby.
     * @author Jochem Kalsbeek
     */
    public String getLobbyName() {
        return lobbyInput.getText();
    }

    public String getWorldConfig() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("seed", getSeedInput());
        jsonObject.add("itemSpawnRules", getItemSpawnRules());
        jsonObject.addProperty("dungeonDepth", getDungeonDepth());
        jsonObject.add("monsterSpawnRules", getMonsterSpawnRules());
        jsonObject.add("worldSize", getWorldSize());

        return jsonObject.toString();
    }

    private JsonObject getMonsterSpawnRules() {
        JsonObject monsterSpawnRules = new JsonObject();
        switch (monsterSpawnRulesBox.getText()) {
            case "Less" -> {
                monsterSpawnRules.addProperty("min", 1);
                monsterSpawnRules.addProperty("max", 3);
            }
            case "Many" -> {
                monsterSpawnRules.addProperty("min", 5);
                monsterSpawnRules.addProperty("max", 15);
            }
            default -> {
                monsterSpawnRules.addProperty("min", 3);
                monsterSpawnRules.addProperty("max", 10);
            }
        }

        return monsterSpawnRules;
    }

    private JsonObject getWorldSize() {
        JsonObject worldSpawnRules = new JsonObject();
        switch (worldSizeBox.getText()) {
            case "Tiny" -> {
                worldSpawnRules.addProperty("width", 5);
                worldSpawnRules.addProperty("height", 5);
            }
            case "Small" -> {
                worldSpawnRules.addProperty("width", 10);
                worldSpawnRules.addProperty("height", 10);
            }
            case "Large" -> {
                worldSpawnRules.addProperty("width", 50);
                worldSpawnRules.addProperty("height", 50);
            }
            case "Infinite" -> {
                worldSpawnRules.addProperty("width", -1);
                worldSpawnRules.addProperty("height", -1);
            }
            default -> {
                worldSpawnRules.addProperty("width", 20);
                worldSpawnRules.addProperty("height", 20);
            }
        }

        return worldSpawnRules;
    }

    private int getDungeonDepth() {
        return switch (dungeonDepthBox.getText()) {
            case "Shallow" -> 2;
            case "Deep" -> 3;
            case "Humongous" -> 4;
            default -> -1;
        };
    }

    private JsonObject getItemSpawnRules() {
        JsonObject itemSpawnRules = new JsonObject();
        switch (itemSpawnRulesBox.getText()) {
            case "Poor" -> {
                itemSpawnRules.addProperty("min", 2);
                itemSpawnRules.addProperty("max", 5);
            }
            case "Generous" -> {
                itemSpawnRules.addProperty("min", 13);
                itemSpawnRules.addProperty("max", 21);
            }
            default -> {
                itemSpawnRules.addProperty("min", 3);
                itemSpawnRules.addProperty("max", 7);
            }
        }

        return itemSpawnRules;
    }

    public String getGameMode() {
        return gameModeBox.getText();
    }

    public String getSeedInput() {
        String seed = seedInput.getText();

        if (seed.isEmpty()) {
            return String.valueOf(random.nextLong());
        }

        return seed;
    }
}
