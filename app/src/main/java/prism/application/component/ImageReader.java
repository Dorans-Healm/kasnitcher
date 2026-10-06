package prism.application.component;

import org.jspecify.annotations.NonNull;
import prism.configuration.context.AppContext;
import prism.domain.vo.ColorCluster;
import prism.domain.vo.Oklch;
import prism.infrastructure.filesystem.FileImageReader;
import prism.utils.ArrayUtils;
import prism.utils.ColorUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Port responsible for reading an image from the filesystem and extracting
 * its color frequencies.
 */
public class ImageReader {

    /** The number of possible 12-bit color buckets. */
    private static final Integer COLOR_BUCKETS = 4096;

    /**
     * Buckets whose mean colors are closer than this (OKLab distance) are merged into one
     * cluster. ~0.02 is barely noticeable to the eye, so 0.05 merges "the same color" only.
     */
    private static final double MERGE_DISTANCE = 0.05;

    /**
     * Reads an image and groups its pixels into color clusters.
     * <p>
     * Pixels are counted in cheap 12-bit buckets, but each bucket keeps the <em>true mean</em>
     * RGB of its pixels (instead of the bucket's rounded center). Buckets that are
     * perceptually the same color are then merged.
     *
     * @param path the path to the image file
     * @return clusters sorted by descending share of the image
     * @throws IOException if the file cannot be read or processed
     */
    public @NonNull List<ColorCluster> readClusters(@NonNull String path) throws IOException {
        FileImageReader fileReader = AppContext
                .instance().getClass(FileImageReader.class);

        BufferedImage image = fileReader.readSample(path);

        long[] count = new long[COLOR_BUCKETS];
        long[] sumR = new long[COLOR_BUCKETS];
        long[] sumG = new long[COLOR_BUCKETS];
        long[] sumB = new long[COLOR_BUCKETS];
        long total = 0;

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {

                int rgb = image.getRGB(x, y);
                int alpha = (rgb >> 24) & 0xff;

                if (alpha == 0) {
                    continue;
                }

                int bucket = ColorUtils.quantizeColor(rgb);

                count[bucket]++;
                sumR[bucket] += (rgb >> 16) & 0xFF;
                sumG[bucket] += (rgb >> 8) & 0xFF;
                sumB[bucket] += rgb & 0xFF;
                total++;
            }
        }

        return toClusters(count, sumR, sumG, sumB, total);
    }

    private @NonNull List<ColorCluster> toClusters(
            long[] count, long[] sumR, long[] sumG, long[] sumB, long total
    ) {
        if (total == 0) {
            return List.of();
        }

        Integer[] order = new Integer[COLOR_BUCKETS];
        for (int i = 0; i < order.length; i++) {
            order[i] = i;
        }
        Arrays.sort(order, (a, b) -> Long.compare(count[b], count[a]));

        List<double[]> sums = new ArrayList<>(); // {count, sumR, sumG, sumB}
        List<Oklch> colors = new ArrayList<>();

        for (int bucket : order) {
            if (count[bucket] == 0) {
                break;
            }

            Oklch candidate = Oklch.fromRgb(
                    mean(sumR[bucket], sumG[bucket], sumB[bucket], count[bucket]));

            int match = -1;
            for (int i = 0; i < colors.size(); i++) {
                if (colors.get(i).dist(candidate) < MERGE_DISTANCE) {
                    match = i;
                    break;
                }
            }

            if (match < 0) {
                sums.add(new double[]{count[bucket], sumR[bucket], sumG[bucket], sumB[bucket]});
                colors.add(candidate);
            } else {
                double[] s = sums.get(match);
                s[0] += count[bucket];
                s[1] += sumR[bucket];
                s[2] += sumG[bucket];
                s[3] += sumB[bucket];
                colors.set(match, Oklch.fromRgb(mean(s[1], s[2], s[3], s[0])));
            }
        }

        List<ColorCluster> clusters = new ArrayList<>();
        for (int i = 0; i < colors.size(); i++) {
            clusters.add(new ColorCluster(colors.get(i), sums.get(i)[0] / total));
        }
        clusters.sort(Comparator.comparingDouble(ColorCluster::share).reversed());

        return clusters;
    }

    private static int mean(double sumR, double sumG, double sumB, double count) {
        return ((int) Math.round(sumR / count) << 16)
                | ((int) Math.round(sumG / count) << 8)
                | (int) Math.round(sumB / count);
    }

    /**
     * Reads an image from the specified path, quantizes its pixels into 12-bit color buckets,
     * and counts the frequency of each color.
     *
     * @param path the path to the image file
     * @return a 2D array of {@code [bucket, count]} pairs for all colors present in the image
     * @throws IOException if the file cannot be read or processed
     * @deprecated 12-bit buckets lose precision; use {@link #readClusters(String)}
     */
    @Deprecated
    public @NonNull Integer[][] readColors(@NonNull String path) throws IOException {
        FileImageReader fileReader = AppContext
                .instance().getClass(FileImageReader.class);

        BufferedImage image = fileReader.readSample(path);

        Integer[] occurrences = new Integer[COLOR_BUCKETS];
        Arrays.fill(occurrences, 0);

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {

                int rgb = image.getRGB(x, y);
                int alpha = (rgb >> 24) & 0xff;

                if (alpha == 0) {
                    continue;
                }

                int bucket = ColorUtils.quantizeColor(rgb);

                occurrences[bucket]++;
            }
        }

        return toColorCountPairs(occurrences);
    }

    private @NonNull Integer[][] toColorCountPairs(@NonNull Integer[] occurrences) {
        if (ArrayUtils.isEmpty(occurrences)) {
            throw new IllegalArgumentException(
                    "occurrences array can not be empty");
        }

        int count = 0;
        for (int occurrence : occurrences) {
            if (occurrence > 0) {
                count++;
            }
        }

        Integer[][] result = new Integer[count][2];
        int index = 0;

        for (int bucket = 0; bucket < occurrences.length; bucket++) {
            if (occurrences[bucket] > 0) {
                result[index][0] = bucket;
                result[index][1] = occurrences[bucket];
                index++;
            }
        }

        return result;
    }
}