package prism;

import prism.base.AppStartup;
import prism.base.context.AppContext;

public class AppOperation extends AppStartup {

    private final String[] args;

    private final AppContext appContext;

    private AppOperation() {
        this.args = new String[0];
        this.appContext = null;
    }

    private AppOperation(String... args) {
        this.args = args;
        this.appContext = super.getAppContext();
    }

    public static void start(String... args) {
        new AppOperation(args).startDaemon();
    }

    private void startDaemon() {

    }
}