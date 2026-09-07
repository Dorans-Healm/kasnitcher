package prism.adapter.operation;

import prism.adapter.cli.AppSubCommandType;
import prism.configuration.AppStartup;
import prism.configuration.apdater.CacheAdapter;
import prism.configuration.apdater.ListenerAdapter;
import prism.configuration.apdater.StorageAdapter;
import prism.configuration.apdater.WriterAdapter;
import prism.configuration.context.AppContext;
import prism.configuration.context.DaemonContext;
import prism.domain.model.Command;
import prism.utils.ArrayUtils;

import java.util.Objects;

import static prism.adapter.cli.AppSubCommandType.*;

public class DaemonOperation extends AppStartup {

    private final Command[] commands;

    private final AppContext appContext;

    private DaemonOperation() {
        this.commands = new Command[]{};
        this.appContext = null;
    }

    private DaemonOperation(Command[] commands) {
        this.commands = commands;
        this.appContext = super.getAppContext();
    }

    public static void start(Command[] commands) {
        new DaemonOperation(commands).startDaemon();
    }

    private void startDaemon() {
        DaemonContext context =
                this.getDaemonContext(this.commands);

    }

    private DaemonContext getDaemonContext(Command[] commands) {
        DaemonContext context = (DaemonContext)
                this.appContext.getClass(DaemonContext.class);

        for (Command command : commands) {
            switch (command.getCommand()) {
                case CACHE -> context.setCacheAdapter(
                        this.getCacheAdapter(command.getSubCommands()));

                case STORE -> context.setStorageConfiguration(
                        this.getStorageAdapter(command.getSubCommands()));

                case WRITE -> context.setWritingConfiguration(
                        this.getWriterAdapter(command.getSubCommands()));

                case LISTEN -> context.setListenerConfiguration(
                        this.getListenerAdapter(command.getSubCommands()));

                case null, default -> throw new IllegalArgumentException(
                        "No configuration found for %s specified value.".formatted(command.getCommand()));
            }
        }

        this.appContext.setClass(context);

        return context;
    }

    private CacheAdapter getCacheAdapter(String[] subCommands) {
        CacheAdapter cacheAdapter = (CacheAdapter)
                this.appContext.getClass(CacheAdapter.class);

        for (int i = 0; i < subCommands.length; i++) {
            String command = subCommands[i];

            AppSubCommandType subCommand = AppSubCommandType.getByCommand(command);
            if (Objects.isNull(subCommand)) {
                throw new IllegalArgumentException(
                        "%s is not a valid Sub command.".formatted(command));
            }

            if (ArrayUtils.contains(AMOUNT.getSubCommands(), subCommand)) {
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
        StorageAdapter storageAdapter = (StorageAdapter)
                this.appContext.getClass(StorageAdapter.class);

        for (String command : subCommands) {
        }

        return storageAdapter;
    }

    private WriterAdapter getWriterAdapter(String[] subCommands) {
        WriterAdapter writerAdapter = (WriterAdapter)
                this.appContext.getClass(WriterAdapter.class);

        for (String command : subCommands) {
        }

        return writerAdapter;
    }

    private ListenerAdapter getListenerAdapter(String[] subCommands) {
        ListenerAdapter listenerAdapter = (ListenerAdapter)
                this.appContext.getClass(ListenerAdapter.class);

        for (String command : subCommands) {
        }

        return listenerAdapter;
    }
}