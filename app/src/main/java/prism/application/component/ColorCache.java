package prism.application.component;

import lombok.Getter;
import lombok.extern.java.Log;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import prism.configuration.annotations.PostConstruct;
import prism.configuration.adapter.CacheAdapter;
import prism.configuration.context.AbstractServiceContextConfiguration;
import prism.domain.model.Prism;
import prism.utils.ArrayUtils;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Log
public class ColorCache {

    private static final Integer WATCHER_WAIT_TIME = 5_000;

    private final Supplier<? extends AbstractServiceContextConfiguration> appExecutionContext;

    private final ScheduledExecutorService scheduler;

    private Object[][] cache;

    @Getter
    private boolean started;

    public ColorCache(
            @NonNull Supplier<? extends AbstractServiceContextConfiguration> appExecutionContext) {

        this.appExecutionContext = appExecutionContext;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(
                Thread.ofVirtual().factory()
        );
    }

    @PostConstruct
    private void setup() {
        CacheAdapter cacheAdapter =
                this.appExecutionContext.get().getCacheAdapter();

        Integer arrSize = CacheAdapter.AMOUNT;

        if (Objects.nonNull(cacheAdapter)) {
            arrSize = cacheAdapter.getAmount();
        }

        this.cache = new Object[arrSize][3];
    }

    public void watch() {
        CacheAdapter cacheAdapter =
                this.appExecutionContext.get().getCacheAdapter();

        Integer keepAlive = CacheAdapter.KEEP_ALIVE;

        if (Objects.nonNull(cacheAdapter)) {
            keepAlive = cacheAdapter.getTimeout();
        }

        final Integer timeout = keepAlive;

        this.started = true;

        this.scheduler.scheduleAtFixedRate(
                () -> this.removeExpired(timeout), 0, WATCHER_WAIT_TIME, TimeUnit.MILLISECONDS);
    }

    public synchronized void unwatch() {
        if (this.scheduler.isShutdown()) {
            log.warning("Trying to stop cache watcher " +
                    "while watcher is not started. Request will be ignored");
            return;
        }

        this.started = false;

        this.scheduler.shutdown();
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
                ArrayUtils.alter(this.cache, cache);
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

        int index = ArrayUtils.lastOccurrence(this.cache);

        if (index == -1) {
            index = this.lastUsed(this.cache);
        }

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