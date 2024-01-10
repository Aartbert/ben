package nl.han.compiler;

import nl.han.IAMLexer;
import nl.han.IAMParser;
import nl.han.compiler.ast.Configuration;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.BitSet;

/**
 * This class is responsible for parsing, checking and transforming Strings.
 */
public class CompilerService implements ANTLRErrorListener {

    /**
     * Compiles a {@link IAgent agent} from a {@link String} of commands.
     *
     * @param input A {@link String} of commands
     * @return A {@link IAgent agent}
     */
    public IAgent compileHandler(String input) {
        Configuration ast = parseString(input);

        ast.check();
        ast.transform();

        return ast;
    }

    /**
     * Parses the input string using the ANTLR lexer and parser to generate an Abstract Syntax Tree (AST).
     *
     * @param input The input string to be parsed.
     * @return An instance of the AST representing the parsed structure.
     * @throws CompilerException if there is an error during the parsing process.
     */
    private Configuration parseString(String input) {
        ASTListener listener = new ASTListener();

        CharStream inputStream = CharStreams.fromString(input);

        // Lex the input characters into tokens
        IAMLexer lexer = new IAMLexer(inputStream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(this);

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // Parse through the tokens
        IAMParser parser = new IAMParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(this);

        ParseTree tree = parser.configuration();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(listener, tree);

        return listener.getAst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object o, int i, int i1, String s, RecognitionException e) {
        throw new CompilerException("Something went wrong with the processing of the input. Does the sentence has an action and ends with a dot?");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reportAmbiguity(Parser parser, DFA dfa, int i, int i1, boolean b, BitSet bitSet, ATNConfigSet atnConfigSet) {
        // Not implemented yet. Task for the construction fase.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reportAttemptingFullContext(Parser parser, DFA dfa, int i, int i1, BitSet bitSet, ATNConfigSet atnConfigSet) {
        // Not implemented yet. Task for the construction fase.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reportContextSensitivity(Parser parser, DFA dfa, int i, int i1, int i2, ATNConfigSet atnConfigSet) {
        // Not implemented yet. Task for the construction fase.
    }
}

