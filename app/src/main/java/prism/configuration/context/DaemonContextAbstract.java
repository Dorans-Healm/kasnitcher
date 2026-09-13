package prism.configuration.context;

import lombok.Getter;
import lombok.Setter;
import prism.adapter.cli.input.Command;
import prism.configuration.adapter.CacheAdapter;
import prism.configuration.adapter.ListenerAdapter;
import prism.configuration.adapter.StorageAdapter;
import prism.configuration.adapter.WriterAdapter;

/**
 * Configuration context for the long-running background daemon process.
 * <p>
 * Holds the parsed adapter settings required by the daemon to perform
 * caching, storage, writing, and listening operations.
 */
@Getter
@Setter
public class DaemonContextAbstract extends AbstractServiceContextConfiguration {

    private WriterAdapter writingConfiguration;

    private ListenerAdapter listenerConfiguration;

    private StorageAdapter storageConfiguration;

    private CacheAdapter cacheAdapter;

    /**
     * Updates the daemon's internal adapter configurations based on the
     * provided CLI commands.
     *
     * @param commands an array of parsed {@link Command}s from the CLI
     * @throws IllegalArgumentException if an unsupported command is encountered
     */
    public void updateConfiguration(Command[] commands) {
        for (Command command : commands) {
            switch (command.getCommand()) {
                case CACHE -> this.setCacheAdapter(
                        super.getCacheAdapter(command.getSubCommands()));

                case STORE -> this.setStorageConfiguration(
                        super.getStorageAdapter(command.getSubCommands()));

                case WRITE -> this.setWritingConfiguration(
                        super.getWriterAdapter(command.getSubCommands()));

                case LISTEN -> this.setListenerConfiguration(
                        super.getListenerAdapter(command.getSubCommands()));

                case null, default -> throw new IllegalArgumentException(
                        "No configuration found for %s specified value.".formatted(command.getCommand()));
            }
        }
    }
}