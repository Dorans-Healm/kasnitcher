package prism.domain.exception;

public class DaemonDownOnCommandException extends RuntimeException {

    public DaemonDownOnCommandException(String message) {
        super(message);
    }
}