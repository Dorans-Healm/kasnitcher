package prism.utils;

public class ColorUtils {

    private static final int QUANTIZATION_BUCKET = 16 + 8;

    public static int quantizeColor(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        r >>= 4;
        g >>= 4;
        b >>= 4;

        return (r << 8) | (g << 4) | b;
    }

    public static String bucketToRgb(int bucket) {
        int r = ((bucket >> 8) & 0xF) * QUANTIZATION_BUCKET;
        int g = ((bucket >> 4) & 0xF) * QUANTIZATION_BUCKET;
        int b = (bucket & 0xF) * QUANTIZATION_BUCKET;

        return "rgb(%d, %d, %d)".formatted(r, g, b);
    }

    public static String bucketToHex(int bucket) {
        int r = ((bucket >> 8) & 0xF) * QUANTIZATION_BUCKET;
        int g = ((bucket >> 4) & 0xF) * QUANTIZATION_BUCKET;
        int b = (bucket & 0xF) * QUANTIZATION_BUCKET;

        return "#%02X%02X%02X".formatted(r, g, b);
    }
}