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

public abstract class AbstractServiceContextConfiguration {

    public abstract void updateConfiguration(Command[] commands);

    protected WriterAdapter getWriterAdapter(String[] subCommands) {
        WriterAdapter writerAdapter = new WriterAdapter();

        for (int i = 0; i < subCommands.length; i++) {
            String command = subCommands[i];

            AppSubCommandType subCommand = AppSubCommandType.getByCommand(command);
            if (Objects.isNull(subCommand)) {
                throw new IllegalArgumentException(
                        "%s is not a valid Sub command.".formatted(command));
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
                    String type  = subCommands[i + 1];

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

    protected CacheAdapter getCacheAdapter(String[] subCommands) {
        CacheAdapter cacheAdapter = new CacheAdapter();

        for (int i = 0; i < subCommands.length; i++) {
            String command = subCommands[i];

            AppSubCommandType subCommand = AppSubCommandType.getByCommand(command);
            if (Objects.isNull(subCommand)) {
                throw new IllegalArgumentException(
                        "%s is not a valid Sub command.".formatted(command));
            }

            if (ArrayUtils.contains(AMOUNT.getSubCommands(), command)) {
                if (!AMOUNT.isNullable()) {
                    try {
                        cacheAdapter.setAmount(Integer.parseInt(subCommands[i + 1]));
                        i++;
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException(
                                "%s is not a valid Amount.".formatted(command));
                    }
                }
            }
        }

        return cacheAdapter;
    }

    protected StorageAdapter getStorageAdapter(String[] subCommands) {
        StorageAdapter storageAdapter = new StorageAdapter();

        for (int i = 0; i < subCommands.length; i++) {
            String command = subCommands[i];

            AppSubCommandType subCommand = AppSubCommandType.getByCommand(command);
            if (Objects.isNull(subCommand)) {
                throw new IllegalArgumentException(
                        "%s is not a valid Sub command.".formatted(command));
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

    protected ListenerAdapter getListenerAdapter(String[] subCommands) {
        ListenerAdapter listenerAdapter = new ListenerAdapter();

        for (int i = 0; i < subCommands.length; i++) {
            String command = subCommands[i];

            AppSubCommandType subCommand = AppSubCommandType.getByCommand(command);
            if (Objects.isNull(subCommand)) {
                throw new IllegalArgumentException(
                        "%s is not a valid Sub command.".formatted(command));
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
}