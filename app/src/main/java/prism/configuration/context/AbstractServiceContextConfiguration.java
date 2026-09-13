package prism.configuration.context;

import prism.adapter.cli.AppSubCommandType;
import prism.adapter.cli.input.Command;
import prism.configuration.adapter.CacheAdapter;
import prism.configuration.adapter.ListenerAdapter;
import prism.configuration.adapter.StorageAdapter;
import prism.configuration.adapter.WriterAdapter;
import prism.utils.ArrayUtils;
import prism.utils.FileUtils;

import java.util.Objects;

import static prism.adapter.cli.AppSubCommandType.*;

/**
 * Base class for configuration contexts that translate CLI commands into configuration
 * adapters.
 * <p>
 * Provides common parsing logic for converting raw CLI sub-commands into specific adapter
 * instances (Writer, Cache, Storage, Listener). Subclasses decide which adapters to expose
 * and manage.
 */
public abstract class AbstractServiceContextConfiguration {

    /**
     * Updates the context state by parsing and applying the provided CLI commands.
     *
     * @param commands the parsed commands to apply
     */
    public abstract void updateConfiguration(Command[] commands);

    /**
     * Parses sub-commands to construct a {@link WriterAdapter}.
     *
     * @param subCommands an array of raw sub-commands
     * @return a populated {@link WriterAdapter}
     * @throws IllegalArgumentException if an invalid sub-command or argument is provided
     */
    protected WriterAdapter getWriterAdapter(String[] subCommands) {
        WriterAdapter writerAdapter = new WriterAdapter();

        for (int i = 0; i < subCommands.length; i++) {
            String command = subCommands[i];

            this.assertValidSubCommand(command);

            if (ArrayUtils.contains(DIRECTORY.getSubCommands(), command)) {
                if (!DIRECTORY.isNullable()) {
                    String dir = subCommands[i + 1];
                    i++;

                    Boolean isValidDir = FileUtils.isValidDirectory(dir);
                    if (!isValidDir) {
                        throw new IllegalArgumentException("%s is not a valid directory.".formatted(dir));
                    }

                    writerAdapter.setDirectory(dir);
                }
            }

            if (ArrayUtils.contains(FILE.getSubCommands(), command)) {
                if (!FILE.isNullable()) {
                    writerAdapter.setFile(subCommands[i + 1]);
                    i++;
                }
            }

            if (ArrayUtils.contains(TYPE.getSubCommands(), command)) {
                if (!TYPE.isNullable()) {
                    String type = subCommands[i + 1];

                    if (!WriterAdapter.RGB.equals(type) && !WriterAdapter.HEX.equals(type)) {
                        throw new IllegalArgumentException(("%s is not a " +
                                "valid type for color writing.").formatted(type));
                    }

                    writerAdapter.setType(type);
                    i++;
                }
            }
        }

        return writerAdapter;
    }

    /**
     * Parses sub-commands to construct a {@link CacheAdapter}.
     *
     * @param subCommands an array of raw sub-commands
     * @return a populated {@link CacheAdapter}
     * @throws IllegalArgumentException if an invalid amount or sub-command is provided
     */
    protected CacheAdapter getCacheAdapter(String[] subCommands) {
        CacheAdapter cacheAdapter = new CacheAdapter();

        for (int i = 0; i < subCommands.length; i++) {
            String command = subCommands[i];

            this.assertValidSubCommand(command);

            if (ArrayUtils.contains(AMOUNT.getSubCommands(), command)) {
                if (!AMOUNT.isNullable()) {
                    String amount = subCommands[i + 1];

                    try {
                        cacheAdapter.setAmount(Integer.parseInt(amount));
                        i++;
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException(
                                "%s is not a valid amount.".formatted(amount));
                    }
                }
            }
        }

        return cacheAdapter;
    }

    /**
     * Parses sub-commands to construct a {@link StorageAdapter}.
     *
     * @param subCommands an array of raw sub-commands
     * @return a populated {@link StorageAdapter}
     * @throws IllegalArgumentException if an invalid directory or sub-command is provided
     */
    protected StorageAdapter getStorageAdapter(String[] subCommands) {
        StorageAdapter storageAdapter = new StorageAdapter();

        for (int i = 0; i < subCommands.length; i++) {
            String command = subCommands[i];

            this.assertValidSubCommand(command);

            if (ArrayUtils.contains(DIRECTORY.getSubCommands(), command)) {
                if (!DIRECTORY.isNullable()) {
                    String dir = subCommands[i + 1];
                    i++;

                    Boolean isValidDir = FileUtils.isValidDirectory(dir);
                    if (!isValidDir) {
                        throw new IllegalArgumentException("%s is not a valid directory.".formatted(dir));
                    }

                    storageAdapter.setDirectory(dir);
                }
            }

            if (ArrayUtils.contains(FILE.getSubCommands(), command)) {
                if (!FILE.isNullable()) {
                    storageAdapter.setFile(subCommands[i + 1]);
                    i++;
                }
            }
        }

        return storageAdapter;
    }

    /**
     * Parses sub-commands to construct a {@link ListenerAdapter}.
     *
     * @param subCommands an array of raw sub-commands
     * @return a populated {@link ListenerAdapter}
     * @throws IllegalArgumentException if an invalid directory or sub-command is provided
     */
    protected ListenerAdapter getListenerAdapter(String[] subCommands) {
        ListenerAdapter listenerAdapter = new ListenerAdapter();

        for (int i = 0; i < subCommands.length; i++) {
            String command = subCommands[i];

            this.assertValidSubCommand(command);

            if (ArrayUtils.contains(DIRECTORY.getSubCommands(), command)) {
                if (!DIRECTORY.isNullable()) {
                    String dir = subCommands[i + 1];
                    i++;

                    Boolean isValidDir = FileUtils.isValidDirectory(dir);
                    if (!isValidDir) {
                        throw new IllegalArgumentException("%s is not a valid directory.".formatted(dir));
                    }

                    listenerAdapter.setDirectory(dir);
                }
            }
        }

        return listenerAdapter;
    }

    /**
     * Validates if a subCommand is actually a subCommand, or else, throws.
     *
     * @param subCommandStr String containing the supposed subCommand
     * @throws IllegalArgumentException If command is not valid
     */
    private void assertValidSubCommand(String subCommandStr) {
        AppSubCommandType subCommand = AppSubCommandType.getByCommand(subCommandStr);
        if (Objects.isNull(subCommand)) {
            throw new IllegalArgumentException(
                    "%s is not a valid Sub command.".formatted(subCommandStr));
        }
    }
}