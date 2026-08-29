package shifter.ka.configuration;

import lombok.Getter;
import shifter.ka.application.daemon.SocketServer;
import shifter.ka.application.service.CacheService;
import shifter.ka.application.service.StorageService;
import shifter.ka.application.service.WatchService;
import shifter.ka.application.service.WriteService;
import shifter.ka.configuration.context.AppContext;
import shifter.ka.observability.systemd.Notifier;

public abstract class DaemonStartup {

    @Getter
    private AppContext appContext;

    public DaemonStartup() {
        this.startup();
    }

    private void startup() {
        this.appContext = new AppContext(

                // Daemon specific
                new SocketServer(),

                // Services
                new CacheService(),
                new StorageService(),
                new WatchService(),
                new WriteService(),

                // Observability
                new Notifier()
        );
    }
}