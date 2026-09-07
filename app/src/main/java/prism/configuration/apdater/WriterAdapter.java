package prism.configuration.apdater;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import prism.domain.model.Prism;

@Getter
@Setter
@Builder
public class WriterAdapter {

    private String file = "palette" + Prism.EXTENTION;

    private String directory = "~/.cache/prism/";
}