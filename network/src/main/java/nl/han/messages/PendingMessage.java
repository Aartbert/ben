package nl.han.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.han.shared.Peer;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PendingMessage {

    private Peer newPeer;
}
