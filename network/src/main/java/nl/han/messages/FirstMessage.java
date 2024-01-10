package nl.han.messages;

import nl.han.shared.Peer;

import java.io.Serial;

public class FirstMessage extends GameMessage {
    @Serial
    private static final long serialVersionUID = 1L;

    public FirstMessage(String message, Peer peer) {
        super(message, peer);
    }

}
