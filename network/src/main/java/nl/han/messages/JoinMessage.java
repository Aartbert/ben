package nl.han.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.han.shared.Peer;

import java.io.Serial;
import java.io.Serializable;

/**
 * The {@code JoinMessage} class represents a serializable message used to convey information about a peer
 * intending to join a distributed system. This message is typically sent from a joining peer to the host peer
 * to announce its presence and initiate the integration process within the distributed system.
 * <p>
 * This class implements the {@code Serializable} interface to enable the serialization and deserialization of
 * instances, allowing them to be transmitted over a network or stored persistently.
 * <p>
 * The class is annotated with Lombok annotations to automatically generate getter, setter, and constructor methods.
 * The default constructor is provided for deserialization purposes, and an all-args constructor is included for
 * convenient instantiation with a new peer.
 *
 * @author Dylan Buil
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JoinMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The {@code newPeer} field represents the peer that is initiating the join process.
     *
     * @author Dylan Buil
     */
    private Peer newPeer;
}
