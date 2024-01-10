package nl.han.compiler;

/**
 * This interface provides methods that should be used by external components to communicate with the compiler. In case
 * a String of instructions is compiled with the intentions of repeated use, this interface provides a different interface
 * that is responsible for repeatedly handling the {@link IAgent agent}.
 */
public interface ICompiler {

    /**
     * Compiles a {@link IAgent agent} based on the provided input String. The input should be a String that contains
     * conditional instructions.
     *
     * @param input The input String containing the instructions
     * @return An instance of the compiled {@link IAgent agent}
     * @throws CompilerException if there is an error during compilation
     */
    IAgent compileHandler(String input);
}
