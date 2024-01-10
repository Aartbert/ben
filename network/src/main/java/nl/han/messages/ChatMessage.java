package nl.han.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nl.han.shared.Peer;

import java.io.Serializable;


/**
 * The {@code ChatMessage} class represents a chat message in the game.
 *
 * @author Dylan Buil
 */
@Getter
@Setter
@AllArgsConstructor
public class ChatMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private String message;
}