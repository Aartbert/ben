package nl.han;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import lombok.extern.java.Log;
import nl.han.bootstrap.BootstrapHandler;
import nl.han.client.INetwork;
import nl.han.messages.AudioMessage;
import nl.han.shared.datastructures.creature.Player;
import nl.han.shared.datastructures.game.ChatMessage;
import nl.han.shared.datastructures.game.Game;
import nl.han.shared.datastructures.game.Team;

import javax.sound.sampled.AudioInputStream;
import java.util.ArrayList;
import java.util.List;

@Log
public class NetworkManager implements INetworkListener {

    @Inject
    protected GameManager gameManager;
    @Inject
    private BootstrapHandler bootstrapHandler;
    @Inject
    private INetwork network;

    private final Gson gson = new GsonBuilder().create();

    public void start(String username) {
        network.start(username);
        network.addNetworkListener(this);
    }

    public List<String> getLobbies() {
        return bootstrapHandler.getLobbyNames();
    }

    public void createLobby(String name) {
        network.hostLobby(name);
    }

    public void joinLobby(String name) {
        network.joinLobby(name);
    }

    /**
     * Sends a chat message to the server.
     *
     * @param chatMessage the chat message to send
     * @author Vasil Verdouw
     * @see ChatMessage
     * @see GameManager#sendChatMessage(String)
     */
    public void sendChatMessage(ChatMessage chatMessage, Player sender, Team team) {
        List<String> ips = new ArrayList<>();

        for (Player player : team.getPlayers()) {
            if (!player.getIpAddress().equals(sender.getIpAddress())) {
                ips.add(player.getIpAddress());
            }
        }
        network.sendChatMessage(chatMessage.message(), ips);
    }

    /**
     * Sends a audio input stream to the server.
     *
     * @param audioInputStream the audio input stream to send
     * @author Lucas van Steveninck
     * @see GameManager#sendChatMessage(String)
     */
    public void sendAudioInputStream(AudioInputStream audioInputStream, Player sender, Team team) {
        List<String> ips = new ArrayList<>();

        for (Player player : team.getPlayers()) {
            if (!player.getIpAddress().equals(sender.getIpAddress())) {
                ips.add(player.getIpAddress());
            }
        }
        network.sendAudioInputStream(audioInputStream, ips);
    }

    /**
     * Receives a chat message from the server.
     *
     * @param message the chat message to receive
     * @author Vasil Verdouw
     * @see ChatMessage
     * @see GameManager#receiveChatMessage(ChatMessage)
     */
    @Override
    public void receiveChatMessage(String message) {
        gameManager.receiveChatMessage(new ChatMessage(message));
    }


    /**
     * Receives a chat message from the server.
     *
     * @param message the chat message to receive
     * @author Vasil Verdouw
     * @see ChatMessage
     * @see GameManager#receiveChatMessage(ChatMessage)
     */
    @Override
    public void receiveAudioMessage(AudioInputStream message, String ipAdress) {
        gameManager.receiveAudioMessage(new AudioMessage(message, ipAdress));
    }

    @Override
    public void receiveFirstMessage(String gamestate) {
        receiveGameState(gamestate);
        sendGameState(gameManager.getCurrentPlayer());
    }

    /**
     * Receives a serialized game state as a JSON string and updates the local game state accordingly.
     *
     * @param gameState A JSON string representing the serialized game state.
     * @see Game
     *
     * @author Laurens van Brecht
     */
    @Override
    public void receiveGameState(String gameState) {
        Player player = gson.fromJson(gameState, Player.class);
        log.info(String.valueOf(player));
        gameManager.onUpdatePlayer(player);
    }

    /**
     * Serializes the current game state and sends it over the network.
     * <p>
     * This method retrieves the current game state from the GameManager,
     * converts it to a JSON string using Gson, and sends it through the network.
     *
     * @see Game
     *
     * @author Laurens van Brecht
     */
    public void sendGameState(Player player) {
        String json = player.toJson();
        log.info(json);
        network.sendGame(json);
    }

    @Override
    public void sendFirstMessage() {
        Player player = gameManager.getCurrentPlayer();
        String json = player.toJson();
        log.info(json);
        network.sendFirstMessage(json);
    }
}
