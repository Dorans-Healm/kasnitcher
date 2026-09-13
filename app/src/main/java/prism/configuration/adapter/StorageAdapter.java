package prism.configuration.adapter;

import lombok.Getter;
import lombok.Setter;
import prism.domain.model.Prism;

/**
 * Configuration settings for generic file storage operations.
 */
@Getter
@Setter
public class StorageAdapter {

    /**
     * The specific file name to use for storage operations.
     */
    private String file = "storage.json";

    /**
     * The target directory for storage operations.
     */
    private String directory = "~/.cache/prism/";
}