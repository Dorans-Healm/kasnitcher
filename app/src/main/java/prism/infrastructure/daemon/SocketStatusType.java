package prism.infrastructure.daemon;

public enum SocketStatusType {

    ACTIVATING,

    READY,

    GRACEFUL_INTERRUPTION,

    FORCEFUL_INTERRUPTION
}