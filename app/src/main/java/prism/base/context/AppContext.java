package prism.base.context;

import prism.unix.daemon.SocketServer;
import prism.unix.service.CacheService;
import prism.unix.service.StorageService;
import prism.unix.service.WatchService;
import prism.unix.service.WriteService;
import prism.observability.systemd.Notifier;

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