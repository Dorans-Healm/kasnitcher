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

    public static final Integer KEEP_ALIVE = 60;

    /**
     * The maximum number of items to retain in the cache. Defaults to 3.
     */
    private Integer amount = AMOUNT;

    /**
     * Timeout to keep a single cache item alive. Defaults to 60 seconds
     */
    private Integer timeout = KEEP_ALIVE;
}