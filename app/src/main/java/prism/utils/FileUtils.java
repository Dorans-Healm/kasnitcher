package prism.utils;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class FileUtils {

    public static Boolean isValidDirectory(String pathStr) {
        if (Objects.isNull(pathStr)) {
            return false;
        }

        try {
            Path path = Paths.get(pathStr);

            boolean exists = Files.exists(path);
            if (!exists) {
                throw new InvalidPathException(pathStr,
                        "%s does not exist in the system.".formatted(pathStr));
            }

            boolean isDir = Files.isDirectory(path);
            if (!isDir) {
                throw new InvalidPathException(pathStr,
                        "%s is not a valid directory.".formatted(pathStr));
            }

            boolean isWritable = Files.isWritable(path);
            if (!isWritable) {
                throw new InvalidPathException(pathStr,
                        "%s is not a writable directory.".formatted(pathStr));
            }

            return true;
        } catch (InvalidPathException e) {
            return false;
        }
    }
}