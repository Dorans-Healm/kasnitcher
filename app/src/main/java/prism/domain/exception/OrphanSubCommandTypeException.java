package prism.domain.exception;

import org.jspecify.annotations.NonNull;

/**
 * Thrown when a sub-command is encountered that does not belong to any registered parent
 * command.
 */
public class OrphanSubCommandTypeException extends RuntimeException {

    /**
     * @param message a description of the orphaned sub-command
     */
    public OrphanSubCommandTypeException(@NonNull String message) {
        super(message);
    }
}