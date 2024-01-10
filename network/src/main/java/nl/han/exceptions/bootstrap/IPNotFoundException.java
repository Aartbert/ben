package nl.han.exceptions.bootstrap;

/**
 * This is an exception class that is thrown when ip is not found.
 * @author Dylan Buil
 * */
public class IPNotFoundException extends Exception {
    public IPNotFoundException(String ipAddress) {
        super("IP Not Found: " + ipAddress);
    }
}
