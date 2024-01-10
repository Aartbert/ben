package nl.han;

import javax.sound.sampled.AudioInputStream;

public interface INetworkListener {
    void receiveChatMessage(String message);
    void receiveAudioMessage(AudioInputStream message, String senderIpAdress);
    void receiveGameState(String gamestate);
    void receiveFirstMessage(String gamestate);
    void sendFirstMessage();
}
