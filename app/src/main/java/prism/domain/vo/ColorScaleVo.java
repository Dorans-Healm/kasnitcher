package prism.domain.vo;

import lombok.Builder;

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