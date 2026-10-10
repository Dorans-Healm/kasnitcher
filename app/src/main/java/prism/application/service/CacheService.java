package prism.application.service;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import prism.application.component.ColorCache;
import prism.configuration.context.AppContext;
import prism.domain.model.Prism;

import java.util.function.Supplier;

/**
 * Application service responsible for managing the application's color cache.
 * It interacts with the {@link ColorCache} to store and retrieve previously generated
 * {@link Prism} color palettes.
 */
public class CacheService {

    private final Supplier<ColorCache> colorCache;

    /**
     * Constructs a new {@code CacheService}, initializing its dependency
     * lazily via the global {@link AppContext}.
     */
    public CacheService() {
        this.colorCache = AppContext
                .getClassLazy(ColorCache.class);
    }

    /**
     * Adds the specified file path and its corresponding {@link Prism} to the cache.
     * Ensures the cache watcher is started before adding the entry.
     *
     * @param filePath the path to the image file, used as the cache key
     * @param prism the generated color spectrum to cache
     */
    public void add(@NonNull String filePath, @NonNull Prism prism) {
        ColorCache cache = colorCache.get();

        if (!cache.isStarted()) {
            cache.watch();
        }

        cache.add(filePath, prism);
    }

    /**
     * Retrieves a cached {@link Prism} color spectrum for the given file path.
     *
     * @param filePath the path to the image file
     * @return the cached {@link Prism}, or {@code null} if not found in the cache
     */
    public @Nullable Prism get(@NonNull String filePath) {
        return colorCache.get().get(filePath);
    }
}