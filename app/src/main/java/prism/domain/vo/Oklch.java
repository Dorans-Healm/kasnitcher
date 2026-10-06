package prism.domain.vo;

/**
 * A color in the OKLCH space: perceptual lightness {@code l} (0-1), chroma {@code c}
 * (0 = gray, ~0.3 = very vivid) and hue {@code h} in degrees (0-360).
 * <p>
 * Distances in this space follow what the eye sees much better than distances in RGB,
 * and lightness can be changed without shifting the hue.
 */
public record Oklch(double l, double c, double h) {

    /**
     * Converts a packed {@code 0xRRGGBB} (or {@code 0xAARRGGBB}, alpha ignored) color.
     */
    public static Oklch fromRgb(int rgb) {
        double r = toLinear(((rgb >> 16) & 0xFF) / 255.0);
        double g = toLinear(((rgb >> 8) & 0xFF) / 255.0);
        double b = toLinear((rgb & 0xFF) / 255.0);

        double lc = Math.cbrt(0.4122214708 * r + 0.5363325363 * g + 0.0514459929 * b);
        double mc = Math.cbrt(0.2119034982 * r + 0.6806995451 * g + 0.1073969566 * b);
        double sc = Math.cbrt(0.0883024619 * r + 0.2817188376 * g + 0.6299787005 * b);

        double lightness = 0.2104542553 * lc + 0.7936177850 * mc - 0.0040720468 * sc;
        double a = 1.9779984951 * lc - 2.4285922050 * mc + 0.4505937099 * sc;
        double bb = 0.0259040371 * lc + 0.7827717662 * mc - 0.8086757660 * sc;

        double hue = (Math.toDegrees(Math.atan2(bb, a)) + 360) % 360;
        return new Oklch(lightness, Math.hypot(a, bb), hue);
    }

    /**
     * Converts to a packed {@code 0xRRGGBB} color. If the color is outside the sRGB gamut
     * the chroma is reduced (lightness and hue are kept) instead of clipping channels.
     */
    public int toRgb() {
        double chroma = c;
        for (int i = 0; i < 40; i++) {
            double[] lin = linearRgb(l, chroma, h);
            if (inGamut(lin)) {
                return pack(lin);
            }
            chroma *= 0.93;
        }
        return pack(linearRgb(l, 0, h));
    }

    /**
     * Euclidean distance in OKLab. Roughly: ~0.02 is barely noticeable, ~0.1 is clearly different.
     */
    public double dist(Oklch other) {
        double dl = l - other.l;
        double da = a() - other.a();
        double db = b() - other.b();
        return Math.sqrt(dl * dl + da * da + db * db);
    }

    /**
     * Smallest angle between two hues, in degrees (0-180).
     */
    public static double hueGap(double h1, double h2) {
        double d = Math.abs(h1 - h2) % 360;
        return d > 180 ? 360 - d : d;
    }

    private double a() {
        return c * Math.cos(Math.toRadians(h));
    }

    private double b() {
        return c * Math.sin(Math.toRadians(h));
    }

    private static double[] linearRgb(double lightness, double chroma, double hueDeg) {
        double a = chroma * Math.cos(Math.toRadians(hueDeg));
        double b = chroma * Math.sin(Math.toRadians(hueDeg));
        double l = Math.pow(lightness + 0.3963377774 * a + 0.2158037573 * b, 3);
        double m = Math.pow(lightness - 0.1055613458 * a - 0.0638541728 * b, 3);
        double s = Math.pow(lightness - 0.0894841775 * a - 1.2914855480 * b, 3);
        return new double[]{
                4.0767416621 * l - 3.3077115913 * m + 0.2309699292 * s,
                -1.2684380046 * l + 2.6097574011 * m - 0.3413193965 * s,
                -0.0041960863 * l - 0.7034186147 * m + 1.7076147010 * s};
    }

    private static boolean inGamut(double[] lin) {
        for (double v : lin) {
            if (v < -0.0005 || v > 1.0005) {
                return false;
            }
        }
        return true;
    }

    private static int pack(double[] lin) {
        int rgb = 0;
        for (double v : lin) {
            double clamped = Math.clamp(v, 0, 1);
            double encoded = clamped <= 0.0031308
                    ? 12.92 * clamped
                    : 1.055 * Math.pow(clamped, 1 / 2.4) - 0.055;
            rgb = (rgb << 8) | (int) Math.round(encoded * 255);
        }
        return rgb;
    }

    private static double toLinear(double c) {
        return c <= 0.04045 ? c / 12.92 : Math.pow((c + 0.055) / 1.055, 2.4);
    }
}