package prism.domain.vo;

import lombok.Builder;
import prism.utils.ColorUtils;

/**
 * Value object representing a 10-step color scale ranging from lightest ({@code shade100})
 * to darkest ({@code shade1000}).
 * <p>
 * Each shade is stored as a hex/rgb color string (e.g. {@code "#FF00AA"}, or
 * {@code "rgb(r, g, b)"}).
 * <p>
 * Scales are typically built from quantized color buckets via
 * {@link #fromBuckets(Integer[])}.
 */
@Builder
public class ColorScaleVo {

    private String shade100;
    private String shade200;
    private String shade300;
    private String shade400;
    private String shade500;
    private String shade600;
    private String shade700;
    private String shade800;
    private String shade900;
    private String shade1000;

    /**
     * Creates a {@code ColorScaleVo} from an array of quantized color buckets.
     * <p>
     * The buckets are sorted by descending luminance before mapping each position to its
     * corresponding shade (100–1000) as a hex/rgb string.
     *
     * @param shades an array of at least 11 quantized 12-bit color buckets
     * @return a new {@code ColorScaleVo} populated with hex color values
     */
    public static ColorScaleVo fromBuckets(Integer[] shades) {
        ColorUtils.sort(shades);

        return ColorScaleVo.builder()
                .shade100(ColorUtils.bucketToHex(shades[0]))
                .shade200(ColorUtils.bucketToHex(shades[1]))
                .shade300(ColorUtils.bucketToHex(shades[2]))
                .shade400(ColorUtils.bucketToHex(shades[3]))
                .shade500(ColorUtils.bucketToHex(shades[4]))
                .shade600(ColorUtils.bucketToHex(shades[5]))
                .shade700(ColorUtils.bucketToHex(shades[6]))
                .shade800(ColorUtils.bucketToHex(shades[8]))
                .shade900(ColorUtils.bucketToHex(shades[9]))
                .shade1000(ColorUtils.bucketToHex(shades[10]))
                .build();
    }

    /**
     * Returns all ten shades as a string array ordered from lightest to darkest.
     *
     * @return an array of hex color strings from {@code shade100} to {@code shade1000}
     */
    public String[] getShades() {
        return new String[]{
                shade100,
                shade200,
                shade300,
                shade400,
                shade500,
                shade600,
                shade700,
                shade800,
                shade900,
                shade1000
        };
    }

    /**
     * Returns all ten shades formatted with a leading semicolon delimiter.
     * <p>
     * Each entry is in the format hex/rgb, suitable for serialization into the
     * spectrum file format.
     *
     * @return an array of semicolon-prefixed hex color strings
     */
    public String[] getFormattedShades() {
        String[] shades = this.getShades();
        String[] formatted = new String[shades.length];

        for (int i = 0; i < shades.length; i++) {
            formatted[i] = ";%s".formatted(shades[i]);
        }

        return formatted;
    }
}