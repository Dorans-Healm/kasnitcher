package prism.adapter.operation;

import prism.configuration.AppStartup;
import prism.configuration.context.AbstractServiceContextConfiguration;
import prism.configuration.context.AppContext;
import prism.configuration.context.DaemonContext;
import prism.adapter.cli.input.Command;

import java.util.function.Supplier;

public class DaemonOperation extends AppStartup {

    private final Command[] commands;

    private final AppContext appContext;

    private DaemonOperation(Command[] commands) {
        Supplier<? extends AbstractServiceContextConfiguration>
                daemonContext = AppContext.getClassLazy(DaemonContext.class);

        super(
                daemonContext
        );

        this.commands = commands;
        this.appContext = super.getAppContext();
    }

    public static void start(Command[] commands) {
        new DaemonOperation(commands).startDaemon();
    }

    private void startDaemon() {
        DaemonContext context =
                this.appContext.getClass(DaemonContext.class);

        context.updateConfiguration(this.commands);

    }
}