package prism.application.port.color;

/**
 * Port responsible for finding a color spectrum that provides the best contrast against a
 * set of quantized color buckets.
 * <p>
 * Contrast calculations follow the WCAG relative luminance and contrast ratio formulas.
 */
public class ContrastFinder {

    /**
     * Finds the contrat value (0–255) that maximizes the <em>average</em> contrast ratio
     * against all provided color buckets.
     *
     * @param buckets an array of 12-bit quantized color buckets
     * @return the color value (0–255) with the highest average contrast
     */
    public Integer findBestByAverage(Integer[] buckets) {
        Double[] luminances = this.getLuminances(buckets);

        int bestContrast = 0;
        double bestAvgContrast = -1;

        for (int contrast = 0; contrast <= 255; contrast++) {
            double contrastLuminance = this.relativeLuminance(contrast, contrast, contrast);

            double totalContrast = 0;
            for (double luminance : luminances) {
                totalContrast += this.contrastRatio(contrastLuminance, luminance);
            }
            double avgContrast = totalContrast / luminances.length;

            if (avgContrast > bestAvgContrast) {
                bestAvgContrast = avgContrast;
                bestContrast = contrast;
            }
        }

        return bestContrast;
    }

    /**
     * Finds the contrast value (0–255) that maximizes the <em>worst-case</em> contrast ratio
     * against the least contrasting color in the set.
     *
     * @param buckets an array of 12-bit quantized color buckets
     * @return the contrast value (0–255) with the best worst-case contrast
     */
    public int findBestByWorst(Integer[] buckets) {
        Double[] luminances = this.getLuminances(buckets);

        int bestContrast = 0;
        double bestWorstContrast = -1;

        for (int contrast = 0; contrast <= 255; contrast++) {
            double contrastLuminance = relativeLuminance(contrast, contrast, contrast);

            double worstContrast = Double.MAX_VALUE;
            for (Double luminance : luminances) {
                worstContrast = Math.min(
                        worstContrast,
                        contrastRatio(contrastLuminance, luminance)
                );
            }

            if (worstContrast > bestWorstContrast) {
                bestWorstContrast = worstContrast;
                bestContrast = contrast;
            }
        }

        return bestContrast;
    }

    /**
     * Converts an array of 12-bit color buckets into their relative luminance values.
     *
     * @param buckets an array of 12-bit quantized color buckets
     * @return an array of relative luminance values (0.0–1.0)
     */
    public Double[] getLuminances(Integer[] buckets) {
        Double[] luminances = new Double[buckets.length];

        for (int i = 0; i < buckets.length; i++) {
            int r = ((buckets[i] >> 8) & 0xF) * 17;
            int g = ((buckets[i] >> 4) & 0xF) * 17;
            int b = (buckets[i] & 0xF) * 17;

            luminances[i] = this.relativeLuminance(r, g, b);
        }

        return luminances;
    }

    /**
     * Calculates the relative luminance of an sRGB color per the WCAG 2.0 definition.
     *
     * @param r the red channel value (0–255)
     * @param g the green channel value (0–255)
     * @param b the blue channel value (0–255)
     * @return the relative luminance (0.0–1.0)
     */
    private Double relativeLuminance(Integer r, Integer g, Integer b) {
        double rl = linearize(r / 255.0);
        double gl = linearize(g / 255.0);
        double bl = linearize(b / 255.0);

        return 0.2126 * rl + 0.7152 * gl + 0.0722 * bl;
    }

    /**
     * Converts an sRGB channel value from gamma-corrected space to linear space.
     *
     * @param c the gamma-corrected channel value (0.0–1.0)
     * @return the linearized value
     */
    private Double linearize(Double c) {
        return c <= 0.03928
                ? c / 12.92
                : Math.pow((c + 0.055) / 1.055, 2.4);
    }

    /**
     * Calculates the contrast ratio between two relative luminance values per WCAG 2.0.
     *
     * @param l1 the first luminance value
     * @param l2 the second luminance value
     * @return the contrast ratio (1.0–21.0)
     */
    private Double contrastRatio(Double l1, Double l2) {
        double lighter = Math.max(l1, l2);
        double darker = Math.min(l1, l2);

        return (lighter + 0.05) / (darker + 0.05);
    }
}