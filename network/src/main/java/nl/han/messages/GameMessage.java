package nl.han.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.han.shared.Peer;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String gameState;
    private Peer peer;
}
