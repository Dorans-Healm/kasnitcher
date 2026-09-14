package prism.application.port;

import org.jspecify.annotations.NonNull;
import prism.configuration.context.AppContext;
import prism.infrastructure.filesystem.FileImageReader;
import prism.utils.ArrayUtils;
import prism.utils.ColorUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

/**
 * Port responsible for reading an image from the filesystem and extracting
 * its quantized color frequencies.
 */
public class ImageReader {

    /** The number of possible 12-bit color buckets. */
    private static final Integer COLOR_BUCKETS = 4096;

    /**
     * Reads an image from the specified path, quantizes its pixels into 12-bit color buckets,
     * and counts the frequency of each color.
     *
     * @param path the path to the image file
     * @return a 2D array of {@code [bucket, count]} pairs for all colors present in the image
     * @throws IOException if the file cannot be read or processed
     */
    public @NonNull Integer[][] readColors(@NonNull String path) throws IOException {
        FileImageReader fileReader = AppContext
                .instance().getClass(FileImageReader.class);

        BufferedImage image = fileReader.readSample(path);

        Integer[] occurrences = new Integer[COLOR_BUCKETS];
        Arrays.fill(occurrences, 0);

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {

                int rgb = image.getRGB(x, y);
                int bucket = ColorUtils.quantizeColor(rgb);

                occurrences[bucket]++;
            }
        }

        return toColorCountPairs(occurrences);
    }

    /**
     * Converts a flat array of occurrences (where the index is the bucket) into a
     * dense 2D array of {@code [bucket, count]} pairs.
     *
     * @param occurrences the array containing the count for each possible 12-bit bucket
     * @return a 2D array of {@code [bucket, count]} pairs
     */
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