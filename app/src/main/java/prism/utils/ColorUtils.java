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
}