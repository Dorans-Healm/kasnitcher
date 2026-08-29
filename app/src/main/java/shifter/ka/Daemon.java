package shifter.ka;

import shifter.ka.configuration.DaemonStartup;
import shifter.ka.configuration.context.AppContext;

public class Daemon extends DaemonStartup {

    private final AppContext appContext;

    private Daemon() {
        this.appContext =
                super.getAppContext();
    }

    public static void start() {
        new Daemon()
                .startDaemon();
    }

    private void startDaemon() {

    }
}