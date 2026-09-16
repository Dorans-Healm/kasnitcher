package prism.configuration.adapter;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration settings for the caching mechanism.
 */
@Getter
@Setter
public class CacheAdapter {

    public static final Integer AMOUNT = 3;

    public static final Integer KEEP_ALIVE = 10_000;

    /**
     * The maximum number of items to retain in the cache.
     * Defaults to 3.
     */
    private Integer amount = AMOUNT;

    private Integer timeout = KEEP_ALIVE;
}