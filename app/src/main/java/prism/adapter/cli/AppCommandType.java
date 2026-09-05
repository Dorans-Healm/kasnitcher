package prism.adapter.cli;

import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import prism.application.service.CacheService;
import prism.application.service.ListeningService;
import prism.application.service.StorageService;
import prism.application.service.WriteService;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static prism.adapter.cli.AppSubCommandType.*;
import static prism.adapter.cli.AppServiceType.*;

public enum AppCommandType {

    /**
     * Should the daemon watch wayland IPC to infer when a file is changed?
     */
    LISTEN(new String[]{"-l", "--listen"},
            DAEMON,
            new AppSubCommandType[]{DIRECTORY},
            ListeningService.class),

    /**
     * Should the daemon store given file location and given directory of the current
     * image?
     */
    STORE(new String[]{"-s", "--store"},
            DAEMON,
            new AppSubCommandType[]{DIRECTORY, FILE},
            StorageService.class),

    /**
     * Should the daemon write (store in a system file), the fetched colors of an image? Or
     * Write to fjle, on command, the fetched colors of an image?
     */
    WRITE(new String[]{"-w", "--write"},
            POLYMATH,
            new AppSubCommandType[]{DIRECTORY, FILE},
            WriteService.class),

    /**
     * Should the daemon hold a small, temporary cache from the last (enumerated) files?
     */
    CACHE(new String[]{"-c", "--cache"},
            DAEMON,
            new AppSubCommandType[]{AMOUNT},
            CacheService.class),

    ;

    @Getter
    private final String[] commands;

    @Getter
    private final AppServiceType workingType;

    @Getter
    private final AppSubCommandType[] subCommands;

    @Getter
    private final Class<?> service;

    private static final Map<String, AppCommandType> enumMap = new ConcurrentHashMap<>();

    static {
        for (AppCommandType type : values()) {
            for (String command : type.commands) {
                enumMap.put(command, type);
            }
        }
    }

    AppCommandType(
            String[] commands,
            AppServiceType appServiceType,
            AppSubCommandType[] subCommands,
            Class<?> service
    ) {
        this.commands = commands;
        this.workingType = appServiceType;
        this.subCommands = subCommands;
        this.service = service;
    }

    public static @Nullable AppCommandType getByCommand(String command) {
        if (Objects.isNull(command) || command.isEmpty()) {
            return null;
        }

        return enumMap.getOrDefault(command, null);
    }

    public static @NonNull String[] getDaemonCmds() {
        AppCommandType[] values = values();

        int commandCount = 0;

        for (AppCommandType value : values) {
            if (POLYMATH.equals(value.workingType)
                    || DAEMON.equals(value.workingType)) {
                commandCount += value.commands.length;
            }
        }

        String[] cmds = new String[commandCount];
        int index = 0;

        for (AppCommandType value : values) {
            if (POLYMATH.equals(value.workingType)
                    || DAEMON.equals(value.workingType)) {
                for (String command : value.commands) {
                    cmds[index++] = command;
                }
            }
        }

        return cmds;
    }

    public static @NonNull String[] getExeCmds() {
        AppCommandType[] values = values();

        int commandCount = 0;

        for (AppCommandType value : values) {
            if (POLYMATH.equals(value.workingType)
                    || SINGLE_EXECUTIONER.equals(value.workingType)) {
                commandCount += value.commands.length;
            }
        }

        String[] cmds = new String[commandCount];
        int index = 0;

        for (AppCommandType value : values) {
            if (POLYMATH.equals(value.workingType)
                    || SINGLE_EXECUTIONER.equals(value.workingType)) {
                for (String command : value.commands) {
                    cmds[index++] = command;
                }
            }
        }

        return cmds;
    }
}