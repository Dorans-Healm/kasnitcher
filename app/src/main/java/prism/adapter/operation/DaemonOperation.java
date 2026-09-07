package prism.adapter.operation;

import prism.configuration.AppStartup;
import prism.configuration.context.AppContext;
import prism.configuration.context.DaemonContext;
import prism.adapter.cli.input.Command;

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
        DaemonContext context = (DaemonContext)
                this.appContext.getClass(DaemonContext.class);

        context.updateConfiguration(this.commands);

    }
}