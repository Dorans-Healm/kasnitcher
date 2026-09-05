package prism.configuration.context;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class AppContext {

    private static AppContext instance;

    private static Map<Class<?>, Object> appClasses;

    private AppContext(Object... objs) {
        appClasses = new HashMap<>();

        for (Object obj : objs) {
            appClasses.put(obj.getClass(), obj);
        }

        instance = this;
    }

    public static synchronized AppContext initialize(Object... objs) {
        if (Objects.nonNull(instance)) {
            throw new IllegalStateException("App context already initialized");
        }

        instance = new AppContext(objs);
        return instance;
    }

    public static AppContext instance() {
        if (instance == null) {
            throw new IllegalStateException("App context has not been initialized");
        }

        return instance;
    }

    public Object getClass(Class<?> clazz) {
        return appClasses.get(clazz);
    }
}