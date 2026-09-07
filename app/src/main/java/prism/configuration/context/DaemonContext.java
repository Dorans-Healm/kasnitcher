package prism.configuration.context;

import lombok.Getter;
import lombok.Setter;
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

@Getter
@Setter
public class DaemonContext implements ServiceContextConfiguration {

    private WriterAdapter writingConfiguration;

    private ListenerAdapter listenerConfiguration;

    private StorageAdapter storageConfiguration;

    private CacheAdapter cacheAdapter;

    public void updateConfiguration(Command[] commands) {
        for (Command command : commands) {
            switch (command.getCommand()) {
                case CACHE -> this.setCacheAdapter(
                        this.getCacheAdapter(command.getSubCommands()));

                case STORE -> this.setStorageConfiguration(
                        this.getStorageAdapter(command.getSubCommands()));

                case WRITE -> this.setWritingConfiguration(
                        this.getWriterAdapter(command.getSubCommands()));

                case LISTEN -> this.setListenerConfiguration(
                        this.getListenerAdapter(command.getSubCommands()));

                case null, default -> throw new IllegalArgumentException(
                        "No configuration found for %s specified value.".formatted(command.getCommand()));
            }
        }
    }

    private CacheAdapter getCacheAdapter(String[] subCommands) {
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
                        cacheAdapter.setAmount(Integer.parseInt(subCommands[i++]));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException(
                                "%s is not a valid Amount.".formatted(command));
                    }
                }
            }
        }

        return cacheAdapter;
    }

    private StorageAdapter getStorageAdapter(String[] subCommands) {
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
                    String dir = subCommands[i++];

                    Boolean isValidDir = FileUtils.isValidDirectory(dir);
                    if (!isValidDir) {
                        throw new IllegalArgumentException("%s is not a valid directory.".formatted(dir));
                    }

                    storageAdapter.setDirectory(dir);
                }
            }

            if (ArrayUtils.contains(FILE.getSubCommands(), command)) {
                if (!FILE.isNullable()) {
                    storageAdapter.setFile(subCommands[i++]);
                }
            }
        }

        return storageAdapter;
    }

    private WriterAdapter getWriterAdapter(String[] subCommands) {
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
                    String dir = subCommands[i++];

                    Boolean isValidDir = FileUtils.isValidDirectory(dir);
                    if (!isValidDir) {
                        throw new IllegalArgumentException("%s is not a valid directory.".formatted(dir));
                    }

                    writerAdapter.setDirectory(dir);
                }
            }

            if (ArrayUtils.contains(FILE.getSubCommands(), command)) {
                if (!FILE.isNullable()) {
                    writerAdapter.setFile(subCommands[i++]);
                }
            }
        }

        return writerAdapter;
    }

    private ListenerAdapter getListenerAdapter(String[] subCommands) {
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
                    String dir = subCommands[i++];

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