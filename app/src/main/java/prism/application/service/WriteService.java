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
import java.util.function.Supplier;

public class WriteService {

    private static final int TOTAL_SPECTRUMS = 4;

    private final Supplier<ColorWriter> colorWriter;

    private final Supplier<ContrastFinder> contrastFinder;

    public WriteService() {
        this.contrastFinder = AppContext
                .getClassLazy(ContrastFinder.class);
        this.colorWriter = AppContext
                .getClassLazy(ColorWriter.class);
    }

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

    // TODO - this is wrong, .remove is calling the wrong method, need to pass all methods to Objects instead of primitives
    public Prism getPrism(int[][] buckets) {
        buckets = this.getMostUsedIn(buckets, TOTAL_SPECTRUMS);

        int grayShade = this.contrastFinder.get()
                .findBestByAverage(this.toBucketArray(buckets));
        int[] graySpectrum = this.colorWriter.get()
                .calculateSpectrum(grayShade);

        int[] coreArr = this.getMostUsedFrom(buckets);
        ColorScaleVo core = ColorScaleVo.fromBuckets(coreArr);
        buckets = (int[][]) ArrayUtils.remove(buckets, coreArr);

        int[] flareArr = ColorUtils.getBrightest(buckets);
        ColorScaleVo flare = ColorScaleVo.fromBuckets(flareArr);
        buckets = (int[][]) ArrayUtils.remove(buckets, flareArr);

        int[] waveArr = this.getMostUsedFrom(buckets);
        ColorScaleVo wave = ColorScaleVo.fromBuckets(waveArr);
        buckets = (int[][]) ArrayUtils.remove(buckets, waveArr);

        return Prism.builder()
                .lux(ColorScaleVo.fromBuckets(graySpectrum))
                .core(core)
                .wave(wave)
                .flare(flare)
                .spark(ColorScaleVo.fromBuckets(buckets[0]))
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
        int[] mostUsed = buckets[0];

        for (int i = 1; i < buckets.length; i++) {
            if (buckets[i][1] > mostUsed[1]) {
                mostUsed = buckets[i];
            }
        }

        return mostUsed;
    }

    private int[] toBucketArray(int[][] buckets) {
        int[] result = new int[buckets.length];

        for (int i = 0; i < buckets.length; i++) {
            result[i] = buckets[i][0];
        }

        return result;
    }
}