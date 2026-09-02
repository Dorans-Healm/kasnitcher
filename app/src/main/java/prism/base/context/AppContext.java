package prism.base.context;

import prism.unix.daemon.SocketServer;
import prism.unix.service.CacheService;
import prism.unix.service.StorageService;
import prism.unix.service.ListeningService;
import prism.unix.service.WriteService;

import java.lang.reflect.RecordComponent;
import java.util.HashMap;
import java.util.Map;

public record AppContext(
        // Daemon specific
        SocketServer socketServer,

        // Services
        CacheService cacheService,
        StorageService storageService,
        ListeningService listeningService,
        WriteService writeService
) {
    private static AppContext instance;

    private static Map<Class<?>, Object> appClasses;

    public AppContext {
        RecordComponent[] components = this.getClass().getRecordComponents();

        appClasses = HashMap.newHashMap(components.length);

        for (RecordComponent component : components) {
            try {
                appClasses.put(component.getType(), component.getAccessor().invoke(this));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        instance = this;
    }

    public static AppContext instance() {
        if (instance == null) {
            throw new IllegalStateException("AppContext has not been initialized");
        }

        return instance;
    }

    public Object getClass(Class<?> clazz) {
        return appClasses.get(clazz);
    }
}