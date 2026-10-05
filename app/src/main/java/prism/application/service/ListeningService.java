package prism.application.service;

import lombok.extern.java.Log;
import prism.application.component.SocketSubscriber;
import prism.configuration.context.AppContext;

import java.io.IOException;
import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;

@Log
public class ListeningService {

    private static final int MAX_ATTEMPTS = 3;
    private static final Duration RETRY_INTERVAL = Duration.ofSeconds(5);

    private final Supplier<SocketSubscriber> socketSubscriber;

    public ListeningService() {
        this.socketSubscriber =
                AppContext.getClassLazy(SocketSubscriber.class);
    }

    public void subscribeWithRetry() {
        for (int i = 1; i < MAX_ATTEMPTS; i++) {
            try {
                this.subscribe();
                return;
            } catch (RuntimeException e) {
                log.log(Level.SEVERE, ("Connection (%s) could " +
                        "not be stabilished while subscribing to socket").formatted(i), e);

                try {
                    Thread.sleep(RETRY_INTERVAL.toMillis());
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted during retry", ie);
                }
            }
        }

        throw new RuntimeException(
                "Listening to socket will not be possible");
    }

    public void subscribe() {
        try {
            this.socketSubscriber.get().subscribe();
        } catch (IOException e) {
            throw new RuntimeException("Error " +
                    "while trying to subscribe to socket", e);
        }
    }

    public void listen(Consumer<String> consumer) {
        try {
            this.socketSubscriber
                    .get().listen(consumer);
        } finally {
            this.socketSubscriber.get().close();
        }
    }
}