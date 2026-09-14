package prism.configuration;

import lombok.Getter;
import org.jspecify.annotations.NonNull;
import prism.application.port.ColorCache;
import prism.application.port.color.ColorWriter;
import prism.application.port.ImageReader;
import prism.application.port.WallpaperListener;
import prism.application.port.color.ContrastFinder;
import prism.configuration.context.AbstractServiceContextConfiguration;
import prism.configuration.context.AppContext;
import prism.configuration.context.DaemonContext;
import prism.infrastructure.daemon.SocketServer;
import prism.application.service.CacheService;
import prism.application.service.StorageService;
import prism.application.service.ListeningService;
import prism.application.service.WriteService;
import prism.infrastructure.filesystem.FileColorWriter;
import prism.infrastructure.filesystem.FileImageReader;

import java.util.function.Supplier;

/**
 * Abstract bootstrap class that wires all application components and initializes the
 * {@link AppContext} singleton.
 * <p>
 * Concrete subclasses inherit a fully constructed {@link AppContext} available via
 * {@link #getAppContext()} as soon as the constructor completes.
 */
public abstract class AppStartup {

    /**
     * The application context holding all registered component instances.
     */
    @Getter
    private AppContext appContext;

    /**
     * Constructs a new AppStartup and bootstraps the application using the provided
     * execution context.
     *
     * @param appExecutionContext a supplier providing the configuration context (e.g.,
     *                            daemon or executioner context)
     */
    public AppStartup(
            @NonNull Supplier<? extends AbstractServiceContextConfiguration> appExecutionContext
    ) {
        this.startup(appExecutionContext);
    }

    /**
     * Bootstraps the application by instantiating core components and registering them into
     * the global {@link AppContext}.
     * <p>
     * This method is automatically called during the construction of {@code AppStartup}
     * subclasses.
     *
     * @param appExecutionContext a supplier providing the configuration context
     */
    private void startup(
            @NonNull Supplier<? extends AbstractServiceContextConfiguration> appExecutionContext
    ) {
        this.appContext = AppContext.initialize(
                // application.port.color
                new ColorCache(),
                new ContrastFinder(),

                // application.port
                new WallpaperListener(),
                new ColorWriter(),
                new ImageReader(),

                // application.service
                new WriteService(appExecutionContext),
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