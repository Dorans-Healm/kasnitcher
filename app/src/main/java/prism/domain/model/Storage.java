package prism.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents the storage details for a file within the system.
 * Contains information about the file name and its directory path.
 */
@Getter
@AllArgsConstructor
public class Storage {

    /**
     * The name of the stored file.
     */
    private String file;

    /**
     * The directory path where the file is stored.
     */
    private String dir;
}