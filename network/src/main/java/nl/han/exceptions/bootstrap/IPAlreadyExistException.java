package nl.han.exceptions.bootstrap;

/**
 * This is an exception class that is thrown when already existing ip is added.
 * @author Davy Zhou
 * */
public class IPAlreadyExistException extends Exception {
    public IPAlreadyExistException(String ipAddress) {
        super("List already contains IP: " + ipAddress);
    }
}
