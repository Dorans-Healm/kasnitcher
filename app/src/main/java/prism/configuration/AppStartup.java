package prism.configuration;

import lombok.Getter;
import prism.configuration.context.AppContext;
import prism.infrastructure.daemon.SocketServer;
import prism.application.service.CacheService;
import prism.application.service.StorageService;
import prism.application.service.ListeningService;
import prism.application.service.WriteService;

public abstract class AppStartup {

    @Getter
    private AppContext appContext;

    public AppStartup() {
        this.startup();
    }

    private void startup() {
        this.appContext = AppContext.initialize(
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