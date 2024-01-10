package nl.han.shared.datastructures;

/**
 * A lobby class that contains the name, type and max players of a lobby
 * 
 * @author Lars Meijerink, Vasil Verdouw
 */
public record Lobby(String name, String type, int MaxPlayers) {
    /**
     * Represents a lobby as a string in the format: name: {name} type: {type} max:
     * {max}
     * 
     * @author Vasil Verdouw
     */
    @Override
    public String toString() {
        return "name: " + name + " type: " + type + " max: " + MaxPlayers;
    }
}