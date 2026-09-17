package prism.domain.model;

import lombok.Builder;
import org.jspecify.annotations.NonNull;
import prism.domain.vo.ColorScaleVo;

/**
 * Domain model representing a color palette (spectrum) extracted from an image.
 * <p>
 * A Prism is composed of five {@link ColorScaleVo} roles, each capturing a different tonal
 * purpose within the palette:
 * <ul>
 *   <li><b>Lux</b> – grayscale tones</li>
 *   <li><b>Core</b> – dominant color shade</li>
 *   <li><b>Wave</b> – secondary supporting shade</li>
 *   <li><b>Flare</b> – bright accent contrast</li>
 *   <li><b>Spark</b> – secondary accent sharpening</li>
 * </ul>
 *
 * <p>Instances are persisted with the custom {@value #EXTENSION} file extension.
 */
@Builder
public class Prism {

    /**
     * File custom "prism spectrum" extension.
     */
    public static final String EXTENSION = ".spec";

    /**
     * Grayscale (Lux)
     */
    private @NonNull ColorScaleVo lux;

    /**
     * Dominant color shade (core)
     */
    private @NonNull ColorScaleVo core;

    /**
     * Secondary supporting (Wave)
     */
    private @NonNull ColorScaleVo wave;

    /**
     * Bright accent contrast (Flare)
     */
    private @NonNull ColorScaleVo flare;

    /**
     * Secondary accent sharpening (Spark)
     */
    private @NonNull ColorScaleVo spark;


    public String getId() {
        return this.getLuxId() +
                "-" +
                this.getCoreId() +
                "-" +
                this.getWaveId() +
                "-" +
                this.getFlareId() +
                "-" +
                this.getSparkId();
    }

    private String getLuxId() {
        return this.getShades(
                this.lux.getShades());
    }

    private String getCoreId() {
        return this.getShades(
                this.core.getShades());
    }

    private String getWaveId() {
        return this.getShades(
                this.wave.getShades());
    }

    private String getFlareId() {
        return this.getShades(
                this.flare.getShades());
    }

    private String getSparkId() {
        return this.getShades(
                this.spark.getShades());
    }

    private String getShades(String[] shades) {
        StringBuilder builder = new StringBuilder();

        for (String shade : shades) {
            builder.append(shade.charAt(shade.length() - 2));
        }

        return builder.toString();
    }
}