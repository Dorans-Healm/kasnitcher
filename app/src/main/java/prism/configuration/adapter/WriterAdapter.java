package prism.configuration.adapter;

import lombok.Getter;
import lombok.Setter;
import prism.domain.model.Prism;

@Getter
@Setter
public class WriterAdapter {

    public static final String RGB = "rgb";

    public static final String HEX = "hex";

    private String file = "palette" + Prism.EXTENSION;

    private String directory = "~/.cache/prism/";

    private String type = RGB;
}