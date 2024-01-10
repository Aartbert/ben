package nl.han.client;

import com.google.gson.annotations.Expose;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import nl.han.INetworkListener;
import nl.han.bootstrap.BootstrapHandler;
import nl.han.messages.*;
import nl.han.shared.*;
import nl.han.shared.Lobby;
import nl.han.threephasecommit.ThreePhaseCommitHandler;

import javax.sound.sampled.AudioInputStream;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * The {@code Client} class represents a client in a distributed system that uses a Three-Phase Commit protocol for consensus.
 * It interacts with a Bootstrap Server to manage lobbies and communicates with other peers in a lobby to achieve consensus.
 *
 * @author Dylan Buil, Laurens van Brecht
 * @see <a href="https://confluenceasd.aimsites.nl/x/qwDjGQ">Testplan netwerk</a>
 */
@Getter
@Setter
@Log
@Singleton
public class Network implements INetwork {

    @Inject
    private BootstrapHandler bootstrapHandler;

    @Inject
    private ThreePhaseCommitHandler threePhaseCommitHandler;

    private Lobby currentLobby;

    @Expose
    private Peer selfPeer;

    private ArrayList<INetworkListener> networkListeners = new ArrayList<>();

    /**
     * Hosts a lobby based on the provided request, creating a lobby on the Bootstrap Server and locally.
     * This method is covered by the end-to-end test NET10.
     *
     * @param lobbyName The lobby name of the lobby they want to create
     * @author Dylan Buil, Laurens van Brecht
     */
    public void hostLobby(String lobbyName) {
        currentLobby = bootstrapHandler.createLobby(lobbyName, selfPeer);
        selfPeer.setPriority(0);
    }

    /**
     * Joins an existing lobby based on the provided request.
     * This method is covered by the end-to-end test NET11.
     *
     * @param lobbyName The lobby name of the lobby they want to create
     * @author Dylan Buil, Laurens van Brecht
     */
    public void joinLobby(String lobbyName) {
        Lobby lobby = bootstrapHandler.joinLobby(lobbyName, selfPeer);
        currentLobby = lobby;
        selfPeer.setPriority(1);
        sendMessage(new JoinMessage(selfPeer), lobby.getHost());
    }

    /**
     * Starts the server socket to listen for incoming messages from other peers.
     *
     * @author Dylan Buil, Laurens van Brecht, Lex van Walsem
     */
    private void startServerSocket() {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            selfPeer.setPort(serverSocket.getLocalPort());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleConnection(clientSocket);
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, e, () -> "Could not create server socket: " + e.getMessage());
        }
    }

    /**
     * Handles a connection from a client socket by reading the incoming message and sending a response.
     *
     * @param clientSocket The client socket to handle.
     * @throws IOException If an I/O error occurs during message handling.
     * @author Dylan Buil
     */
    private void handleConnection(Socket clientSocket) throws IOException {
        try (clientSocket; ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream())); ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream())) {
            Object receivedObject = in.readObject();
            handleIncomingMessage(receivedObject, outputStream);
        } catch (IOException | ClassNotFoundException e) {
            log.log(Level.SEVERE, e, () -> "Could not read object from input stream: " + e.getMessage());
        }
    }

    /**
     * Initializes the server socket and starts the peer with the given username.
     *
     * @param username The username for the peer.
     * @author Jochem Kalsbeek
     */
    public void start(String username) {
        selfPeer = new Peer(username);
        new Thread(this::startServerSocket).start();
    }

    /**
     * Sends chat messages to *all* peers in the lobby.
     *
     * @param message The message to send.
     * @param ips     The ips to send the message to.
     *                               TODO: Change currentLobby.getPeers() to make use of the ips variable, based on the teams.
     */
    @Override
    public void sendChatMessage(String message, List<String> ips) {
        ChatMessage chatMessage = new ChatMessage(message);
        log.info(String.valueOf(currentLobby.getPeers().size()));
        for (Peer p : currentLobby.getPeers()) {
            log.info(p.getUserName() + " " + p.getIpWithPort());
            if (!p.getIpWithPort().equals(selfPeer.getIpWithPort())) {
                sendMessage(chatMessage, p);
            }
        }
    }

    /**
     * Sends audio input stream to *all* peers in the lobby.
     *
     * @param audioInputStream The audio input stream to send.
     * @param ips     The ips to send the message to.
     *                               TODO: Change currentLobby.getPeers() to make use of the ips variable, based on the teams.
     */
    public void sendAudioInputStream(AudioInputStream audioInputStream, List<String> ips) {
        log.info(String.valueOf(currentLobby.getPeers().size()));
        for (Peer p : currentLobby.getPeers()) {
            log.info(p.getUserName() + " " + p.getIpWithPort());
            if (!p.getIpWithPort().equals(selfPeer.getIpWithPort())) {
                sendMessage(audioInputStream, p);
            }
        }
    }


    /**
     * Handles incoming messages from other peers based on their type.
     *
     * @param receivedObject     The received object, representing a message from another peer.
     * @param senderOutputStream The output stream to send responses back to the sender.
     * @throws IOException If an I/O error occurs during message handling.
     * @author Dylan Buil, Laurens van Brecht
     */
    public void handleIncomingMessage(Object receivedObject, ObjectOutputStream senderOutputStream) throws IOException {
        if (receivedObject instanceof JoinMessage lobbyMessage) {

            receiveJoinMessage(lobbyMessage, lobbyMessage.getNewPeer());

        } else if (receivedObject instanceof ThreePhaseCommitMessage message) {

            threePhaseCommitHandler.handleIncomingMessage(message, senderOutputStream);

        } else if (receivedObject instanceof ChatMessage message) {

            log.info(message.getMessage());
            receiveChatMessage(message);

        } else if (receivedObject instanceof JoinedMessage message) {
            currentLobby.addPeer(message.getNewPeer());
            log.info("New peer joined lobby: " + message.getNewPeer().getUserName() + " (" + message.getNewPeer().getIpAddress() + ")");

        } else if (receivedObject instanceof FirstMessage message) {
            receiveFirstMessage(message);
        } else if (receivedObject instanceof GameMessage message) {
            receiveGameMessage(message);
        }
    }

    /**
     * Sends a message to a peer.
     *
     * @param message The message (object) to send.
     * @param peer    The peer to send the message to.
     * @author Dylan Buil, Jochem Kalsbeek, Jordan Geurtsen
     */
    public void sendMessage(Object message, Peer peer) {
        new Thread(() -> {
            try (Socket socket = new Socket(peer.getIpAddress(), peer.getPort()); ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())) {
                outputStream.writeObject(message);
                outputStream.flush();
            } catch (IOException e) {
                log.log(Level.SEVERE, e, () -> "Could not connect to peer: " + peer.getIpWithPort());
            }
        }).start();
    }

    public void sendGame(String gamestate) {
        if (currentLobby == null) return;
        for (Peer p : currentLobby.getPeers()) {
            if (p.getIpWithPort().equals(selfPeer.getIpWithPort())) continue;
            sendMessage(new GameMessage(gamestate, selfPeer), p);
        }
    }

    public void sendFirstMessage(String gamestate) {
        if (currentLobby == null) return;
        for (Peer p : currentLobby.getPeers()) {
            if (p.getIpWithPort().equals(selfPeer.getIpWithPort())) continue;
            sendMessage(new FirstMessage(gamestate, selfPeer), p);
        }
    }

    /**
     * Handles incoming chat messages from other peers and sends them to the network listener.
     *
     * @param message The message (object) to send.
     * @author Jochem Kalsbeek
     */
    private void receiveChatMessage(ChatMessage message) {
        for (INetworkListener listener : networkListeners) {
            listener.receiveChatMessage(message.getMessage());
        }
    }

    private void receiveAudioMessage(AudioMessage message) {
        for (INetworkListener listener : networkListeners) {
            listener.receiveAudioMessage(message.getMessage(), message.getSenderIpAdress());
        }
    }

    private void receiveGameMessage(GameMessage message) {
        for (INetworkListener listener : networkListeners) {
            listener.receiveGameState(message.getGameState());
        }
    }

    private void receiveFirstMessage(FirstMessage message) {
        for (INetworkListener listener : networkListeners) {
            listener.receiveFirstMessage(message.getGameState());
        }
    }

    private void receiveJoinMessage(JoinMessage message, Peer peer) {
        currentLobby.addPeer(message.getNewPeer());

        for (Peer p : currentLobby.getPeers()) {
            if (p.getIpWithPort().equals(selfPeer.getIpWithPort())) continue;
            if (p.getIpWithPort().equals(message.getNewPeer().getIpWithPort())) continue;

            sendMessage(new JoinedMessage(message.getNewPeer()), peer);
        }

        for (INetworkListener listener : networkListeners) {
            listener.sendFirstMessage();
        }
    }


    /**
     * @param listener The listener to add to the list of listeners.
     * @author Jochem Kalsbeek
     */
    @Override
    public void addNetworkListener(INetworkListener listener) {
        networkListeners.add(listener);
    }
}