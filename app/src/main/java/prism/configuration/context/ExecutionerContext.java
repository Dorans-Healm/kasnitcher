package prism.configuration.context;

import lombok.Getter;
import lombok.Setter;
import prism.configuration.apdater.WriterAdapter;

@Setter
@Getter
public class ExecutionerContext {

    private WriterAdapter writingConfiguration;
}