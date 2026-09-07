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

    TYPE(new String[]{"-t", "--type"}, false)

    ;

    @Getter
    private final String[] subCommands;

    @Getter
    private final boolean nullable;

    private static final String[] enumMap;

    static {
        AppSubCommandType[] values = values();

        enumMap = new String[values.length * 2 * 2];
        int index = 0;

        for (AppSubCommandType type : values) {
            for (String command : type.subCommands) {
                enumMap[index++] = command;
                enumMap[index++] = type.name();
            }
        }
    }

    AppSubCommandType(String[] subCommands, boolean nullable) {
        if (Objects.isNull(subCommands)) {
            throw new IllegalArgumentException(
                    "ubCommands is null");
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

        Object obj = ArrayUtils.get(command, enumMap);
        if (Objects.nonNull(obj) && !(obj instanceof AppSubCommandType)) {
            throw new IllegalArgumentException(
                    "Invalid sub command inserted into the enum map: " + command);
        }

        return (AppSubCommandType) obj;
    }

    public static @NonNull String[] getCommandsByArray(AppSubCommandType[] subCommands) {
        String[] commands = new String[subCommands.length * 2];
        int index = 0;

        for (AppSubCommandType type : subCommands) {
            String[] subCmds = type.subCommands;

            commands[index++] = subCmds[0];
            commands[index++] = subCmds[1];
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