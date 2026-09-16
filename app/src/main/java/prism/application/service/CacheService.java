package prism.application.service;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import prism.application.component.ColorCache;
import prism.configuration.context.AppContext;
import prism.domain.model.Prism;

import java.util.function.Supplier;

public class CacheService {

    private final Supplier<ColorCache> colorCache;

    public CacheService() {
        this.colorCache = AppContext
                .getClassLazy(ColorCache.class);
    }

    public void add(@NonNull String filePath, @NonNull Prism prism) {
        ColorCache cache = colorCache.get();

        if (!cache.isStarted()) {
            cache.watch();
        }

        cache.add(filePath, prism);
    }

    public @Nullable Prism get(@NonNull String filePath) {
        return colorCache.get().get(filePath);
    }
}