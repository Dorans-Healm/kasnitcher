package prism.configuration.context;

import jdk.dynalink.NoSuchDynamicMethodException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import prism.adapter.cli.AppSubCommandType;
import prism.adapter.cli.input.Command;
import prism.configuration.adapter.*;
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
     * Retrieves the {@link WriterAdapter} if supported by the context. Default
     * implementation returns {@code null}.
     *
     * @return the writer adapter, or {@code null} if unsupported
     */
    public @Nullable WriterAdapter getWriterAdapter() {
        return null;
    }

    /**
     * Parses sub-commands to construct a {@link WriterAdapter}.
     *
     * @param subCommands an array of raw sub-commands
     * @return a populated {@link WriterAdapter}
     * @throws IllegalArgumentException if an invalid sub-command or argument is provided
     */
    protected @NonNull WriterAdapter getWriterAdapter(@Nullable WriterAdapter adapter, @NonNull String[] subCommands) {
        WriterAdapter writerAdapter = adapter == null
                ? new WriterAdapter()
                : adapter;

        for (int i = 0; i < subCommands.length; i++) {
            String command = subCommands[i];

            this.assertValidSubCommand(command);

            if (isReset(command)) {
                writerAdapter = new WriterAdapter();
                continue;
            }

            if (ArrayUtils.contains(DIRECTORY.getSubCommands(), command)) {
                if (!DIRECTORY.isNullable()) {
                    String dir = subCommands[i + 1];
                    i++;

                    Boolean isValidDir = FileUtils.isValidDirectory(dir);
                    if (!isValidDir) {
                        throw new IllegalArgumentException("%s is not a valid directory.".formatted(dir));
                    }

                    writerAdapter.setDirectory(dir);
                    continue;
                }
            }

            if (ArrayUtils.contains(FILE.getSubCommands(), command)) {
                if (!FILE.isNullable()) {
                    writerAdapter.setFile(subCommands[i + 1]);
                    i++;
                    continue;
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
     * Retrieves the {@link CacheAdapter} if supported by the context. Default
     * implementation returns {@code null}.
     *
     * @return the cache adapter, or {@code null} if unsupported
     */
    public @Nullable CacheAdapter getCacheAdapter() {
        return null;
    }

    /**
     * Parses sub-commands to construct a {@link CacheAdapter}.
     *
     * @param subCommands an array of raw sub-commands
     * @return a populated {@link CacheAdapter}
     * @throws IllegalArgumentException if an invalid amount or sub-command is provided
     */
    protected @Nullable CacheAdapter getCacheAdapter(@Nullable CacheAdapter adapter, @NonNull String[] subCommands) {
        CacheAdapter cacheAdapter = adapter == null
                ? new CacheAdapter()
                : adapter;

        for (int i = 0; i < subCommands.length; i++) {
            String command = subCommands[i];

            this.assertValidSubCommand(command);

            if (isInterruption(command)) {
                return null;
            }

            if (isReset(command)) {
                cacheAdapter = new CacheAdapter();
                continue;
            }

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
     * Retrieves the {@link StorageAdapter} if supported by the context. Default
     * implementation returns {@code null}.
     *
     * @return the storage adapter, or {@code null} if unsupported
     */
    protected @Nullable StorageAdapter getStorageAdapter() {
        return null;
    }

    /**
     * Parses sub-commands to construct a {@link StorageAdapter}.
     *
     * @param subCommands an array of raw sub-commands
     * @return a populated {@link StorageAdapter}
     * @throws IllegalArgumentException if an invalid directory or sub-command is provided
     */
    protected @Nullable StorageAdapter getStorageAdapter(@Nullable StorageAdapter adapter, @NonNull String[] subCommands) {
        StorageAdapter storageAdapter = adapter == null
                ? new StorageAdapter()
                : adapter;

        for (int i = 0; i < subCommands.length; i++) {

            String command = subCommands[i];

            this.assertValidSubCommand(command);

            if (isInterruption(command)) {
                return null;
            }

            if (isReset(command)) {
                storageAdapter = new StorageAdapter();
                continue;
            }

            if (ArrayUtils.contains(DIRECTORY.getSubCommands(), command)) {
                if (!DIRECTORY.isNullable()) {
                    String dir = subCommands[i + 1];
                    i++;

                    Boolean isValidDir = FileUtils.isValidDirectory(dir);
                    if (!isValidDir) {
                        throw new IllegalArgumentException("%s is not a valid directory.".formatted(dir));
                    }

                    storageAdapter.setDirectory(dir);
                    continue;
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
     * Retrieves the {@link ListenerAdapter} if supported by the context. Default
     * implementation returns {@code null}.
     *
     * @return the listener adapter, or {@code null} if unsupported
     */
    public @Nullable ListenerAdapter getListenerAdapter() {
        return null;
    }

    /**
     * Parses sub-commands to construct a {@link ListenerAdapter}.
     *
     * @param subCommands an array of raw sub-commands
     * @return a populated {@link ListenerAdapter}
     * @throws IllegalArgumentException if an invalid directory or sub-command is provided
     */
    protected @Nullable ListenerAdapter getListenerAdapter(@Nullable ListenerAdapter adapter, @NonNull String[] subCommands) {
        ListenerAdapter listenerAdapter = adapter == null
                ? new ListenerAdapter()
                : adapter;

        for (int i = 0; i < subCommands.length; i++) {
            String command = subCommands[i];

            this.assertValidSubCommand(command);

            if (isInterruption(command)) {
                return null;
            }

            if (isReset(command)) {
                listenerAdapter = new ListenerAdapter();
                continue;
            }

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

    public @Nullable ArgumentAdapter getArgumentAdapter() {
        throw new NoSuchDynamicMethodException("Argument adapter " +
                "method should be created for each individual execution context.");
    }

    protected @NonNull ArgumentAdapter getArgumentAdapter(@NonNull String[] subCommands) {
        ArgumentAdapter argumentAdapter = new ArgumentAdapter();

        for (int i = 0; i < subCommands.length; i++) {
            String command = subCommands[i];

            this.assertValidSubCommand(command);

            if (ArrayUtils.contains(FILE.getSubCommands(), command)) {
                if (!FILE.isNullable()) {
                    argumentAdapter.setFile(subCommands[i + 1]);
                    i++;
                }
            }
        }

        return argumentAdapter;
    }

    /**
     * Validates if a subCommand is actually a subCommand, or else, throws.
     *
     * @param subCommandStr String containing the supposed subCommand
     * @throws IllegalArgumentException If command is not valid
     */
    private void assertValidSubCommand(@NonNull String subCommandStr) {
        AppSubCommandType subCommand = AppSubCommandType.getByCommand(subCommandStr);
        if (Objects.isNull(subCommand)) {
            throw new IllegalArgumentException(
                    "%s is not a valid Sub command.".formatted(subCommandStr));
        }
    }

    private boolean isInterruption(String subCommand) {
        return ArrayUtils.contains(INTERRUPT.getSubCommands(), subCommand);
    }

    private boolean isReset(String subCommand) {
        return ArrayUtils.contains(RESET.getSubCommands(), subCommand);
    }
}