package prism.domain.exception;

import org.jspecify.annotations.NonNull;

/**
 * Thrown when a required argument for a command or operation cannot be found or is not provided.
 */
public class ArgumentNotFoundException extends RuntimeException {

    /**
     * @param message a description of the argument that was not found
     */
    public ArgumentNotFoundException(@NonNull String message) {
        super(message);
    }
}