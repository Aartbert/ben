package nl.han.threephasecommit;

/**
 * The {@code OperationType} enumeration represents the different types of operations that can be part
 * of a Three-Phase Commit protocol in a distributed system. Each enum constant corresponds to a specific
 * phase or action within the Three-Phase Commit process.
 * <p>
 * Example usage:
 * ```java
 * OperationType operation = OperationType.PROPOSAL;
 * ```
 *
 * @author Dylan Buil
 */
public enum OperationType {
    INITIALIZATION,
    PROPOSAL,
    FINALIZE_COMMIT,
    CAN_COMMIT,
    ACK,
    ABORT,
    SUCCESS,
    FAILURE,
    BUSY
}
