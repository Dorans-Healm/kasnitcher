package prism.adapter.cli;

/**
 * Defines the types of services that can execute commands in the application.
 */
public enum AppServiceType {

    /**
     * Represents a background daemon service.
     */
    DAEMON,

    /**
     * Represents a one-off execution service.
     */
    SINGLE_EXECUTIONER,

    /**
     * Represents a service type that can run in both daemon and single-execution modes.
     */
    POLYMATH
}