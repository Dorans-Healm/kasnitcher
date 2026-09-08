package prism.application.service;

import prism.configuration.adapter.WriterAdapter;
import prism.utils.ColorUtils;

public class WriteService {

    public int[] getMostUsedColors(int[][] colors, int amount) {
        int resultSize = Math.min(amount, colors.length);

        int[] result = new int[resultSize];

        for (int i = 0; i < resultSize; i++) {
            int mostUsedIndex = i;

            for (int j = i + 1; j < colors.length; j++) {
                if (colors[j][1] > colors[mostUsedIndex][1]) {
                    mostUsedIndex = j;
                }
            }

            int[] temp = colors[i];
            colors[i] = colors[mostUsedIndex];
            colors[mostUsedIndex] = temp;

            result[i] = colors[i][0];
        }

        return result;
    }

    public String[] formatColors(int[] colors, String type) {
        if (!WriterAdapter.HEX.equalsIgnoreCase(type) && !WriterAdapter.RGB.equalsIgnoreCase(type)) {
            throw new IllegalArgumentException(
                    "Invalid color type: %s. Expected hex or rgb.".formatted(type));
        }

        String[] result = new String[colors.length];

        for (int i = 0; i < colors.length; i++) {
            int color = colors[i];

            result[i] = WriterAdapter.HEX.equalsIgnoreCase(type)
                    ? ColorUtils.bucketToHex(color)
                    : ColorUtils.bucketToRgb(color);
        }

        return result;
    }
}