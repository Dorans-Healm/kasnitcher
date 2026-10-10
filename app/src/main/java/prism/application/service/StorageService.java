package prism.application.service;

import lombok.extern.java.Log;
import prism.configuration.context.AppContext;
import prism.domain.model.Storage;
import prism.infrastructure.filesystem.FileDataWriter;

import java.nio.file.Path;
import java.util.function.Supplier;
import java.util.logging.Level;

/**
 * Application service responsible for persisting storage information
 * to the file system.
 */
@Log
public class StorageService {

    private final Supplier<FileDataWriter> dataWriter;

    /**
     * Constructs a new {@code StorageService}, initializing its dependencies
     * lazily via the global {@link AppContext}.
     */
    public StorageService() {
        this.dataWriter = AppContext
                .getClassLazy(FileDataWriter.class);
    }

    /**
     * Stores the given file path as storage information.
     * Extracts the file name and directory to construct a {@link Storage} model,
     * which is then persisted using the configured {@link FileDataWriter}.
     *
     * @param file the absolute or relative path to the file to store
     */
    public void store(String file) {
        Path imagePath = Path.of(file);

        String fileName = imagePath.getFileName().toString();
        Path parent = imagePath.getParent();
        String dir = parent != null ? parent.toString() : "";

        Storage crrStorage = new Storage(fileName, dir);

        try {
            dataWriter.get()
                    .writeStorage(crrStorage);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Storage " +
                    "information could not be written to file", e);
        }
    }
}