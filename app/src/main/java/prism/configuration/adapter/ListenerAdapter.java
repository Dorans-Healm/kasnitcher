package prism.configuration.adapter;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration settings for the directory listener mechanism.
 */
@Getter
@Setter
public class ListenerAdapter {

    /**
     * The directory path to monitor for incoming changes.
     */
    // TODO - Affirm the correct socket dir
    private String directory = "/var/";
}