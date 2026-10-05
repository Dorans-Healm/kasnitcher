package prism.application.service;

import lombok.extern.java.Log;
import prism.configuration.context.AppContext;
import prism.domain.model.Storage;
import prism.infrastructure.filesystem.FileDataWriter;

import java.nio.file.Path;
import java.util.function.Supplier;
import java.util.logging.Level;

@Log
public class StorageService {

    private final Supplier<FileDataWriter> dataWriter;

    public StorageService() {
        this.dataWriter = AppContext
                .getClassLazy(FileDataWriter.class);
    }

    public void store(String file) {
        Path imagePath = Path.of(file);

        String fileName = imagePath.getFileName().toString();
        String dir = imagePath.getParent().toString();

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