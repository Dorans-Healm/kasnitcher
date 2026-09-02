package prism;

import prism.base.AppStartup;
import prism.base.context.AppContext;

public class DaemonOperation extends AppStartup {

    private final String[] args;

    private final AppContext appContext;

    private DaemonOperation() {
        this.args = new String[0];
        this.appContext = null;
    }

    private DaemonOperation(String... args) {
        this.args = args;
        this.appContext = super.getAppContext();
    }

    public static void start(String... args) {
        new DaemonOperation(args).startDaemon();
    }

    private void startDaemon() {

    }
}