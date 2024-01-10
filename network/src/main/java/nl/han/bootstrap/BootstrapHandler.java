package nl.han.bootstrap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.java.Log;
import nl.han.exceptions.bootstrap.LobbyNotFoundException;
import nl.han.shared.Lobby;
import nl.han.shared.RequestDTO;
import nl.han.shared.Peer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

/**
 * This class handles the communication with the bootstrapping server.
 * @author Dylan Buil
 */
@Log
public class BootstrapHandler {
    private static final String BASE_API_URL = "http://145.74.104.220:8080/game";

    public List<String> getLobbyNames() {
        Optional<ArrayList<Lobby>> lobbies = sendHttpRequestForLobbies();
        if(lobbies.isEmpty()) throw new LobbyNotFoundException();
        return lobbies.get().stream().map(Lobby::getName).toList();
    }

    /**
     * Joins a lobby on the bootstrapping server.
     * @return Lobby The lobby that was created.
     * @see RequestDTO
     * @see Lobby
     * @author Dylan Buil
     */
    public Lobby joinLobby(String lobbyName, Peer peer) {
        RequestDTO request = new RequestDTO(peer.getUserName(), lobbyName, peer.getIpWithPort());
        String apiUrl = BASE_API_URL + "/name/" + lobbyName;
        Optional<Lobby> lobby = sendHttpRequest(apiUrl, request, "POST");
        if(lobby.isEmpty()) throw new LobbyNotFoundException();
        return lobby.get();
    }

    /**
     * Creates a lobby on the bootstrapping server.
     * @param lobbyName The name of the lobby to be created.
     * @param peer The peer that creates the lobby.
     *
     * @see RequestDTO
     * @author Dylan Buil
     */
    public Lobby createLobby(String lobbyName, Peer peer) {
        RequestDTO request = new RequestDTO(peer.getUserName(), lobbyName, peer.getIpWithPort());
        Optional<Lobby> lobby = sendHttpRequest(BASE_API_URL, request, "POST");
        if(lobby.isEmpty()) throw new LobbyNotFoundException();
        return lobby.get();
    }

    /**
     * Sends an HTTP request to the bootstrapping server.
     * @param apiUrl The API URL to send the request to.
     * @param requestDTO The request data transfer object.
     * @param method The HTTP method to use.
     * @return Optional<Lobby> The lobby that was created.
     * @author Dylan Buil
     */
    private Optional<Lobby> sendHttpRequest(String apiUrl, RequestDTO requestDTO, String method) {
        HttpClient httpClient = HttpClient.newHttpClient();

        try {
            URI uri = URI.create(apiUrl);
            HttpRequest httpRequest = HttpRequest.newBuilder(uri)
                    .method(method, BodyPublishers.ofString(new Gson().toJson(requestDTO)))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                if (response.body().isEmpty()) {
                    return Optional.empty();
                }

                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.excludeFieldsWithoutExposeAnnotation().create();
                Lobby lobby = gson.fromJson(response.body(), Lobby.class);

                for (Peer peer : lobby.getPeers()) {
                    String[] ipPort = peer.getIpWithPort().split(":");
                    peer.setIpAddress(ipPort[0]);
                    peer.setPort(Integer.parseInt(ipPort[1]));
                }

                lobby.setHost(lobby.getHostInPeers());
                return Optional.of(lobby);
            } else {
                log.severe("Could not create lobby. Please try again.\n" + response);
            }
        } catch (IOException | InterruptedException | RuntimeException e) {
            // Won't interrupt the program if the request fails, because it will restart the request.
            log.log(Level.SEVERE, e, () -> "Could not start server socket: " + e.getMessage());
            Thread.currentThread().interrupt();
        }

        return Optional.empty();
    }

    private Optional<ArrayList<Lobby>> sendHttpRequestForLobbies() {
        HttpClient httpClient = HttpClient.newHttpClient();

        try {
            URI uri = URI.create(BASE_API_URL);
            HttpRequest httpRequest = HttpRequest.newBuilder(uri)
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                if (response.body().isEmpty()) {
                    return Optional.empty();
                }

                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.excludeFieldsWithoutExposeAnnotation().create();
                ArrayList<Lobby> lobbies = gson.fromJson(response.body(), new TypeToken<ArrayList<Lobby>>() {}.getType());

                return Optional.of(lobbies);
            } else {
                log.severe("Could not create lobby. Please try again.\n" + response);
            }
        } catch (IOException | RuntimeException | InterruptedException e) {
            // Won't interrupt the program if the request fails, because it will restart the request.
            log.log(Level.SEVERE, e, () -> "Could not start server socket: " + e.getMessage());
            Thread.currentThread().interrupt();
        }

        return Optional.empty();
    }
}