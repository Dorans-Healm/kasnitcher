package shifter.ka.application.service;

import lombok.Getter;

public enum UnixServiceType {

    /**
     * Should the daemon watch wayland IPC to infer when a file is changed?
     */
    WATCH("", ServiceWorkingType.DAEMON),

    /**
     * Should the daemon store given file location and given directory of the current image?
     * Holding the data for a possible "snitch" on where is the current image file, or what
     * would be the next image/image list inside the given directory.
     */
    STORE("", ServiceWorkingType.DAEMON),

    /**
     * Should the daemon write (store in a system file), the fetched colors of an image? Or
     * Write to fjle, on command, the fetched colors of an image.
     */
    WRITE("", ServiceWorkingType.POLYMATH),

    /**
     * Should the daemon hold a small, temporary cache from the last (enumerated) files?
     */
    CACHE("", ServiceWorkingType.DAEMON);

    @Getter
    private final String command;

    @Getter
    private final ServiceWorkingType workingType;

    UnixServiceType(String command, ServiceWorkingType serviceWorkingType) {
        this.command = command;
        this.workingType = serviceWorkingType;
    }
}