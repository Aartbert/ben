package nl.han.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.sound.sampled.AudioInputStream;
import java.io.Serializable;


/**
 * The {@code AudioMessage} class represents an audio message in the game.
 *
 * @author Lucas van Steveninck
 */
@Getter
@Setter
@AllArgsConstructor
public class AudioMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private AudioInputStream message;
    private String senderIpAdress;
}