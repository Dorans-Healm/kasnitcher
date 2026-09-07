package prism.domain.model;

import lombok.Builder;
import prism.domain.vo.ColorScaleVo;

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