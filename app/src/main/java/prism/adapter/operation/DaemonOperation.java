package prism.adapter.operation;

import prism.configuration.AppStartup;
import prism.configuration.context.AbstractServiceContextConfiguration;
import prism.configuration.context.AppContext;
import prism.configuration.context.DaemonContext;
import prism.adapter.cli.input.Command;

import java.util.function.Supplier;

/**
 * Operation that serves as the entry point for starting the long-running background daemon
 * process.
 * <p>
 * Extends {@link AppStartup} to bootstrap the core application components, specifically
 * providing a {@link DaemonContext} as the execution context.
 */
public class DaemonOperation extends AppStartup {

    private final Command[] commands;

    private final AppContext appContext;

    /**
     * Constructs a new daemon operation and bootstraps the application context.
     *
     * @param commands the array of parsed CLI commands used to configure the daemon
     */
    private DaemonOperation(Command[] commands) {
        Supplier<? extends AbstractServiceContextConfiguration>
                daemonContext = AppContext.getClassLazy(DaemonContext.class);

        super(
                daemonContext
        );

        this.commands = commands;
        this.appContext = super.getAppContext();
    }

    /**
     * Starts the background daemon process with the specified commands.
     *
     * @param commands the configuration commands
     */
    public static void start(Command[] commands) {
        new DaemonOperation(commands).startDaemon();
    }

    /**
     * Starts Daemon process.
     */
    private void startDaemon() {
        DaemonContext context =
                this.appContext.getClass(DaemonContext.class);

        context.updateConfiguration(this.commands);

    }
}