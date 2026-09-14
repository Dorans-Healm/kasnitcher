package prism.application.port;

import org.jspecify.annotations.NonNull;
import prism.configuration.context.AppContext;
import prism.infrastructure.filesystem.FileImageReader;
import prism.utils.ColorUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageReader {

    private static final Integer COLOR_BUCKETS = 4096;

    public @NonNull Integer[][] readColors(@NonNull String path) throws IOException {
        FileImageReader fileReader = AppContext
                .instance().getClass(FileImageReader.class);

        BufferedImage image = fileReader.readSample(path);
        Integer[] occurrences = new Integer[COLOR_BUCKETS];

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {

                int rgb = image.getRGB(x, y);
                int bucket = ColorUtils.quantizeColor(rgb);

                occurrences[bucket]++;
            }
        }

        return toColorCountPairs(occurrences);
    }

    private @NonNull Integer[][] toColorCountPairs(@NonNull Integer[] occurrences) {
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