package prism.domain.model;

import lombok.Builder;
import prism.domain.vo.ColorScaleVo;

/**
 * Domain model representing a color palette (spectrum) extracted from an image.
 *
 * <p>A Prism is composed of five {@link ColorScaleVo} roles, each capturing a
 * different tonal purpose within the palette:
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
    private ColorScaleVo lux;

    /**
     * Dominant color shade (core)
     */
    private ColorScaleVo core;

    /**
     * Secondary supporting (Wave)
     */
    private ColorScaleVo wave;

    /**
     * Bright accent contrast (Flare)
     */
    private ColorScaleVo flare;

    /**
     * Secondary accent sharpening (Spark)
     */
    private ColorScaleVo spark;
}