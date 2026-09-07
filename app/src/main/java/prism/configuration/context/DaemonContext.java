package prism.configuration.context;

import lombok.Getter;
import lombok.Setter;
import prism.configuration.apdater.CacheAdapter;
import prism.configuration.apdater.ListenerAdapter;
import prism.configuration.apdater.StorageAdapter;
import prism.configuration.apdater.WriterAdapter;

@Getter
@Setter
public class DaemonContext {

    private WriterAdapter writingConfiguration;

    private ListenerAdapter listenerConfiguration;

    private StorageAdapter storageConfiguration;

    private CacheAdapter cacheAdapter;
}