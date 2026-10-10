package prism.adapter.cli;

import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import prism.utils.ArrayUtils;

import java.util.Arrays;
import java.util.Objects;

/**
 * Defines the sub-commands that can be used as modifiers or arguments for main commands.
 */
public enum AppSubCommandType {

    /**
     * Sub-command for specifying a directory.
     */
    DIRECTORY(new String[]{"-d", "--directory"}, false),

    /**
     * Sub-command for specifying a file.
     */
    FILE(new String[]{"-f", "--file"}, false),

    /**
     * Sub-command for specifying an amount or count.
     */
    AMOUNT(new String[]{"-a", "--amount"}, false),

    /**
     * Sub-command to set a keep-alive duration or flag.
     */
    KEEP_ALIVE(new String[]{"-k", "--keepalive"}, false),

    /**
     * Sub-command to specify a certain type or format.
     */
    TYPE(new String[]{"-t", "--type"}, false),

    /**
     * Sub-command to interrupt or stop a process.
     */
    INTERRUPT(new String[]{"interrupt"}, true),

    /**
     * Sub-command to reset a configuration or state.
     */
    RESET(new String[]{"reset"}, true),

    ;

    /**
     * The string flags associated with this sub-command (e.g., "-d", "--directory").
     */
    @Getter
    private final String[] subCommands;

    /**
     * Indicates whether this sub-command requires a subsequent value or can be used without one.
     */
    @Getter
    private final boolean nullable;

    /**
     * Precomputed map to quickly retrieve an enum constant by its sub-command flag.
     */
    private static final Object[] enumMap;

    static {
        AppSubCommandType[] values = values();

        int totalAliases = 0;
        for (AppSubCommandType type : values) {
            totalAliases += type.subCommands.length;
        }

        enumMap = new Object[totalAliases * 2];
        int index = 0;

        for (AppSubCommandType type : values) {
            for (String command : type.subCommands) {
                enumMap[index++] = command;
                enumMap[index++] = type;
            }
        }
    }

    /**
     * Constructs a new sub-command type.
     *
     * @param subCommands the string flags for the sub-command
     * @param nullable    true if the sub-command does not require an accompanying value
     */
    AppSubCommandType(String[] subCommands, boolean nullable) {
        if (Objects.isNull(subCommands)) {
            throw new IllegalArgumentException(
                    "subCommands is null");
        }

        if (subCommands.length <= 0) {
            throw new IllegalArgumentException(
                    "subCommands is empty");
        }

        if (subCommands.length > 2) {
            throw new IllegalArgumentException(
                    "subCommands must be at most 2");
        }

        this.subCommands = subCommands;
        this.nullable = nullable;
    }

    /**
     * Retrieves an AppSubCommandType by its command flag.
     *
     * @param command the sub-command string (e.g., "-d")
     * @return the corresponding AppSubCommandType, or null if not found
     */
    public static @Nullable AppSubCommandType getByCommand(String command) {
        if (Objects.isNull(command) || command.isEmpty()) {
            return null;
        }

        Object obj = ArrayUtils.mapGet(command, enumMap);
        if (Objects.nonNull(obj) && !(obj instanceof AppSubCommandType)) {
            throw new IllegalArgumentException(
                    "Invalid sub command inserted into the enum map: " + command);
        }

        return (AppSubCommandType) obj;
    }

    /**
     * Retrieves all valid flag strings for an array of AppSubCommandTypes.
     *
     * @param subCommands the array of sub-command types
     * @return an array of all flag strings for the given sub-commands
     */
    public static @NonNull String[] getCommandsByArray(AppSubCommandType[] subCommands) {
        int totalSize = 0;
        for (AppSubCommandType type : subCommands) {
            totalSize += type.subCommands.length;
        }

        String[] commands = new String[totalSize];
        int index = 0;

        for (AppSubCommandType type : subCommands) {
            for (String subCmd : type.subCommands) {
                commands[index++] = subCmd;
            }
        }

        return Arrays.copyOf(commands, index);
    }

    /**
     * Retrieves all valid flag strings across all sub-commands.
     *
     * @return an array containing all sub-command flag strings
     */
    public static String[] getSubCmds() {
        AppSubCommandType[] values = values();

        String[] cmds = new String[values.length * 2];
        int index = 0;

        for (AppSubCommandType value : values) {
            for (String command : value.subCommands) {
                cmds[index++] = command;
            }
        }

        return Arrays.copyOf(cmds, index);
    }
}