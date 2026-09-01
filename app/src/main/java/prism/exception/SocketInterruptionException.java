package prism.exception;

import lombok.Getter;
import prism.unix.daemon.SocketStatusType;

public class SocketInterruptionException extends RuntimeException {

    @Getter
    private final SocketStatusType socketStatusType;

    public SocketInterruptionException(String message, SocketStatusType socketStatusType) {
        super(message);
        this.socketStatusType = socketStatusType;
    }
}