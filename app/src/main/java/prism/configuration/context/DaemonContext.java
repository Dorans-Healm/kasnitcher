package prism.configuration.context;

import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NonNull;
import prism.adapter.cli.input.Command;
import prism.configuration.adapter.*;

/**
 * Configuration context for the long-running background daemon process.
 * <p>
 * Holds the parsed adapter settings required by the daemon to perform
 * caching, storage, writing, and listening operations.
 */
@Getter
@Setter
public class DaemonContext extends AbstractServiceContextConfiguration {

    private WriterAdapter writerAdapter = new WriterAdapter();

    private ListenerAdapter listenerAdapter;

    private StorageAdapter storageAdapter;

    private CacheAdapter cacheAdapter;

    private ArgumentAdapter argumentAdapter;

    /**
     * Updates the daemon's internal adapter configurations based on the
     * provided CLI commands.
     *
     * @param commands an array of parsed {@link Command}s from the CLI
     * @throws IllegalArgumentException if an unsupported command is encountered
     */
    @Override
    public void updateConfiguration(@NonNull Command[] commands) {
        for (Command command : commands) {
            String[] subCommands = command.getSubCommands();

            switch (command.getCommand()) {
                case WRITE -> this.setWriterAdapter(
                        super.getWriterAdapter(this.writerAdapter, subCommands));

                case CACHE -> this.setCacheAdapter(
                        super.getCacheAdapter(this.cacheAdapter, subCommands));

                case STORE -> this.setStorageAdapter(
                        super.getStorageAdapter(this.storageAdapter, subCommands));

                case LISTEN -> this.setListenerAdapter(
                        super.getListenerAdapter(this.listenerAdapter, subCommands));

                case ARGUMENT -> this.setArgumentAdapter(
                        super.getArgumentAdapter(subCommands));

                case null, default -> throw new IllegalArgumentException(
                        "No configuration found for %s specified value.".formatted(command.getCommand()));
            }
        }
    }

    @Override
    public ArgumentAdapter getArgumentAdapter() {
        ArgumentAdapter argumentAdapter = this.argumentAdapter;
        this.argumentAdapter = null;

        return argumentAdapter;
    }
}