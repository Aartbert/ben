package nl.han.exceptions.bootstrap;

/**
 * This is an exception class that is thrown when a http request fails.
 *
 * @author Dylan Buil
 */
public class HTTPRequestFailed extends RuntimeException {
    public HTTPRequestFailed(String message) {
        super(message);
    }
}
