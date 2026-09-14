package prism.application.service;

import org.jspecify.annotations.NonNull;
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

/**
 * Application service responsible for extracting prominent color palettes and assembling
 * the full {@link Prism} spectrum from quantized image buckets.
 */
public class WriteService {

    /**
     * The number of top core colors to extract from the image buckets for Prism
     * generation.
     */
    private static final int TOTAL_SPECTRUMS = 4;

    /**
     * Lazy supplier for the color writing service, resolving from the AppContext.
     */
    private final Supplier<ColorWriter> colorWriter;

    /**
     * Lazy supplier for the contrast finding service, resolving from the AppContext.
     */
    private final Supplier<ContrastFinder> contrastFinder;

    /**
     * Constructs a new {@code WriteService}, initializing its dependencies lazily via the
     * global {@link AppContext}.
     */
    public WriteService() {
        this.contrastFinder = AppContext
                .getClassLazy(ContrastFinder.class);
        this.colorWriter = AppContext
                .getClassLazy(ColorWriter.class);
    }

    /**
     * Formats an array of quantized color buckets into string representations.
     *
     * @param colors an array of 12-bit color buckets
     * @param type   the formatting type, either {@link WriterAdapter#HEX} or
     *               {@link WriterAdapter#RGB}
     * @return an array of formatted color strings
     * @throws IllegalArgumentException if an unsupported type is provided
     */
    public @NonNull String[] formatColors(@NonNull Integer[] colors, @NonNull String type) {
        ColorUtils.sort(colors);

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

    /**
     * Assembles a complete {@link Prism} spectrum from the given color buckets.
     * <p>
     * Iteratively extracts the gray tone, core shade, brightest flare, and supporting wave
     * colors, removing each from the available buckets as it goes.
     *
     * @param buckets a 2D array of {@code [bucket, count]} pairs
     * @return a fully populated {@link Prism} instance
     */
    public @NonNull Prism getPrism(@NonNull Integer[][] buckets) {
        buckets = this.getMostUsedIn(buckets, TOTAL_SPECTRUMS);

        Integer grayShade = this.contrastFinder.get()
                .findBestByAverage(this.toBucketArray(buckets));
        Integer[] graySpectrum = this.colorWriter.get()
                .calculateSpectrum(grayShade);

        Integer[] coreArr = this.getMostUsedFrom(buckets);
        ColorScaleVo core = ColorScaleVo.fromBuckets(coreArr);
        buckets = ArrayUtils.remove(buckets, coreArr);

        Integer[] flareArr = ColorUtils.getBrightest(buckets);
        ColorScaleVo flare = ColorScaleVo.fromBuckets(flareArr);
        buckets = ArrayUtils.remove(buckets, flareArr);

        Integer[] waveArr = this.getMostUsedFrom(buckets);
        ColorScaleVo wave = ColorScaleVo.fromBuckets(waveArr);
        buckets = ArrayUtils.remove(buckets, waveArr);

        return Prism.builder()
                .lux(ColorScaleVo.fromBuckets(graySpectrum))
                .core(core)
                .wave(wave)
                .flare(flare)
                .spark(ColorScaleVo.fromBuckets(buckets[0]))
                .build();
    }

    /**
     * Extracts the top N most frequent color buckets by completely sorting a clone of the
     * input array.
     *
     * @param buckets a 2D array of {@code [bucket, count]} pairs
     * @param amount  the maximum number of elements to return
     * @return a new 2D array containing the top {@code amount} most used buckets
     */
    @SuppressWarnings("SameParameterValue")
    private @NonNull Integer[][] getMostUsedIn(@NonNull Integer[][] buckets, @NonNull Integer amount) {
        Integer[][] sorted = buckets.clone();
        Arrays.sort(sorted, (a, b) -> b[1] - a[1]);

        int count = Math.min(amount, sorted.length);
        return Arrays.copyOfRange(sorted, 0, count);
    }

    /**
     * Finds and returns the single most frequent bucket-count pair from the given array.
     *
     * @param buckets a 2D array of {@code [bucket, count]} pairs
     * @return the {@code [bucket, count]} pair with the highest frequency
     */
    private @NonNull Integer[] getMostUsedFrom(@NonNull Integer[][] buckets) {
        Integer[] mostUsed = buckets[0];

        for (int i = 1; i < buckets.length; i++) {
            if (buckets[i][1] > mostUsed[1]) {
                mostUsed = buckets[i];
            }
        }

        return mostUsed;
    }

    /**
     * Converts a 2D array of bucket-count pairs into a 1D array of just the bucket values.
     *
     * @param buckets a 2D array of {@code [bucket, count]} pairs
     * @return a 1D array containing only the bucket integer values
     */
    private @NonNull Integer[] toBucketArray(@NonNull Integer[][] buckets) {
        Integer[] result = new Integer[buckets.length];

        for (int i = 0; i < buckets.length; i++) {
            result[i] = buckets[i][0];
        }

        return result;
    }
}