package prism.application.port;

import prism.utils.ColorUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class ImageReader {

    private static final int MAX_DIMENSION = 1000;
    private static final int COLOR_BUCKETS = 4096;

    public int[][] readColors(String path) throws IOException {
        int[] occurrences = new int[COLOR_BUCKETS];

        try (ImageInputStream input = ImageIO.createImageInputStream(new File(path))) {
            if (input == null) {
                throw new IOException("Could not open image: " + path);
            }

            Iterator<javax.imageio.ImageReader> readers = ImageIO.getImageReaders(input);

            if (!readers.hasNext()) {
                throw new IOException("Unsupported image format: " + path);
            }

            javax.imageio.ImageReader reader = readers.next();

            try {
                reader.setInput(input);

                int width = reader.getWidth(0);
                int height = reader.getHeight(0);

                int sample = this.calculateSample(width, height);

                ImageReadParam param = reader.getDefaultReadParam();
                param.setSourceSubsampling(sample, sample, 0, 0);

                BufferedImage image = reader.read(0, param);

                for (int y = 0; y < image.getHeight(); y++) {
                    for (int x = 0; x < image.getWidth(); x++) {
                        int rgb = image.getRGB(x, y);
                        int bucket = ColorUtils.quantizeColor(rgb);

                        occurrences[bucket]++;
                    }
                }

            } finally {
                reader.dispose();
            }
        }

        return this.toColorCountPairs(occurrences);
    }

    private int[][] toColorCountPairs(int[] occurrences) {
        int count = 0;
        for (int occurrence : occurrences) {
            if (occurrence > 0) {
                count++;
            }
        }

        int[][] result = new int[count][2];
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

    private int calculateSample(int width, int height) {
        int largestDimension = Math.max(width, height);

        return Math.max(
                1,
                (int) Math.ceil(
                        (double) largestDimension / MAX_DIMENSION
                )
        );
    }
}