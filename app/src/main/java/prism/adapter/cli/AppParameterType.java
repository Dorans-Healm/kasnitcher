package prism.adapter.cli;

import lombok.Getter;
import prism.application.service.CacheService;
import prism.application.service.ListeningService;
import prism.application.service.StorageService;
import prism.application.service.WriteService;

public enum AppParameterType {

    /**
     * Should the daemon watch wayland IPC to infer when a file is changed?
     */
    LISTEN(new String[]{"-l", "--listen"}, AppServiceType.DAEMON, ListeningService.class),

    /**
     * Should the daemon store given file location and given directory of the current image?
     */
    STORE(new String[]{"-s", "--store"}, AppServiceType.DAEMON, StorageService.class),

    /**
     * Should the daemon write (store in a system file), the fetched colors of an image? Or
     * Write to fjle, on command, the fetched colors of an image?
     */
    WRITE(new String[]{"-w", "--write"}, AppServiceType.POLYMATH, WriteService.class),

    /**
     * Should the daemon hold a small, temporary cache from the last (enumerated) files?
     */
    CACHE(new String[]{"-c", "--cache"}, AppServiceType.DAEMON, CacheService.class),;

    @Getter
    private final String[] commands;

    @Getter
    private final AppServiceType workingType;

    @Getter
    private final Class<?> service;

    AppParameterType(
            String[] commands,
            AppServiceType appServiceType,
            Class<?> service
    ) {
        this.commands = commands;
        this.workingType = appServiceType;
        this.service = service;
    }

    public static AppParameterType getByCommand(String command) {
        for (AppParameterType appParameterType : AppParameterType.values()) {
            for (String appCommand : appParameterType.commands) {
                if (appCommand.equals(command)) {
                    return appParameterType;
                }
            }
        }

        return null;
    }
}