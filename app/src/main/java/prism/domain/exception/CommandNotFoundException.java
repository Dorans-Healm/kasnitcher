package prism.domain.exception;

import org.jspecify.annotations.NonNull;

/**
 * Thrown when a requested command cannot be resolved to a known handler.
 */
public class CommandNotFoundException extends RuntimeException {

    /**
     * @param message a description of which command was not found
     */
    public CommandNotFoundException(@NonNull String message) {
        super(message);
    }
}