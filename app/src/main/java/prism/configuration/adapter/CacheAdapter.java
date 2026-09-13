package prism.configuration.adapter;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration settings for the caching mechanism.
 */
@Getter
@Setter
public class CacheAdapter {

    /**
     * The maximum number of items to retain in the cache.
     * Defaults to 3.
     */
    private Integer amount = 3;
}