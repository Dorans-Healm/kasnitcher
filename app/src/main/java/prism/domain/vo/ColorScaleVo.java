package prism.domain.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import prism.utils.ColorUtils;

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

    public static ColorScaleVo fromBuckets(int[] shades) {
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

    public String[] getFormattedShades() {
        String[] shades = this.getShades();
        String[] formatted = new String[shades.length];

        for (int i = 0; i < shades.length; i++) {
            formatted[i] = "-%s".formatted(shades[i]);
        }

        return formatted;
    }
}