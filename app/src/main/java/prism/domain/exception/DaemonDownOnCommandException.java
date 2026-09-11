package prism.domain.exception;

import lombok.NonNull;

/**
 * Thrown when a command is issued while the daemon process is not running.
 */
public class DaemonDownOnCommandException extends RuntimeException {

    /**
     * @param message a description of the command that failed due to the daemon being down
     */
    public DaemonDownOnCommandException(@NonNull String message) {
        super(message);
    }
}