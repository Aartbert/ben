package nl.han.compiler;

import nl.han.CompilerManager;
import nl.han.shared.datastructures.BoundedValue;
import nl.han.shared.datastructures.Config;
import nl.han.shared.datastructures.Item;
import nl.han.shared.datastructures.creature.Bot;
import nl.han.shared.datastructures.creature.Creature;
import nl.han.shared.datastructures.creature.Player;
import nl.han.shared.datastructures.game.Game;
import nl.han.shared.datastructures.world.Chunk;
import nl.han.shared.datastructures.world.Coordinate;
import nl.han.shared.datastructures.world.Tile;
import nl.han.shared.datastructures.world.World;
import nl.han.shared.enums.BotType;
import nl.han.shared.enums.GameMode;
import nl.han.shared.enums.ItemData;
import nl.han.shared.enums.TileType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testing class for E2E tests.
 *
 * @see <a href="https://confluenceasd.aimsites.nl/display/ASDS1G2/Testrapport+Onderzoek+Programmeren+Agents">Testrapport</a>
 */
class EndToEndTest {

    private static final Map<String, String> INSTRUCTIONS = Map.ofEntries(
            entry("loop naar beneden.", "[MOVE_DOWN]"),
            entry("loop 3 naar rechts.", "[MOVE_RIGHT, MOVE_RIGHT, MOVE_RIGHT]"),
            entry("loop omhoog en loop naar links.", "[MOVE_UP, MOVE_LEFT]"),
            entry("loop omhoog. val de speler aan.", "[MOVE_UP]"),
            entry("als mijn kracht 80 is, pak een item op.", "[]"),
            entry("als stamina groter is dan 50%, loop rond.", "[MOVE_AROUND]"),
            entry("als mijn hp groter is dan 40% en mijn hp minder dan 80%, ren weg.", "[RUN]"),
            entry("als ik een health potion heb, gebruik dan de health potion.", "[USE_HEALTH_POTION]"),
            entry("als speler binnen 5 tegels, val dan de speler aan.", "[SEARCH_PLAYER]"),
            entry("als monster binnen 10 tegels, dan val monster aan.", "[]"),
            entry("als vijand binnen 5 tegels, dan val vijand aan.", "[SEARCH_ENEMY]"),
            entry("als health potion binnen 5 tegels, dan oppakken.", "[SEARCH_ITEM]")
//            entry("lop beneden.", "Something went wrong with the processing of the input. Does the sentence has an action and ends with a dot?")
    );

    private CompilerManager sut;
    private Creature creature;
    private Game game;

    @BeforeEach
    void setup() {
        sut = new CompilerManager();

        // Tiles
        Tile[][] tiles = new Tile[Chunk.CHUNK_HEIGHT][Chunk.CHUNK_WIDTH];

        for (int y = 0; y < Chunk.CHUNK_HEIGHT; y++) {
            for (int x = 0; x < Chunk.CHUNK_WIDTH; x++) {
                Coordinate coordinate = new Coordinate(x, y);
                Tile tile = new Tile(TileType.FOREST, coordinate);
                tiles[y][x] = tile;
            }
        }

        tiles[10][12].addItem(new Item(UUID.randomUUID(), ItemData.SMALL_HEALTH_POTION));

        // Chunks
        Chunk chunk = new Chunk(tiles, new Coordinate(0, 0, 0), 0L, UUID.randomUUID());

        // Player 1
        BoundedValue health = new BoundedValue(50, 100, 0);
        BoundedValue stamina = new BoundedValue(100, 100, 0);
        BoundedValue power = new BoundedValue(200, 200, 0);

        Player player = new Player(UUID.randomUUID(), "LOL", null, UUID.randomUUID(), null, health, power, stamina, "");

        player.addItemToInventory(new Item(UUID.randomUUID(), ItemData.SMALL_HEALTH_POTION));
        player.setCoordinate(new Coordinate(10, 10));
        player.setChunk(chunk);

        creature = player;

        // Player 2
        Player player2 = new Player(UUID.randomUUID(), "LOL", null, UUID.randomUUID(), null, health, power, stamina, "");
        player2.addItemToInventory(new Item(UUID.randomUUID(), ItemData.SMALL_HEALTH_POTION));
        player2.setCoordinate(new Coordinate(14, 10));
        player2.setChunk(chunk);

        // Monster
        BotType botType = BotType.ZOMBIE;
        Bot monster = new Bot(UUID.randomUUID(), new Coordinate(0, 0), UUID.randomUUID(), new Config(UUID.randomUUID(), "a", "b"), botType);
        monster.setCoordinate(new Coordinate(24, 24));
        monster.setChunk(chunk);

        // World
        ArrayList<Chunk> chunks = new ArrayList<>();
        chunks.add(chunk);
        World world = new World(123, chunks, new Config(UUID.randomUUID(), "feminism.", ""));

        // Game
        game = new Game(UUID.randomUUID(), "Adil", GameMode.LMS, world);
        game.addPlayer(player);
        game.addPlayer(player2);
        game.addMonster(monster);

        sut.setCompiler(new CompilerController());
    }

    /**
     * Test code COM75
     */
    @ParameterizedTest
    @MethodSource("provider")
    @DisplayName("test if an instruction will result in the expected action")
    void testInstructions(String instruction, String expected) {
        // Arrange
        String actual;

        // Act
        try {
            actual = sut.compileInput(instruction, creature, game).toString();
        } catch (Exception e) {
            actual = e.getMessage();
        }

        // Assert
        assertEquals(expected, actual);
    }

    private static Stream<Object[]> provider() {
        return INSTRUCTIONS.entrySet().stream()
                .map(entry -> new Object[]{entry.getKey(), entry.getValue()});
    }
}
