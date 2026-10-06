package prism.application.component.color;

import org.jspecify.annotations.NonNull;
import prism.domain.vo.Oklch;

/**
 * Generates a 10-step color scale (100-1000) from one base color.
 * <p>
 * Every scale uses the same lightness steps, so step 500 is equally light in all roles and
 * the roles mix well. The base color is kept exactly at the step closest to its own
 * lightness; the other steps keep its hue and taper its chroma toward the light and dark
 * ends. Work is done in OKLCH and returned as full 8-bit RGB, so steps never collide.
 */
public class ColorWriter {

    /** Shared lightness for steps 100..1000 (lightest to darkest). */
    private static final double[] LIGHTNESS_STEPS = {
            0.97, 0.92, 0.84, 0.75, 0.65, 0.55, 0.45, 0.35, 0.25, 0.15
    };

    /** How quickly chroma fades with distance from the base lightness. */
    private static final double CHROMA_TAPER = 1.4;

    /** Chroma never drops below this fraction of the base chroma, so ends keep a tint. */
    private static final double MIN_CHROMA_FACTOR = 0.15;

    /**
     * Calculates a 10-step spectrum from a base color.
     *
     * @param base the role's base color
     * @return 10 packed {@code 0xRRGGBB} values from lightest to darkest
     */
    public @NonNull Integer[] calculateSpectrum(@NonNull Oklch base) {
        int anchor = 0;
        for (int i = 1; i < LIGHTNESS_STEPS.length; i++) {
            if (Math.abs(LIGHTNESS_STEPS[i] - base.l()) < Math.abs(LIGHTNESS_STEPS[anchor] - base.l())) {
                anchor = i;
            }
        }

        Integer[] result = new Integer[LIGHTNESS_STEPS.length];

        for (int i = 0; i < result.length; i++) {
            double lightness = i == anchor ? base.l() : LIGHTNESS_STEPS[i];

            double chroma = i == anchor
                    ? base.c()
                    : base.c() * Math.max(
                    MIN_CHROMA_FACTOR,
                    1.0 - CHROMA_TAPER * Math.abs(lightness - base.l()));

            result[i] = new Oklch(lightness, chroma, base.h()).toRgb();
        }

        return result;
    }
}