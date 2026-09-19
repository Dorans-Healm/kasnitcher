package prism.domain.exception;

import org.jspecify.annotations.NonNull;

public class ArgumentNotFoundException extends  RuntimeException {

    public ArgumentNotFoundException(@NonNull String message) {
        super(message);
    }
}