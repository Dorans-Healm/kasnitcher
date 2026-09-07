package prism.domain.model;

import lombok.Builder;
import prism.infrastructure.entity.ColorScale;

@Builder
public class Prism {

    /**
     * File custom "prism spectrum" extension.
     */
    public static final String EXTENTION = ".spec";

    /**
     * Grayscale (Lux)
     */
    private ColorScale lux;

    /**
     * Dominant color shade (core)
     */
    private ColorScale core;

    /**
     * Secondary supporting (Wave)
     */
    private ColorScale wave;

    /**
     * Bright accent contrast (Flare)
     */
    private ColorScale flare;

    /**
     * Secondary accent sharpening (Spark)
     */
    private ColorScale spark;
}