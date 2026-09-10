package prism.application.service;

import prism.application.port.color.ColorWriter;
import prism.application.port.color.ContrastFinder;
import prism.configuration.adapter.WriterAdapter;
import prism.configuration.context.AppContext;
import prism.domain.model.Prism;
import prism.domain.vo.ColorScaleVo;
import prism.utils.ArrayUtils;
import prism.utils.ColorUtils;

import java.util.Arrays;

public class WriteService {

    private static final int TOTAL_SPECTRUMS = 4;

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

    public Prism getPrism(int[][] buckets) {
        buckets = this.getMostUsedIn(buckets, TOTAL_SPECTRUMS);

        ContrastFinder contrastFinder = (ContrastFinder)
                AppContext.instance().getClass(ContrastFinder.class);
        ColorWriter colorWriter = (ColorWriter)
                AppContext.instance().getClass(ColorWriter.class);

        int grayShade = contrastFinder
                .findBestByAverage(this.toBucketArray(buckets));
        int[] graySpectrum = colorWriter
                .calculateSpectrum(grayShade);

        ColorScaleVo lux = ColorScaleVo.fromBuckets(graySpectrum);

        int[] coreArr = this.getMostUsedFrom(buckets);
        ColorScaleVo core = ColorScaleVo.fromBuckets(coreArr);
        buckets = (int[][]) ArrayUtils.remove(buckets, coreArr);

        int[] flareArr = ColorUtils.getBrightest(buckets);
        ColorScaleVo flare = ColorScaleVo.fromBuckets(flareArr);
        buckets = (int[][]) ArrayUtils.remove(buckets, flareArr);

        int[] waveArr = this.getMostUsedFrom(buckets);
        ColorScaleVo wave = ColorScaleVo.fromBuckets(waveArr);
        buckets = (int[][]) ArrayUtils.remove(buckets, waveArr);

        ColorScaleVo spark = ColorScaleVo.fromBuckets(buckets[0]);

        return Prism.builder()
                .lux(lux)
                .core(core)
                .flare(flare)
                .wave(wave)
                .spark(spark)
                .build();
    }

    @SuppressWarnings("SameParameterValue")
    private int[][] getMostUsedIn(int[][] buckets, int amount) {
        int[][] sorted = buckets.clone();
        Arrays.sort(sorted, (a, b) -> b[1] - a[1]);

        int count = Math.min(amount, sorted.length);
        return Arrays.copyOfRange(sorted, 0, count);
    }

    private int[] getMostUsedFrom(int[][] buckets) {
        int[][] sorted = buckets.clone();
        Arrays.sort(sorted, (a, b) -> b[1] - a[1]);

        return sorted[0];
    }

    private int[] toBucketArray(int[][] buckets) {
        int[] result = new int[buckets.length];

        for (int i = 0; i < buckets.length; i++) {
            result[i] = buckets[i][0];
        }

        return result;
    }
}