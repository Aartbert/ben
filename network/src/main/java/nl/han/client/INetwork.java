package nl.han.client;


import nl.han.INetworkListener;

import javax.sound.sampled.AudioInputStream;
import java.util.List;

public interface INetwork {
    void hostLobby(String name);
    void joinLobby(String name);
    void start(String username);
    void sendChatMessage(String message, List<String> ips);
    void sendAudioInputStream(AudioInputStream audioInputStream, List<String> ips);
    void addNetworkListener(INetworkListener listener);
    void sendGame(String game);
    void sendFirstMessage(String game);
}
