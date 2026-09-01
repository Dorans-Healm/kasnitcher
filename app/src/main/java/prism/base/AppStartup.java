package prism.base;

import lombok.Getter;
import prism.unix.daemon.SocketServer;
import prism.unix.service.CacheService;
import prism.unix.service.StorageService;
import prism.unix.service.WatchService;
import prism.unix.service.WriteService;
import prism.base.context.AppContext;
import prism.observability.systemd.Notifier;

public abstract class AppStartup {

    @Getter
    private AppContext appContext;

    public AppStartup() {
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