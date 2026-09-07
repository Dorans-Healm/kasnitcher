package prism.configuration;

import lombok.Getter;
import prism.adapter.cli.procedure.ProcedureCaller;
import prism.application.port.ColorCache;
import prism.application.port.ColorWriter;
import prism.application.port.ImageReader;
import prism.application.port.WallpaperListener;
import prism.configuration.context.AppContext;
import prism.configuration.context.DaemonContext;
import prism.infrastructure.daemon.SocketServer;
import prism.application.service.CacheService;
import prism.application.service.StorageService;
import prism.application.service.ListeningService;
import prism.application.service.WriteService;
import prism.infrastructure.filesystem.FileColorWriter;
import prism.infrastructure.filesystem.FileImageReader;

public abstract class AppStartup {

    @Getter
    private AppContext appContext;

    public AppStartup() {
        this.startup();
    }

    private void startup() {
        this.appContext = AppContext.initialize(
                // adapter.cli.procedure
                new ProcedureCaller(),

                // application.port
                new ColorCache(),
                new WallpaperListener(),
                new ColorWriter(),
                new ImageReader(),

                // application.service
                new WriteService(),
                new StorageService(),
                new ListeningService(),
                new CacheService(),

                // configuration.context
                new DaemonContext(),

                // infrastructure.daemon
                new SocketServer(),

                // infrastructure.filesystem
                new FileImageReader(),
                new FileColorWriter()
        );
    }
}