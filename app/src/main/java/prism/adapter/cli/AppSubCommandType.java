package prism.adapter.cli;

import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import prism.utils.ArrayUtils;

import java.util.Objects;

public enum AppSubCommandType {

    DIRECTORY(new String[]{"-d", "--directory"}, false),

    FILE(new String[]{"-f", "--file"}, false),

    AMOUNT(new String[]{"-a", "--amount"}, false),

    TYPE(new String[]{"-t", "--type"}, false),

    INTERRUPT(new String[]{"interrupt"}, true),

    RESET(new String[]{"reset"}, true),

    ;

    @Getter
    private final String[] subCommands;

    @Getter
    private final boolean nullable;

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
                    "subCommands must be exactly 2");
        }

        this.subCommands = subCommands;
        this.nullable = nullable;
    }

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

        return commands;
    }

    public static String[] getSubCmds() {
        AppSubCommandType[] values = values();

        String[] cmds = new String[values.length * 2];
        int index = 0;

        for (AppSubCommandType value : values) {
            for (String command : value.subCommands) {
                cmds[index++] = command;
            }
        }

        return cmds;
    }
}