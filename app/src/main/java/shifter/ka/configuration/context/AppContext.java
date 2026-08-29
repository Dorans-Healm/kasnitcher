package shifter.ka.configuration.context;

import shifter.ka.application.daemon.SocketServer;
import shifter.ka.application.service.CacheService;
import shifter.ka.application.service.StorageService;
import shifter.ka.application.service.WatchService;
import shifter.ka.application.service.WriteService;
import shifter.ka.observability.systemd.Notifier;

public record AppContext(

        // Daemon specific
        SocketServer socketServer,

        // Services
        CacheService cacheService,
        StorageService storageService,
        WatchService watchService,
        WriteService writeService,

        // Observability
        Notifier notifier
) {

}