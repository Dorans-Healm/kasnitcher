package prism.domain.exception;

import lombok.Getter;
import lombok.NonNull;
import prism.infrastructure.daemon.SocketStatusType;

/**
 * Thrown when a socket server is interrupted during connection handling.
 * <p>
 * Carries the {@link SocketStatusType} that was active at the time of interruption,
 * allowing callers to distinguish between graceful and forceful shutdowns.
 */
public class SocketInterruptionException extends RuntimeException {

    /**
     * The server status at the time the interruption occurred.
     */
    @Getter
    private final SocketStatusType socketStatusType;

    /**
     * @param message          a description of the interruption
     * @param socketStatusType the server status when the interruption occurred
     */
    public SocketInterruptionException(@NonNull String message, @NonNull SocketStatusType socketStatusType) {
        super(message);
        this.socketStatusType = socketStatusType;
    }
}