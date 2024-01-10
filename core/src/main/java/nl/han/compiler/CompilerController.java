package nl.han.compiler;

/**
 * This class implements the {@link ICompiler compiler} interface.
 */
public class CompilerController implements ICompiler {

    private final CompilerService service = new CompilerService();

    /**
     * {@inheritDoc}
     */
    @Override
    public IAgent compileHandler(String input) {
        return service.compileHandler(input);
    }
}
