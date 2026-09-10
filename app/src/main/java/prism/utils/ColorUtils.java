package prism.utils;

import org.jspecify.annotations.NonNull;

/**
 * Utility methods for quantizing RGB colors into compact buckets and
 * converting between bucket, hex, and RGB string representations.
 * <p>
 * Colors are quantized by reducing each channel from 8 bits to 4 bits,
 * producing a 12-bit bucket value suitable for palette extraction and comparison.
 */
public class ColorUtils {

    /** Multiplier used to expand a 4-bit channel back to an approximate 8-bit value ({@value}). */
    private static final Integer QUANTIZATION_BUCKET = 16 + 8;

    /**
     * Quantizes a 24-bit RGB color into a 12-bit bucket by discarding the
     * lower 4 bits of each channel.
     *
     * @param rgb a packed 24-bit RGB integer ({@code 0xRRGGBB})
     * @return a 12-bit bucket value with 4 bits per channel
     */
    public static @NonNull Integer quantizeColor(@NonNull Integer rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        r >>= 4;
        g >>= 4;
        b >>= 4;

        return (r << 8) | (g << 4) | b;
    }

    /**
     * Converts a 12-bit color bucket to a CSS-style {@code rgb(r, g, b)} string.
     *
     * @param bucket a 12-bit quantized color bucket
     * @return a string in the format {@code "rgb(r, g, b)"}
     */
    public static @NonNull String bucketToRgb(@NonNull Integer bucket) {
        int r = ((bucket >> 8) & 0xF) * QUANTIZATION_BUCKET;
        int g = ((bucket >> 4) & 0xF) * QUANTIZATION_BUCKET;
        int b = (bucket & 0xF) * QUANTIZATION_BUCKET;

        return "rgb(%d, %d, %d)".formatted(r, g, b);
    }

    /**
     * Converts a 12-bit color bucket to a hex color string.
     *
     * @param bucket a 12-bit quantized color bucket
     * @return a string in the format {@code "#RRGGBB"}
     */
    public static @NonNull String bucketToHex(@NonNull Integer bucket) {
        int r = ((bucket >> 8) & 0xF) * QUANTIZATION_BUCKET;
        int g = ((bucket >> 4) & 0xF) * QUANTIZATION_BUCKET;
        int b = (bucket & 0xF) * QUANTIZATION_BUCKET;

        return "#%02X%02X%02X".formatted(r, g, b);
    }

    /**
     * Returns the bucket-count pair with the highest perceived luminance from
     * the given array of pairs.
     *
     * @param buckets a 2D array where each row is a {@code [bucket, count]} pair
     * @return the pair with the highest luminance
     */
    public static @NonNull Integer[] getBrightest(@NonNull Integer[][] buckets) {
        Integer[] brightest = buckets[0];
        double brightestLuminance = luminance(brightest[0]);

        for (Integer[] pair : buckets) {
            double currentLuminance = luminance(pair[0]);
            if (currentLuminance > brightestLuminance) {
                brightestLuminance = currentLuminance;
                brightest = pair;
            }
        }

        return brightest;
    }

    /**
     * Computes the relative luminance of a 12-bit color bucket using the
     * sRGB coefficients defined by the W3C (ITU-R BT.709).
     *
     * @param bucket a 12-bit quantized color bucket
     * @return the relative luminance in the range {@code [0.0, 1.0]}
     * @see <a href="https://www.w3.org/TR/WCAG20/#relativeluminancedef">WCAG 2.0 – Relative Luminance</a>
     */
    public static @NonNull Double luminance(@NonNull Integer bucket) {
        int r = ((bucket >> 8) & 0xF) * 17;
        int g = ((bucket >> 4) & 0xF) * 17;
        int b = (bucket & 0xF) * 17;

        double rl = linearize(r / 255.0);
        double gl = linearize(g / 255.0);
        double bl = linearize(b / 255.0);

        return 0.2126 * rl + 0.7152 * gl + 0.0722 * bl;
    }

    /**
     * Applies the sRGB inverse companding (gamma decoding) function to convert
     * a normalized sRGB channel value to linear light.
     *
     * @param calc the normalized sRGB channel value in {@code [0.0, 1.0]}
     * @return the linearized channel value
     */
    public static @NonNull Double linearize(@NonNull Double calc) {
        return calc <= 0.03928
                ? calc / 12.92
                : Math.pow((calc + 0.055) / 1.055, 2.4);
    }

    /**
     * Sorts an array of 12-bit color buckets in-place by descending luminance
     * using a selection sort.
     *
     * @param array the array of color buckets to sort
     */
    public static void sort(@NonNull Integer[] array) {
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = i + 1; j < array.length; j++) {
                if (luminance(array[i]) < luminance(array[j])) {
                    int temp = array[i];
                    array[i] = array[j];
                    array[j] = temp;
                }
            }
        }
    }
}