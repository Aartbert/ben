package nl.han;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.java.Log;
import nl.han.shared.datastructures.Item;
import nl.han.shared.datastructures.WorldRules;
import nl.han.shared.datastructures.WorldRulesBuilder;
import nl.han.shared.datastructures.creature.Creature;
import nl.han.shared.datastructures.creature.Player;
import nl.han.shared.datastructures.world.Chunk;
import nl.han.shared.datastructures.world.Coordinate;
import nl.han.shared.datastructures.world.Tile;
import nl.han.shared.enums.Action;

import java.util.List;
import java.util.stream.Stream;

import static java.lang.Math.abs;
import static nl.han.shared.enums.ItemData.*;

/**
 * This class is responsible for managing various actions within the game.<br/>
 * It has a log for debugging purposes and can process multiple actions for a
 * given creature.<br/>
 * {@link GameManager} is used in this class to maintain the game state.
 *
 * @author Jochem Kalsbeek
 * @see GameManager
 */
@Log
@Singleton
public class ActionManager {

    @Inject
    private GameManager gameManager;

    @Inject
    private UIManager uiManager;

    /**
     * Executes the specified action for a given creature.<br/>
     * This method supports moving the creature up, down, left, or right. It can
     * also
     * use a staircase, different health potions and other extra items if necessary.
     * It also lets a player run, attack, pick up and search items and quit the
     * game.
     *
     * @param action   the action to be executed
     * @param creature the creature which the action is being executed for
     * @throws IllegalStateException if an illegal action is provided
     * @author Jochem Kalsbeek, Rieke Jansen, Justin Slijkhuis, Thomas Droppert, Adil Sadiki
     */
    public void handleAction(Action action, Creature creature) {
        if (validateAction(action, creature)) {
            switch (action) {
                case MOVE_UP -> creature.moveUp();
                case MOVE_DOWN -> creature.moveDown();
                case MOVE_LEFT -> creature.moveLeft();
                case MOVE_RIGHT -> creature.moveRight();
                case ATTACK_PLAYER -> creature.attack(getAdjacentCreature(attackPlayerAction()));
                case ATTACK_MONSTER -> creature.attack(getAdjacentCreature(attackMonsterAction()));
                case ATTACK_ENEMY -> creature.attack(getAdjacentCreature(attackEnemyAction()));
                case USE_STAIRCASE -> creature.useStaircase();

                //TODO: fetch the right creature to attack from compiler/command to attack
                case RUN -> gameManager.runAway();
                case SEARCH_PLAYER -> gameManager.searchPlayer();
                case SEARCH_ENEMY -> gameManager.searchEnemy();
                case SEARCH_MONSTER -> gameManager.searchMonster();
                case USE_HEALTH_POTION -> gameManager.handleInventoryItem(action, creature);
                case USE_SMALL_HEALTH_POTION -> creature.increaseHealth(SMALL_HEALTH_POTION.getValue());
                case USE_MEDIUM_HEALTH_POTION -> creature.increaseHealth(MEDIUM_HEALTH_POTION.getValue());
                case USE_BIG_HEALTH_POTION -> creature.increaseHealth(BIG_HEALTH_POTION.getValue());
                case QUIT_GAME -> gameManager.stop();
                case SEARCH_ITEM -> gameManager.moveToClosestItem();
                case PICK_UP -> pickUpItem();
                case INTERACT -> gameManager.interact();
                default -> throw new IllegalStateException();
            }
        }
    }

    /**
     * Checks if an action is allowed.
     *
     * @param action the action to be executed.
     * @param player the player which the action is being executed for.
     * @return true if the action is allowed.
     * @author Rieke Jansen, Thomas Droppert
     */
    private boolean validateAction(Action action, Creature player) {
        if (player.getHealth().getValue().intValue() <= 0) {
            return false;
        }
        return switch (action) {
            case MOVE_RIGHT, MOVE_DOWN, MOVE_UP, MOVE_LEFT -> validateMovement(action, player);
            case USE_HEALTH_POTION -> validateItemUsage(action, (Player) player);
            case USE_SMALL_HEALTH_POTION -> true;
            case USE_MEDIUM_HEALTH_POTION -> true;
            case USE_BIG_HEALTH_POTION -> true;
            case USE_STAIRCASE -> true;//TODO validate dit
            case ATTACK_PLAYER, ATTACK_ENEMY, ATTACK_MONSTER -> validateAttack();
            case SEARCH_PLAYER, SEARCH_ENEMY, SEARCH_MONSTER -> true;
            case PICK_UP -> player.getTile().hasItems();
            case SEARCH_ITEM -> validateChunkHasItem(player);
            case INTERACT -> true;
            case RUN -> true;
            case QUIT_GAME -> true;
            default -> false;
        };
    }

    private boolean validateAttack(){
        return getAdjacentCreature(gameManager.allCreaturesInChunk()) != null;
    }

    /**
     * Checks if an item is in a players inventory.
     *
     * @param action the action that determines which kind of item needs to be checked.
     * @param player the player that the item should be used on.
     * @return true if the item is in the players inventory.
     * @author Rieke Jansen, Thomas Droppert
     */
    private boolean validateItemUsage(Action action, Player player) {
        for (Item item : player.getInventory()) {
            if (action == Action.USE_HEALTH_POTION) {
                if (Stream.of(BIG_HEALTH_POTION, MEDIUM_HEALTH_POTION, SMALL_HEALTH_POTION).anyMatch(itemData -> (item.getItemData() == itemData))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if movement in a specific direction is allowed.
     *
     * @param action the action to be executed
     * @param player the player which the action is being executed for
     * @author Rieke Jansen
     */
    private boolean validateMovement(Action action, Creature player) {
        WorldRules.WorldSize worldSize = WorldRulesBuilder.convertToWorldRules(gameManager.getGame().getWorld().getConfig()).getWorldSize();

        Chunk chunk = player.getChunk();
        int x = player.getCoordinate().x();
        int y = player.getCoordinate().y();
        switch (action) {
            case MOVE_UP -> y--;
            case MOVE_RIGHT -> x++;
            case MOVE_DOWN -> y++;
            case MOVE_LEFT -> x--;
            default -> throw new IllegalStateException();
        }

        int width = Chunk.CHUNK_WIDTH;
        int height = Chunk.CHUNK_HEIGHT;
        int chunkX = chunk.getCoordinate().x();
        int chunkY = chunk.getCoordinate().y();
        int chunkZ = chunk.getCoordinate().z();

        if (x <= 0) {
            x = width + x;
            chunkX--;
        }
        if (x >= width) {
            x %= width;
            chunkX++;
        }
        if (y <= 0) {
            y = height + y;
            chunkY--;
        }
        if (y >= height) {
            y %= height;
            chunkY++;
        }
        if (worldSize.getWidth() != -1 && (abs(chunkX) > worldSize.getWidth() || abs(chunkY) > worldSize.getHeight())) return false;
        Coordinate newCoordinate = new Coordinate(chunkX, chunkY, chunkZ);
        chunk = gameManager.getCurrentChunk(newCoordinate);
        boolean stamina = validateStamina(chunk.getTiles()[y][x].getMovementCost(), player.getStamina().getIntValue());
        boolean passable = chunk.getTiles()[y][x].isPassable();
        return passable && stamina;
    }

    /**
     * This method is responsible for validating if the player has enough
     * stamina<br/>
     *
     * @author Jochem Kalsbeek
     */
    private boolean validateStamina(int cost, int stamina) {
        return cost <= stamina;
    }

    /**
     * Picks up the item on the tile the player is currently on
     *
     * @author Justin Slijkhuis
     */
    private void pickUpItem() {
        Player currentPlayer = gameManager.getCurrentPlayer();
        Tile tile = currentPlayer.getTile();
        if (tile.hasItems()) {
            Item item = tile.getItems().get(0);
            currentPlayer.addItemToInventory(item);
            tile.removeItem(item);
            uiManager.updateInventory(currentPlayer.getInventory());
        }
    }

    /**
     * Retrieves the first adjacent creature from the given list based on the current player's position.
     *
     * @param creatures A list of creatures to check for adjacency.
     * @return The first adjacent creature, or null if none are found.
     * @author Adil Sadiki, Justin Slijkhuis
     */
    public Creature getAdjacentCreature(List<Creature> creatures) {
        Coordinate currentPlayerCoordinate = gameManager.getCurrentPlayer().getCoordinate();

        // Check if any creature's coordinate is equal to one of the current player's adjacent coordinates
        return creatures.stream()
                .filter(creature -> currentPlayerCoordinate.getAdjacentCoordinates().contains(creature.getCoordinate()))
                .findFirst().orElse(null);
    }

    /**
     * Retrieves a list of all players in the current chunk.
     *
     * @return A list of player creatures in the current chunk.
     * @author Adil Sadiki, Justin Slijkhuis
     */
    public List<Creature> attackPlayerAction() {
        return gameManager.allPlayersInChuck(
                gameManager.allCreaturesInChunk()
        );
    }

    /**
     * Retrieves a list of all monsters in the current chunk.
     *
     * @return A list of monster creatures in the current chunk.
     * @author Adil Sadiki, Justin Slijkhuis
     */
    private List<Creature> attackMonsterAction() {
        return gameManager.allMonsterInChuck(
                gameManager.allCreaturesInChunk()
        );
    }

    /**
     * Retrieves a list of all creatures (players and monsters) in the current chunk.
     *
     * @return A list of all creature creatures in the current chunk.
     * @author Adil Sadiki, Justin Slijkhuis
     */
    private List<Creature> attackEnemyAction() {
        return gameManager.allCreaturesInChunk();
    }

    /**
     * This method is responsible for validating if the chunk has an item
     *
     * @param player The player that is searching for an item
     * @return boolean indicating if the chunk has an item
     * @author Justin Slijkhuis
     */
    private boolean validateChunkHasItem(Creature player) {
        Chunk chunk = player.getChunk();
        Tile tile = chunk.getClosestTileWithItem(player.getCoordinate());
        return tile != null;
    }
}
