package nl.han.shared;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code Lobby} class represents a data structure used to store information about a lobby in a distributed system.
 * It contains details such as the lobby's unique identifier, name, list of peers currently in the lobby, and the host peer.
 * <p>
 * This class implements the {@code Serializable} interface, allowing instances to be serialized for network transmission
 * or persistent storage. It utilizes Lombok annotations for automatic generation of getter and setter methods.
 * <p>
 * The class also includes methods to add and remove peers from the lobby, retrieve the host peer, and provide a string
 * representation of the lobby for debugging and logging purposes.
 *
 * @author Dylan Buil
 */
@Getter
@Setter
public class Lobby implements Serializable {
    @Expose
    private long id;

    @Expose
    private String name;

    @Expose
    private List<Peer> peers = new ArrayList<>();
    private Peer host;

    /**
     * Adds a peer to the lobby's list of peers.
     *
     * @param peer The peer to be added to the lobby.
     * @author Dylan Buil
     */
    public void addPeer(Peer peer) {
        peers.add(peer);
    }

    /**
     * Removes a peer from the lobby's list of peers.
     *
     * @param peer The peer to be removed from the lobby.
     * @author Dylan Buil
     */
    public void removePeer(Peer peer) {
        peers.forEach(p -> {
            if (p.getIpAddress().equals(peer.getIpAddress())) {
                peers.remove(p);
            }
        });
    }

    /**
     * Returns a string representation of the Lobby object.
     *
     * @return A string representing the lobby's details.
     * @author Dylan Buil
     */
    public String toString() {
        return "Lobby{" + "id=" + id + ", name='" + name + '\'' + ", peers=" + peers + '}';
    }

    /**
     * Retrieves the host peer from the list of peers in the lobby.
     *
     * @return The host peer if found; otherwise, returns null.
     * @author Dylan Buil
     */
    public Peer getHostInPeers() {
        for (Peer p : peers) {
            if (p.getPriority() == 0) {
                return p;
            }
        }
        return null;
    }
}

