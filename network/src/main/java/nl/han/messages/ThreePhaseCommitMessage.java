package nl.han.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.han.shared.Peer;
import nl.han.shared.Proposal;
import nl.han.threephasecommit.OperationType;

import java.io.Serial;
import java.io.Serializable;

/**
 * The {@code ThreePhaseCommitMessage} class represents a serializable message used in the Three-Phase Commit protocol,
 * conveying information about a particular operation to be committed or aborted by participating peers in a distributed system.
 * <p>
 * This message encapsulates details such as the sender peer, the type of operation (commit or abort), and the associated proposal.
 * The class implements the {@code Serializable} interface to enable instances to be serialized for network transmission or
 * persistent storage.
 * <p>
 * The class is annotated with Lombok annotations for automatic generation of getter, setter, and constructor methods. It includes
 * an all-args constructor for convenient instantiation with specific values and a default constructor for deserialization purposes.
 * <p>
 * Additionally, the class provides a customized {@code toString} method for creating a human-readable representation of the message,
 * primarily for debugging and logging purposes.
 *
 * @author Dylan Buil, Laurens van Brecht
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ThreePhaseCommitMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Peer sender;
    private OperationType operationType;
    private Proposal proposal;

    @Override
    public String toString() {
        return "ThreePhaseCommitMessage{" +
                "operationType='" + operationType + '\'' +
                ", data='" + proposal + '\'' +
                '}';
    }
}
