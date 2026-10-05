package prism.application.service;

import prism.application.component.SocketSubscriber;
import prism.configuration.context.AppContext;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ListeningService {

    private static final int MAX_ATTEMPTS = 3;
    private static final Duration RETRY_INTERVAL = Duration.ofSeconds(5);

    private final Supplier<SocketSubscriber> socketSubscriber;

    private final ScheduledExecutorService scheduler;

    public ListeningService() {
        this.socketSubscriber =
                AppContext.getClassLazy(SocketSubscriber.class);

        this.scheduler = Executors
                .newSingleThreadScheduledExecutor();
    }

    public void subscribeWithRetry() {
        this.subscribeWithRetry(1);
    }


    private void subscribeWithRetry(int attempt) {
        try {
            this.subscribe();
        } catch (RuntimeException e) {
            if (attempt >= MAX_ATTEMPTS) {
                throw new RuntimeException("Listening to socket will not be possible", e);
            }

            scheduler.schedule(
                    () -> subscribeWithRetry(attempt + 1),
                    RETRY_INTERVAL.toSeconds(),
                    TimeUnit.SECONDS);
        }
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