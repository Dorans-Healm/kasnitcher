package prism.configuration.context;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public final class AppContext {

    private static AppContext instance;

    private static Map<Class<?>, Object> appClasses;

    private AppContext(Object... objs) {
        appClasses = new HashMap<>();

        for (Object obj : objs) {
            appClasses.put(obj.getClass(), obj);
        }
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

    public <O> O getClass(Class<O> clazz) {
        O obj = clazz.cast(appClasses.get(clazz));

        if (Objects.isNull(obj)) {
            throw new IllegalStateException(
                    "Class not registered in context: %s".formatted(clazz.getName()));
        }

        return obj;
    }

    public static <O> Supplier<O> getClassLazy(Class<O> clazz) {
        return () -> {
            if (Objects.isNull(appClasses)) {
                throw new IllegalStateException(
                        "App context has not been initialized");
            }

            O obj = clazz.cast(appClasses.get(clazz));

            if (Objects.isNull(obj)) {
                throw new IllegalStateException(
                        "Class not registered in context: %s".formatted(clazz.getName()));
            }

            return obj;
        };
    }

    public Object setClass(Object obj) {
        return appClasses.put(obj.getClass(), obj);
    }
}