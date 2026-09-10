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

    public static int[] getBrightest(int[][] buckets) {
        int[] brightest = buckets[0];
        double brightestLuminance = ColorUtils.luminance(brightest[0]);

        for (int[] pair : buckets) {
            double currentLuminance = ColorUtils.luminance(pair[0]);
            if (currentLuminance > brightestLuminance) {
                brightestLuminance = currentLuminance;
                brightest = pair;
            }
        }

        return brightest;
    }

    public static double luminance(int bucket) {
        int r = ((bucket >> 8) & 0xF) * 17;
        int g = ((bucket >> 4) & 0xF) * 17;
        int b = (bucket & 0xF) * 17;

        double rl = linearize(r / 255.0);
        double gl = linearize(g / 255.0);
        double bl = linearize(b / 255.0);

        return 0.2126 * rl + 0.7152 * gl + 0.0722 * bl;
    }

    public static double linearize(double c) {
        return c <= 0.03928
                ? c / 12.92
                : Math.pow((c + 0.055) / 1.055, 2.4);
    }

    public static void sort(int[] array) {
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