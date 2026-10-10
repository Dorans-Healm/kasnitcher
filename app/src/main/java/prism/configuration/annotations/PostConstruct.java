package prism.configuration.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used on a method that needs to be executed after dependency injection is done
 * to perform any initialization.
 * <p>
 * This annotation MUST be supported by the dependency injection container. The method
 * annotated with {@link PostConstruct} will be invoked exactly once during the initialization
 * of the application context.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PostConstruct {
}