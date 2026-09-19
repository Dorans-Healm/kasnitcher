package prism.domain.vo;

import lombok.Builder;
import org.jspecify.annotations.NonNull;

/**
 * Value object representing a 10-step color scale ranging from lightest ({@code shade100})
 * to darkest ({@code shade1000}).
 * <p>
 * Each shade is stored as a hex/rgb color string (e.g. {@code "#FF00AA"}, or
 * {@code "rgb(r, g, b)"}).
 */
@Builder
public class ColorScaleVo {

    private @NonNull String shade100;
    private @NonNull String shade200;
    private @NonNull String shade300;
    private @NonNull String shade400;
    private @NonNull String shade500;
    private @NonNull String shade600;
    private @NonNull String shade700;
    private @NonNull String shade800;
    private @NonNull String shade900;
    private @NonNull String shade1000;

    /**
     * Creates a {@code ColorScaleVo} from an array of pre-formatted color strings.
     * <p>
     * The input array must be sorted by descending luminance and contain exactly 10 shades
     * so that positions 0-10 map to shade100-shade1000.
     *
     * @param shades an array of at least 10 formatted hex/rgb color strings
     * @return a new {@code ColorScaleVo} populated with the color values
     */
    public static @NonNull ColorScaleVo fromColorArray(@NonNull String[] shades) {
        return ColorScaleVo.builder()
                .shade100(shades[0])
                .shade200(shades[1])
                .shade300(shades[2])
                .shade400(shades[3])
                .shade500(shades[4])
                .shade600(shades[5])
                .shade700(shades[6])
                .shade800(shades[7])
                .shade900(shades[8])
                .shade1000(shades[9])
                .build();
    }

    /**
     * Returns all ten shades as a string array ordered from lightest to darkest.
     *
     * @return an array of hex color strings from {@code shade100} to {@code shade1000}
     */
    public @NonNull String[] getShades() {
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
     * Each entry is in the format hex/rgb, suitable for serialization into the spectrum
     * file format.
     *
     * @return an array of semicolon-prefixed hex color strings
     */
    public @NonNull String[] getFormattedShades() {
        String[] shades = this.getShades();
        String[] formatted = new String[shades.length];

        for (int i = 0; i < shades.length; i++) {
            formatted[i] = "::%s".formatted(shades[i]);
        }

        return formatted;
    }
}