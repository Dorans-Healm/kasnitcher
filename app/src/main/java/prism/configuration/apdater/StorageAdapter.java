package prism.configuration.apdater;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StorageAdapter {

    private String file = null;

    private String directory = null;
}