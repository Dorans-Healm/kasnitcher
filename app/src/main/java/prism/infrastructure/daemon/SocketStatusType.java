package prism.infrastructure.daemon;

/**
 * Represents the lifecycle states of a daemon socket server.
 * <p>
 * The server transitions through these states from startup to shutdown, allowing consumers
 * to distinguish between normal operation and different shutdown modes.
 */
public enum SocketStatusType {

    /**
     * The server is initializing and not yet accepting connections.
     */
    ACTIVATING,

    /**
     * The server is fully started and accepting connections.
     */
    READY,

    /**
     * A clean shutdown has been requested; in-flight work should finish before closing.
     */
    GRACEFUL_INTERRUPTION,

    /**
     * An immediate shutdown has been requested or sent from an unhandled error; connections
     * are terminated without draining.
     */
    FORCEFUL_INTERRUPTION
}