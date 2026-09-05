package prism.adapter.cli;

import lombok.Getter;

public enum AppSubCommandType {

    DIRECTORY(new String[]{"-d", "--directory"}),

    FILE(new String[]{"-f", "--file"}),

    AMOUNT(new String[]{"-a", "--amount"});

    @Getter
    private final String[] subCommands;

    AppSubCommandType(String[] subCommands) {
        this.subCommands = subCommands;
    }

    public static String[] getSubCmds() {
        AppSubCommandType[] values = values();

        int commandCount = 0;

        for (AppSubCommandType value : values) {
            commandCount += value.subCommands.length;
        }

        String[] cmds = new String[commandCount];
        int index = 0;

        for (AppSubCommandType value : values) {
            for (String command : value.subCommands) {
                cmds[index++] = command;
            }
        }

        return cmds;
    }
}