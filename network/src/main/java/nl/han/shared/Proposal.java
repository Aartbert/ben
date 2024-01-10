package nl.han.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * The {@code Proposal} class represents a proposal in a distributed system, encapsulating information about
 * the sender peer and the proposed state change.
 * <p>
 * This class implements the {@code Serializable} interface, allowing instances to be serialized for network
 * transmission or persistent storage. It utilizes Lombok annotations for automatic generation of getter, setter,
 * and constructor methods.
 * <p>
 * The class includes a default constructor for deserialization purposes and an all-args constructor for convenient
 * instantiation with specific values.
 *
 * @author Laurens van Brecht, Dylan Buil
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Proposal implements Serializable {
    /**
     * The {@code sender} field represents the peer that originated the proposal.
     */
    private Peer sender;

    /**
     * The {@code state} field represents the proposed state change associated with the proposal.
     */
    private State state;
}