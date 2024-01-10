package nl.han.compiler.ast.literals;

/**
 * All the possible literal types that expressions could evaluate to. Undefined should be used when the
 * literal type of expression cannot be determined.
 */
public enum LiteralType {
    BOOL,
    NUMERIC,
    UNDEFINED
}
