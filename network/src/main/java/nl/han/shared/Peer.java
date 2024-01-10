package nl.han.shared;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import java.io.Serial;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;

/**
 * The {@code Peer} class represents a peer in a distributed system, storing information such as
 * its unique identifier, priority, communication port, leadership status, username, IP address,
 * leader reference, and current state.
 * This class implements the {@code Serializable} interface, allowing instances to be serialized for
 * network transmission or persistent storage. It utilizes Lombok annotations for automatic generation
 * of getter, setter, and constructor methods.
 * The class also provides a string representation of the peer for debugging and logging purposes.
 *
 * @author Laurens van Brecht, Dylan Buil
 */
@Getter
@Setter
@AllArgsConstructor
@Log
public class Peer implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Expose
    private int id;
    @Expose
    private int priority;
    @Expose
    private int port;

    @Expose
    private String userName;

    @Expose
    private String ipAddress;
    private State state;

    /**
     * Returns a string representation of the Peer object.
     *
     * @return A string representing the peer's details.
     * @author Laurens van Brecht
     */
    @Override
    public String toString() {
        return "Peer{" + "id=" + id + ", priority=" + priority + ", port=" + port + ", userName='" + userName + '\'' + ", ipAddress='" + ipAddress + '\'' + ", state=" + state + '}';
    }

    public Peer(String userName) {
        this.userName = userName;
        this.ipAddress = getLocalIp();
    }

    /**
     * Gets the local IP address of the machine.
     *
     * @return The local IP address of the machine.
     * @throws RuntimeException If the local IP address could not be resolved.
     * @author Dylan Buil
     * @see InetAddress#getLocalHost()
     * @see InetAddress#getHostAddress()
     * @see UnknownHostException
     */
    private static String getLocalIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.log(Level.SEVERE, e, () -> "Could not resolve local IP address: " + e.getMessage());
        }

        return null;
    }

    public String getIpWithPort() {
        return this.ipAddress + ":" + this.port;
    }
}




