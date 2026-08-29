package shifter.ka.exception;

import lombok.Getter;
import shifter.ka.application.daemon.SocketStatusType;

public class SocketInterruptionException extends RuntimeException {

    @Getter
    private final SocketStatusType socketStatusType;

    public SocketInterruptionException(String message, SocketStatusType socketStatusType) {
        super(message);
        this.socketStatusType = socketStatusType;
    }
}