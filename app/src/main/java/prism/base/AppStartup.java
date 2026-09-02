package prism.base;

import lombok.Getter;
import prism.unix.daemon.SocketServer;
import prism.unix.service.CacheService;
import prism.unix.service.StorageService;
import prism.unix.service.ListeningService;
import prism.unix.service.WriteService;
import prism.base.context.AppContext;

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
                new ListeningService(),
                new WriteService()
        );
    }
}