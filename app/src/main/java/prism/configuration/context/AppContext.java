package prism.configuration.context;


import org.jspecify.annotations.NonNull;
import prism.configuration.annotations.PostConstruct;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A lightweight dependency injection container for the application.
 * <p>
 * Acts as a singleton registry for core components, allowing them to be retrieved by their
 * class type. Must be initialized before use.
 */
public final class AppContext {

    private static AppContext instance;

    private static Map<Class<?>, Object> appClasses;

    private AppContext(@NonNull Object... objs) {
        appClasses = new HashMap<>();

        for (Object obj : objs) {
            appClasses.put(obj.getClass(), obj);
        }
    }

    /**
     * Initializes the global context with the provided instances.
     *
     * @param objs the component instances to register
     * @return the initialized context singleton
     * @throws IllegalStateException if the context is already initialized
     */
    public static synchronized @NonNull AppContext initialize(@NonNull Object... objs) {
        if (Objects.nonNull(instance)) {
            throw new IllegalStateException("App context already initialized");
        }

        instance = new AppContext(objs);
        callPostConstruct();

        return instance;
    }

    private static void callPostConstruct() {
        for (Object inst : appClasses.values()) {
            Method[] methods = inst.getClass().getDeclaredMethods();

            for (Method method : methods) {
                if (method.isAnnotationPresent(PostConstruct.class)) {
                    continue;
                }

                try {
                    method.setAccessible(true);
                    method.invoke(inst);
                } catch (Exception e) {
                    throw new RuntimeException("App context could not " +
                            "be initialized while applying post processing methods.", e);
                }

                break;
            }
        }
    }

    /**
     * Returns the global context singleton.
     *
     * @return the context singleton
     * @throws IllegalStateException if the context has not been initialized
     */
    public static @NonNull AppContext instance() {
        if (instance == null) {
            throw new IllegalStateException("App context has not been initialized");
        }

        return instance;
    }

    /**
     * Retrieves a registered component by its class type.
     *
     * @param clazz the class of the component to retrieve
     * @param <O>   the expected type of the component
     * @return the registered component instance
     * @throws IllegalStateException if the component is not registered
     */
    public <O> @NonNull O getClass(@NonNull Class<O> clazz) {
        O obj = clazz.cast(appClasses.get(clazz));

        if (Objects.isNull(obj)) {
            throw new IllegalStateException(
                    "Class not registered in context: %s".formatted(clazz.getName()));
        }

        return obj;
    }

    /**
     * Creates a {@link Supplier} that lazily retrieves a component by its class type.
     *
     * @param clazz the class of the component to retrieve
     * @param <O>   the expected type of the component
     * @return a supplier that will retrieve the component upon invocation
     */
    public static <O> @NonNull Supplier<O> getClassLazy(@NonNull Class<O> clazz) {
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

    /**
     * Registers a new component instance into the context dynamically.
     *
     * @param obj the component to register
     */
    public void setClass(@NonNull Object obj) {
        appClasses.put(obj.getClass(), obj);
    }
}