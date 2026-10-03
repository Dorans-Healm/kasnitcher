package prism.application.service;

import lombok.extern.java.Log;
import org.jspecify.annotations.NonNull;
import prism.configuration.adapter.StorageAdapter;
import prism.configuration.context.AbstractServiceContextConfiguration;
import prism.configuration.context.AppContext;
import prism.domain.model.Storage;
import prism.infrastructure.filesystem.FileDataWriter;

import java.util.function.Supplier;
import java.util.logging.Level;

@Log
public class StorageService {

    private final Supplier<? extends AbstractServiceContextConfiguration> appExecutionContext;

    private final Supplier<FileDataWriter> dataWriter;

    public StorageService(@NonNull Supplier<? extends AbstractServiceContextConfiguration> appExecutionContext) {
        this.appExecutionContext = appExecutionContext;

        this.dataWriter = AppContext
                .getClassLazy(FileDataWriter.class);
    }

    public boolean store() {
        AbstractServiceContextConfiguration executionContext = appExecutionContext.get();

        StorageAdapter storageAdapter = executionContext.getStorageAdapter();
        if (storageAdapter == null) {
            return false;
        }

        String file = storageAdapter.getFile();
        String dir = storageAdapter.getDirectory();

        Storage crrStorage =
                new Storage(file, dir);

        try {
            dataWriter.get()
                    .writeStorage(crrStorage);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Storage " +
                    "information could not be written to file", e);
            return false;
        }

        return true;
    }
}