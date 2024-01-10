package nl.han.compiler;

/**
 * This class represents exceptions that occur while compiling code.
 */
public class CompilerException extends RuntimeException {

    /**
     * Constructs a new CompilerException with the specified detail message.
     *
     * @param message The detail message describing the specific compiler-related error.
     */
    public CompilerException(String message) {
        super(message);
    }
}
