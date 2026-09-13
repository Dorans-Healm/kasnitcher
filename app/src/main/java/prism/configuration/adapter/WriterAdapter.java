package prism.configuration.adapter;

import lombok.Getter;
import lombok.Setter;
import prism.domain.model.Prism;

/**
 * Configuration settings for outputting generated color palettes.
 */
@Getter
@Setter
public class WriterAdapter {

    /**
     * Constant representing the RGB color format type.
     */
    public static final String RGB = "rgb";

    /**
     * Constant representing the HEX color format type.
     */
    public static final String HEX = "hex";

    /**
     * The default filename for the output palette. Ends with the {@link Prism#EXTENSION}
     * extension.
     */
    private String file = "palette" + Prism.EXTENSION;

    /**
     * The default output directory for the palette file.
     */
    private String directory = "~/.cache/prism/";

    /**
     * The color format type to use for writing (e.g. {@value #RGB} or {@value #HEX}).
     * Defaults to {@value #RGB}.
     */
    private String type = RGB;
}