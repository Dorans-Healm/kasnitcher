package prism.application.service;

import lombok.extern.java.Log;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import prism.application.port.ImageReader;
import prism.application.port.color.ColorWriter;
import prism.application.port.color.ContrastFinder;
import prism.configuration.adapter.WriterAdapter;
import prism.configuration.context.AbstractServiceContextConfiguration;
import prism.configuration.context.AppContext;
import prism.domain.model.Prism;
import prism.domain.vo.ColorScaleVo;
import prism.utils.ArrayUtils;
import prism.utils.ColorUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.Level;

/**
 * Application service responsible for extracting prominent color palettes and assembling
 * the full {@link Prism} spectrum from quantized image buckets.
 */
@Log
public class WriteService {

    /**
     * The number of top core colors to extract from the image buckets for Prism
     * generation.
     */
    private static final int TOTAL_SPECTRUMS = 4;

    /**
     * Lazy supplier for the application configuration context holding user preferences.
     */
    private final Supplier<? extends AbstractServiceContextConfiguration> appExecutionContext;

    /**
     * Lazy supplier for the color writing service, resolving from the AppContext.
     */
    private final Supplier<ColorWriter> colorWriter;

    /**
     * Lazy supplier for the contrast finding service, resolving from the AppContext.
     */
    private final Supplier<ContrastFinder> contrastFinder;

    /**
     * Lazy supplier for the image reading port, resolving from the AppContext.
     */
    private final Supplier<ImageReader> imageReader;

    /**
     * Constructs a new {@code WriteService}, initializing its dependencies lazily via the
     * global {@link AppContext}.
     *
     * @param appExecutionContext a supplier providing the configuration context (e.g. from
     *                            CLI arguments)
     */
    public WriteService(@NonNull Supplier<? extends AbstractServiceContextConfiguration> appExecutionContext) {
        this.appExecutionContext = appExecutionContext;

        this.contrastFinder = AppContext
                .getClassLazy(ContrastFinder.class);
        this.colorWriter = AppContext
                .getClassLazy(ColorWriter.class);
        this.imageReader = AppContext
                .getClassLazy(ImageReader.class);
    }

    /**
     * Reads and processes an image from the given file path, extracting its color
     * frequencies.
     *
     * @param pathToImage the file path to the image
     * @return a 2D array of {@code [bucket, count]} pairs, or {@code null} if an error
     * occurs
     */
    public @Nullable Integer[][] processImage(@NonNull String pathToImage) {
        try {
            return this.imageReader
                    .get().readColors(pathToImage);
        } catch (IOException e) {
            log.log(Level.SEVERE, ("Error reading image " +
                    "file from path %s").formatted(pathToImage), e);

            return null;
        }
    }

    /**
     * Assembles a complete {@link Prism} spectrum from the given color buckets.
     * <p>
     * Iteratively extracts the contrast tone, core shade, brightest flare, and supporting wave
     * colors, removing each from the available buckets as it goes.
     *
     * @param buckets a 2D array of {@code [bucket, count]} pairs
     * @return a fully populated {@link Prism} instance
     */
    public @NonNull Prism getPrism(@NonNull Integer[][] buckets) {
        buckets = this.getMostUsedIn(
                buckets, TOTAL_SPECTRUMS);

        // Lux
        ColorScaleVo lux = toScale(this.getLuxShade(buckets));

        Integer core = this.getMostUsedFrom(buckets);
        buckets = ArrayUtils.remove(buckets, core);

        // Flare
        Integer flare = ColorUtils.getBrightest(buckets);
        buckets = ArrayUtils.remove(buckets, flare);

        // Wave
        Integer wave = this.getMostUsedFrom(buckets);
        buckets = ArrayUtils.remove(buckets, wave);

        // Spark
        Integer spark = buckets[0][0];

        return Prism.builder()
                .lux(lux)
                .core(toScale(core))
                .flare(toScale(flare))
                .wave(toScale(wave))
                .spark(toScale(spark))
                .build();
    }

    /**
     * Converts a single 12-bit color bucket into a fully formatted {@link ColorScaleVo}
     * by generating a 10-step spectrum and formatting each shade.
     *
     * @param bucket a 12-bit quantized color bucket
     * @return a populated {@link ColorScaleVo}
     */
    private @NonNull ColorScaleVo toScale(@NonNull Integer bucket) {
        Integer[] spectrum = this.colorWriter.get().calculateSpectrum(bucket);
        return toScale(spectrum);
    }

    /**
     * Converts a pre-computed spectrum array into a fully formatted {@link ColorScaleVo}.
     *
     * @param spectrum an array of 10 bucket values representing the color spectrum
     * @return a populated {@link ColorScaleVo}
     */
    private @NonNull ColorScaleVo toScale(@NonNull Integer[] spectrum) {
        return ColorScaleVo.fromColorArray(this.formatColors(spectrum));
    }

    /**
     * Extracts the best fitting color shade spectrum from the given buckets for lux prism
     * color.
     *
     * @param buckets a 2D array of {@code [bucket, count]} pairs
     * @return an array of bucket values representing the calculated gray spectrum
     */
    public @NonNull Integer[] getLuxShade(@NonNull Integer[][] buckets) {
        Integer grayShade = this.contrastFinder.get()
                .findBestByAverage(this.toBucketArray(buckets));
        return this.colorWriter.get()
                .calculateSpectrum(ColorUtils.quantizeColor(grayShade));
    }

    /**
     * Formats an array of quantized color buckets into string representations.
     *
     * @param colors an array of 12-bit color buckets
     * @return an array of formatted color strings
     * @throws IllegalArgumentException if an unsupported type is provided
     */
    public @NonNull String[] formatColors(@NonNull Integer[] colors) {
        WriterAdapter adapter = this.appExecutionContext.get().getWriterAdapter();

        String type = WriterAdapter.RGB;
        if (Objects.nonNull(adapter)) {
            type = adapter.getType();
        }

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
    private @NonNull Integer getMostUsedFrom(@NonNull Integer[][] buckets) {
        Integer[] mostUsed = buckets[0];

        for (int i = 1; i < buckets.length; i++) {
            if (buckets[i][1] > mostUsed[1]) {
                mostUsed = buckets[i];
            }
        }

        return mostUsed[0];
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