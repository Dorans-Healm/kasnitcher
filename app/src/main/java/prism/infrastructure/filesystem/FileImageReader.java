package prism.infrastructure.filesystem;

import org.jspecify.annotations.NonNull;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;

/**
 * General utilities to read from the filesystem, images, text files, etc.
 */
public class FileImageReader {

    /**
     * Maximum allowed pixel count for the largest dimension after sub-sampling ({@value}).
     */
    private static final Integer MAX_DIMENSION = 1000;

    /**
     * Reads an image from the given file path, sub-sampling it so that its largest
     * dimension does not exceed {@value #MAX_DIMENSION} pixels.
     *
     * @param path the absolute or relative path to the image file; must not be empty
     * @return a {@link BufferedImage} of the (possibly down-sampled) image
     * @throws IllegalArgumentException if {@code path} is empty
     * @throws IOException              if the file cannot be opened, the format is
     *                                  unsupported, or a read error occurs
     */
    public @NonNull BufferedImage readSample(@NonNull String path) throws IOException {
        if (path.isEmpty()) {
            throw new IllegalArgumentException("File path can not be empty");
        }

        try (ImageInputStream input = ImageIO.createImageInputStream(new File(path))) {

            if (input == null) {
                throw new IOException("Could not open image: " + path);
            }

            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);

            if (!readers.hasNext()) {
                throw new IOException("Unsupported image format: " + path);
            }

            ImageReader reader = readers.next();

            try {
                reader.setInput(input);

                int width = reader.getWidth(0);
                int height = reader.getHeight(0);

                int sample = calculateSample(width, height);

                ImageReadParam param = reader.getDefaultReadParam();
                param.setSourceSubsampling(sample, sample, 0, 0);

                return reader.read(0, param);
            } finally {
                reader.dispose();
            }
        }
    }

    /**
     * Calculates the sub-sampling factor needed to scale the largest dimension down to at
     * most {@value #MAX_DIMENSION} pixels.
     *
     * @param width  the original image width in pixels
     * @param height the original image height in pixels
     * @return the sub-sampling rate (≥ 1); a value of 1 means no sub-sampling is needed
     */
    private @NonNull Integer calculateSample(@NonNull Integer width, @NonNull Integer height) {
        int largestDimension = Math.max(width, height);

        return Math.max(
                1,
                (int) Math.ceil(
                        (double) largestDimension / MAX_DIMENSION
                )
        );
    }
}