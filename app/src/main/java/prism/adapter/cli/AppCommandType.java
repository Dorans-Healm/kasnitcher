package prism.adapter.cli;

import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import prism.application.service.CacheService;
import prism.application.service.ListeningService;
import prism.application.service.StorageService;
import prism.application.service.WriteService;
import prism.utils.ArrayUtils;

import java.util.Arrays;
import java.util.Objects;

import static prism.adapter.cli.AppSubCommandType.*;
import static prism.adapter.cli.AppServiceType.*;

/**
 * Defines the main commands available in the CLI application.
 * Each command is associated with a specific service type and a set of allowed sub-commands.
 */
public enum AppCommandType {

    /**
     * Should the daemon watch wayland IPC to infer when a file is changed?
     */
    LISTEN(new String[]{"-l", "--listen"},
            DAEMON,
            new AppSubCommandType[]{DIRECTORY, FILE, INTERRUPT, RESET}),

    /**
     * Should the daemon store given file location and given directory of the current
     * image?
     */
    STORE(new String[]{"-s", "--store"},
            DAEMON,
            new AppSubCommandType[]{DIRECTORY, FILE, INTERRUPT, RESET}),

    /**
     * Should the daemon write (store in a system file), the fetched colors of an image? Or
     * Write to fjle, on command, the fetched colors of an image?
     */
    WRITE(new String[]{"-w", "--write"},
            POLYMATH,
            new AppSubCommandType[]{DIRECTORY, FILE, TYPE, RESET}),

    /**
     * Should the daemon hold a small, temporary cache from the last (enumerated) files?
     */
    CACHE(new String[]{"-c", "--cache"},
            DAEMON,
            new AppSubCommandType[]{AMOUNT, KEEP_ALIVE, INTERRUPT, RESET}),

    /**
     * Should the system (daemon or executioner) use some argument on execution?
     */
    PARAMETER(new String[]{"-p", "--parameter"},
            POLYMATH,
            new AppSubCommandType[]{FILE})

    ;

    /**
     * The string flags associated with this command (e.g., "-l", "--listen").
     */
    @Getter
    private final String[] commands;

    /**
     * The type of service this command is intended for.
     */
    @Getter
    private final AppServiceType workingType;

    /**
     * The sub-commands allowed to be used alongside this command.
     */
    @Getter
    private final AppSubCommandType[] subCommands;

    /**
     * Precomputed map to quickly retrieve an enum constant by its command flag.
     */
    private static final Object[] enumMap;

    static {
        AppCommandType[] values = values();

        enumMap = new Object[values.length * 2 * 2];
        int index = 0;

        for (AppCommandType type : values) {
            for (String command : type.commands) {
                enumMap[index++] = command;
                enumMap[index++] = type;
            }
        }
    }

    /**
     * Constructs a new command type.
     *
     * @param commands       the string flags for the command
     * @param appServiceType the target service type
     * @param subCommands    the allowed sub-commands
     */
    AppCommandType(
            String[] commands,
            AppServiceType appServiceType,
            AppSubCommandType[] subCommands
    ) {
        if (Objects.isNull(commands)) {
            throw new IllegalArgumentException(
                    "commands is null");
        }

        if (commands.length <= 0) {
            throw new IllegalArgumentException(
                    "commands is empty");
        }

        if (commands.length > 2) {
            throw new IllegalArgumentException(
                    "commands must be at most 2");
        }

        this.commands = commands;
        this.workingType = appServiceType;
        this.subCommands = subCommands;
    }

    /**
     * Retrieves an AppCommandType by its command flag.
     *
     * @param command the command string (e.g., "-l")
     * @return the corresponding AppCommandType, or null if not found
     */
    public static @Nullable AppCommandType getByCommand(String command) {
        if (Objects.isNull(command) || command.isEmpty()) {
            return null;
        }

        Object obj = ArrayUtils.mapGet(command, enumMap);
        if (Objects.nonNull(obj) && !(obj instanceof AppCommandType)) {
            throw new IllegalArgumentException(
                    "Invalid sub command inserted into the enum map: " + command);
        }

        return (AppCommandType) obj;
    }

    /**
     * Retrieves all commands that can be run by the daemon.
     * Includes POLYMATH and DAEMON commands.
     *
     * @return an array of valid daemon command strings
     */
    public static @NonNull String[] getDaemonCmds() {
        AppCommandType[] values = values();

        String[] cmds = new String[values.length * 2];
        int index = 0;

        for (AppCommandType value : values) {
            if (POLYMATH.equals(value.workingType)
                    || DAEMON.equals(value.workingType)) {
                for (String command : value.commands) {
                    cmds[index++] = command;
                }
            }
        }

        return Arrays.copyOf(cmds, index);
    }

    /**
     * Retrieves commands exclusively intended for the daemon service.
     * Excludes POLYMATH commands.
     *
     * @return an array of strictly daemon command strings
     */
    public static @NonNull String[] getDaemonNonPolymathCmds() {
        AppCommandType[] values = values();

        String[] cmds = new String[values.length * 2];
        int index = 0;

        for (AppCommandType value : values) {
            if (DAEMON.equals(value.workingType)) {
                for (String command : value.commands) {
                    cmds[index++] = command;
                }
            }
        }

        return Arrays.copyOf(cmds, index);
    }

    /**
     * Retrieves all commands that can be run by the executioner.
     * Includes POLYMATH and SINGLE_EXECUTIONER commands.
     *
     * @return an array of valid executioner command strings
     */
    public static @NonNull String[] getExeCmds() {
        AppCommandType[] values = values();

        String[] cmds = new String[values.length * 2];
        int index = 0;

        for (AppCommandType value : values) {
            if (POLYMATH.equals(value.workingType)
                    || SINGLE_EXECUTIONER.equals(value.workingType)) {
                for (String command : value.commands) {
                    cmds[index++] = command;
                }
            }
        }

        return Arrays.copyOf(cmds, index);
    }

    /**
     * Retrieves commands exclusively intended for the single executioner service.
     * Excludes POLYMATH commands.
     *
     * @return an array of strictly executioner command strings
     */
    public static @NonNull String[] getExeNonPolymathCmds() {
        AppCommandType[] values = values();

        String[] cmds = new String[values.length * 2];
        int index = 0;

        for (AppCommandType value : values) {
            if (SINGLE_EXECUTIONER.equals(value.workingType)) {
                for (String command : value.commands) {
                    cmds[index++] = command;
                }
            }
        }

        return Arrays.copyOf(cmds, index);
    }
}