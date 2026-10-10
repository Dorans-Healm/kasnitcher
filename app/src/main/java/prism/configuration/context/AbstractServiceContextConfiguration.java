package prism.configuration.context;

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
        WriterAdapter writerAdapter = new WriterAdapter();
        if (adapter != null) {
            writerAdapter.setFile(adapter.getFile());
            writerAdapter.setDirectory(adapter.getDirectory());
            writerAdapter.setType(adapter.getType());
        }

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

                    if (!WriterAdapter.RGB.equalsIgnoreCase(type) && !WriterAdapter.HEX.equalsIgnoreCase(type)) {
                        throw new IllegalArgumentException(("%s is not a " +
                                "valid type for color writing.").formatted(type));
                    }

                    writerAdapter.setType(type.toLowerCase());
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
        CacheAdapter cacheAdapter = new CacheAdapter();
        if (adapter != null) {
            cacheAdapter.setAmount(adapter.getAmount());
            cacheAdapter.setTimeout(adapter.getTimeout());
        }

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

            if (ArrayUtils.contains(KEEP_ALIVE.getSubCommands(), command)) {
                if (!KEEP_ALIVE.isNullable()) {
                    String timeout = subCommands[i + 1];

                    try {
                        cacheAdapter.setTimeout(Integer.parseInt(timeout));
                        i++;
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException(
                                "%s is not a valid timeout.".formatted(timeout));
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
    public @Nullable StorageAdapter getStorageAdapter() {
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
        StorageAdapter storageAdapter = new StorageAdapter();
        if (adapter != null) {
            storageAdapter.setFile(adapter.getFile());
            storageAdapter.setDirectory(adapter.getDirectory());
        }

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
        ListenerAdapter listenerAdapter = new ListenerAdapter();
        if (adapter != null) {
            listenerAdapter.setFile(adapter.getFile());
            listenerAdapter.setDirectory(adapter.getDirectory());
        }

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
                    continue;
                }
            }

            if (ArrayUtils.contains(FILE.getSubCommands(), command)) {
                if (!FILE.isNullable()) {
                    listenerAdapter.setFile(subCommands[i + 1]);
                    i++;
                }
            }
        }

        return listenerAdapter;
    }

    /**
     * Retrieves the {@link ParameterAdapter} if supported by the context. Default
     * implementation throws {@link UnsupportedOperationException}.
     *
     * @return the parameter adapter
     * @throws UnsupportedOperationException if not supported by the context
     */
    public @Nullable ParameterAdapter getParameterAdapter() {
        throw new UnsupportedOperationException("Parameter adapter " +
                "method should be created for each individual execution context.");
    }

    /**
     * Parses sub-commands to construct a {@link ParameterAdapter}.
     *
     * @param subCommands an array of raw sub-commands
     * @return a populated {@link ParameterAdapter}
     * @throws IllegalArgumentException if an invalid sub-command is provided
     */
    protected @NonNull ParameterAdapter getParameterAdapter(@NonNull String[] subCommands) {
        ParameterAdapter parameterAdapter = new ParameterAdapter();

        for (int i = 0; i < subCommands.length; i++) {
            String command = subCommands[i];

            this.assertValidSubCommand(command);

            if (ArrayUtils.contains(FILE.getSubCommands(), command)) {
                if (!FILE.isNullable()) {
                    parameterAdapter.setFile(subCommands[i + 1]);
                    i++;
                }
            }
        }

        return parameterAdapter;
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

    /**
     * Checks if the given sub-command indicates an interruption command.
     *
     * @param subCommand the sub-command to check
     * @return {@code true} if the sub-command is an interruption command, {@code false} otherwise
     */
    private boolean isInterruption(String subCommand) {
        return ArrayUtils.contains(INTERRUPT.getSubCommands(), subCommand);
    }

    /**
     * Checks if the given sub-command indicates a reset command.
     *
     * @param subCommand the sub-command to check
     * @return {@code true} if the sub-command is a reset command, {@code false} otherwise
     */
    private boolean isReset(String subCommand) {
        return ArrayUtils.contains(RESET.getSubCommands(), subCommand);
    }
}