package prism.utils;

public class ColorUtils {

    public static int quantizeColor(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        r >>= 4;
        g >>= 4;
        b >>= 4;

        return (r << 8) | (g << 4) | b;
    }

    public static int[] bucketToRgb(int bucket) {
        int r = (bucket >> 8) & 0xF;
        int g = (bucket >> 4) & 0xF;
        int b = bucket & 0xF;

        return new int[]{
                r * 16,
                g * 16,
                b * 16
        };
    }

    public static String bucketToHex(int bucket) {
        int r = ((bucket >> 8) & 0xF) * 16;
        int g = ((bucket >> 4) & 0xF) * 16;
        int b = (bucket & 0xF) * 16;

        return String.format("#%02X%02X%02X", r, g, b);
    }
}