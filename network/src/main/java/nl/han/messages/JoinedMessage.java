package nl.han.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.han.shared.Peer;

import java.io.Serializable;

/**
 * The {@code JoinedMessage} class represents a message that is sent when a peer joins the game.
 *
 * @author Dylan Buil
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JoinedMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private Peer newPeer;
}
