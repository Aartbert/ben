package nl.han.shared.datastructures.creature;

import lombok.Getter;
import lombok.Setter;
import nl.han.ISavable;
import nl.han.shared.datastructures.BoundedValue;
import nl.han.shared.datastructures.Config;
import nl.han.shared.datastructures.world.Coordinate;
import nl.han.shared.enums.BotType;

import java.util.UUID;


/**
 * This class represents a Bot which extends from the Creature class and implements the ISavable interface.
 *
 * @author Jordan Geurtsen
 * @see Creature
 * @see ISavable
 */
@Getter
@Setter
public class Bot extends Creature {
    private final BotType botType;

    /**
     * Constructs a Bot object with id and coordinate.
     *
     * @param id         Unique identifier for the bot.
     * @param coordinate The location of the bot.
     * @param gameId     The id of the game of the bot.
     * @param config     The configuration of the bot.
     * @author Rieke Jansen
     * @see BoundedValue
     */
    public Bot(UUID id, Coordinate coordinate, UUID gameId, Config config,
               BotType botType) {
        super(id, coordinate, gameId, config, new BoundedValue(botType.getHealth(), botType.getHealth(), 0), new BoundedValue(botType.getStamina(), botType.getStamina(), 0), new BoundedValue(botType.getPower(), botType.getPower(), 0));
        this.botType = botType;
    }
}