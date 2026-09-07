package prism.configuration.context;

import lombok.Builder;
import prism.configuration.apdater.WriterAdapter;

@Builder
public class ExecutionerContext {

    private WriterAdapter writingConfiguration;
}