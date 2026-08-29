package shifter.ka;

import shifter.ka.configuration.DaemonStartup;
import shifter.ka.configuration.context.AppContext;

public class Daemon extends DaemonStartup {

    private final String[] args;

    private final AppContext appContext;

    private Daemon() {
        this.args = new String[0];
        this.appContext = null;
    }

    private Daemon(String... args) {
        this.args = args;
        this.appContext = super.getAppContext();
    }

    public static void start(String... args) {
        new Daemon(args).startDaemon();
    }

    private void startDaemon() {

    }
}