package prism.infrastructure.filesystem;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class FileImageReader {

    public BufferedImage readSample(String path, int maxDimension) throws IOException {
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

                int sample = calculateSample(width, height, maxDimension);

                ImageReadParam param = reader.getDefaultReadParam();
                param.setSourceSubsampling(sample, sample, 0, 0);

                return reader.read(0, param);
            } finally {
                reader.dispose();
            }
        }
    }

    private int calculateSample(int width, int height, int maxDimension) {
        int largestDimension = Math.max(width, height);

        return Math.max(
                1,
                (int) Math.ceil(
                        (double) largestDimension / maxDimension
                )
        );
    }
}