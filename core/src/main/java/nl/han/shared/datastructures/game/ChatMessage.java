package nl.han.shared.datastructures.game;

import nl.han.shared.datastructures.creature.Player;

/**
 * A record representing a chat message, which includes the message content and the sender.
 *
 * @param message The content of the chat message.
 * @see String
 * @author Jordan Geurtsen
 */
public record ChatMessage(String message) {
}