package prism.application.component;

import lombok.Getter;
import lombok.extern.java.Log;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import prism.configuration.adapter.CacheAdapter;
import prism.configuration.context.AbstractServiceContextConfiguration;
import prism.domain.model.Prism;

import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Log
public class ColorCache {

    private static final int WATCHER_WAIT_TIME = 5_000;

    private static final int TOTAL_PROPERTIES = 3;

    private final Supplier<? extends AbstractServiceContextConfiguration> appExecutionContext;

    private final ScheduledExecutorService scheduler;

    private Object[][] cache;

    private ScheduledFuture<?> watchTask;

    @Getter
    private boolean started;

    public ColorCache(@NonNull Supplier<? extends AbstractServiceContextConfiguration> appExecutionContext) {
        this.appExecutionContext = appExecutionContext;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(
                Thread.ofVirtual().factory()
        );
        this.cache = new Object[this.getConfiguredAmount()][TOTAL_PROPERTIES];
    }

    /**
     * Resizes the cache to the currently configured amount, keeping as many existing
     * entries as fit. Needed because the configuration can change after the initial
     * {@link CacheAdapter#AMOUNT}.
     */
    private void resizeIfNeeded() {
        int amount = this.getConfiguredAmount();

        if (this.cache.length == amount) {
            return;
        }

        Object[][] resized = new Object[amount][TOTAL_PROPERTIES];

        if (amount >= this.cache.length) {
            System.arraycopy(this.cache, 0, resized, 0, this.cache.length);
        } else {
            Object[][] sorted = this.cache.clone();

            Arrays.sort(sorted, Comparator.comparing(
                    row -> (Instant) row[2],
                    Comparator.nullsLast(Comparator.reverseOrder())
            ));

            System.arraycopy(sorted, 0, resized, 0, amount);
        }

        this.cache = resized;
    }

    private int getConfiguredAmount() {
        CacheAdapter cacheAdapter =
                this.appExecutionContext.get().getCacheAdapter();

        Integer amount = CacheAdapter.AMOUNT;

        if (Objects.nonNull(cacheAdapter)) {
            amount = cacheAdapter.getAmount();
        }

        if (amount < 1) {
            throw new IllegalArgumentException(
                    "Cache amount must be at least 1, got %d".formatted(amount));
        }

        return amount;
    }

    public void watch() {
        if (this.started) {
            return;
        }

        CacheAdapter cacheAdapter =
                this.appExecutionContext.get().getCacheAdapter();

        Integer keepAlive = CacheAdapter.KEEP_ALIVE;

        if (Objects.nonNull(cacheAdapter)) {
            keepAlive = cacheAdapter.getTimeout();
        }

        final Integer timeout = keepAlive;

        this.started = true;

        this.watchTask = this.scheduler.scheduleAtFixedRate(
                () -> this.removeExpired(timeout), 0, WATCHER_WAIT_TIME, TimeUnit.MILLISECONDS);
    }

    public synchronized void unwatch() {
        if (this.watchTask == null || this.watchTask.isCancelled()) {
            log.warning("Trying to stop cache watcher " +
                    "while watcher is not started. Request will be ignored");
            return;
        }

        this.started = false;

        this.watchTask.cancel(true);
    }

    private synchronized void removeExpired(@NonNull Integer timeout) {
        Instant now = Instant.now();

        for (Object[] cache : this.cache) {
            if (cache[2] == null) {
                continue;
            }

            Instant expiration =
                    ((Instant) cache[2]).plusSeconds(timeout);

            if (!expiration.isAfter(now)) {
                Arrays.fill(cache, null);
            }
        }

        boolean hasAny = false;

        for (Object[] cache : this.cache) {
            if (cache[2] != null) {
                hasAny = true;
                break;
            }
        }

        if (!hasAny) {
            this.clear();
        }
    }

    public synchronized void add(@NonNull String filePath, @NonNull Prism prism) {
        if (filePath.isEmpty()) {
            throw new IllegalArgumentException(
                    "File path can not be empty while using cache"
            );
        }

        this.resizeIfNeeded();

        int index = this.lastUsed(this.cache);

        this.cache[index] =
                new Object[]{filePath, prism, Instant.now()};
    }

    public synchronized @Nullable Prism get(@NonNull String filePath) {
        if (filePath.isEmpty()) {
            throw new IllegalArgumentException(
                    "File path can not be empty while using cache");
        }

        for (int i = 0; i < this.cache.length; i++) {
            if (this.cache[i][0] != null && this.cache[i][0].equals(filePath)) {
                this.cache[i][2] = Instant.now();
                return (Prism) this.cache[i][1];
            }
        }

        return null;
    }

    public synchronized void clear() {
        this.cache = new Object[this.cache.length][3];
    }

    private int lastUsed(@NonNull Object[][] array) {
        int lastUsedIndex = 0;

        for (int i = 0; i < array.length; i++) {
            if (array[i][2] == null) {
                return i;
            }

            if (array[i][2] instanceof Instant current
                    && array[lastUsedIndex][2] instanceof Instant oldest
                    && current.isBefore(oldest)) {

                lastUsedIndex = i;
            }
        }

        return lastUsedIndex;
    }
}