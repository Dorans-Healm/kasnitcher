package prism.application.component;

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

    public ColorCache(@NonNull Supplier<? extends AbstractServiceContextConfiguration> appExecutionContext) {
        this.appExecutionContext = appExecutionContext;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(Thread.ofVirtual().factory());
    }

    @PostConstruct
    private void setup() {
        CacheAdapter cacheAdapter =
                this.appExecutionContext.get().getCacheAdapter();

        Integer arrSize = CacheAdapter.AMOUNT;
        if (Objects.nonNull(cacheAdapter)) {
            arrSize = cacheAdapter.getAmount();
        }

        this.cache = new Object[arrSize][2];
    }

    public void watch() {
        CacheAdapter cacheAdapter =
                this.appExecutionContext.get().getCacheAdapter();

        Integer keepAlive = CacheAdapter.KEEP_ALIVE;
        if (Objects.nonNull(cacheAdapter)) {
            keepAlive = cacheAdapter.getTimeout();
        }

        final Integer timeout = keepAlive;

        this.scheduler.scheduleAtFixedRate(
                () -> this.removeExpired(timeout), 0, WATCHER_WAIT_TIME, TimeUnit.MILLISECONDS);
    }

    public synchronized void unwatch() {
        if (this.scheduler.isShutdown() || this.scheduler.isTerminated()) {
            log.warning("Trying to stop cache watcher " +
                    "while watcher is not started. Request will be ignored");
            return;
        }

        this.scheduler.shutdown();
    }

    private synchronized void removeExpired(@NonNull Integer timeout) {
        Instant now = Instant.now();

        for (Object[] cache : this.cache) {
            if (cache == null || cache[1] == null) {
                continue;
            }

            Instant expiration =
                    ((Instant) cache[1]).plusSeconds(timeout);

            if (expiration.isBefore(now)) {
                ArrayUtils.alter(this.cache, cache);
            }
        }
    }

    public synchronized void add(@NonNull String filePath, @NonNull Prism prism) {
        if (filePath.isEmpty()) {
            throw new IllegalArgumentException(
                    "File path can not be empty while using cache");
        }

        int lastUsedIndex =
                ArrayUtils.lastOccurrence(this.cache);

        if (lastUsedIndex == -1) {
            ArrayUtils.alter(
                    this.cache, this.lastUsed(this.cache));

            lastUsedIndex =
                    ArrayUtils.lastOccurrence(this.cache);
        }

        Object[] newItem = {filePath, prism};
        Instant instant = Instant.now();

        this.cache[lastUsedIndex] = new Object[]{newItem, instant};
    }

    public synchronized @Nullable Prism get(@NonNull String filePath) {
        if (filePath.isEmpty()) {
            throw new IllegalArgumentException(
                    "File path can not be empty while using cache");
        }

        for (Object[] cache : this.cache) {
            if (cache[0] != null && cache[0].equals(filePath)) {
                return (Prism) cache[1];
            }
        }

        return null;
    }

    public synchronized void clear() {
        this.cache = new Object[this.cache.length][2];
    }

    private int lastUsed(@NonNull Object[][] array) {
        int lastUsedIndex = 0;

        for (int i = 0; i < array.length; i++) {
            if (array[i][1] == null) {
                return i;
            }

            if (array[lastUsedIndex][1] == null
                    || ((Instant) array[i][1]).isBefore((Instant) array[lastUsedIndex][1])) {
                lastUsedIndex = i;
            }
        }

        return lastUsedIndex;
    }
}