package prism.configuration.adapter;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration settings for the directory listener mechanism.
 */
@Getter
@Setter
public class ListenerAdapter {

    public static final String FILE = "gbx.socket";

    public static final String DIR = "/tmp";

    /**
     * The specific file name to use subscribe to sockets.
     */
    private String file = FILE;

    /**
     * The directory path to monitor for incoming changes.
     */
    private String directory = DIR;
}