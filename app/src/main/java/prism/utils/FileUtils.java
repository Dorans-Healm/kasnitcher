package prism.utils;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Utility methods for filesystem paths.
 */
public class FileUtils {

    /**
     * Checks whether the given path string points to an existing, writable directory.
     * <p>
     * The method verifies that the path exists, is a directory, and is writable.
     * Returns {@code false} if any check fails or if {@code pathStr} is {@code null}.
     *
     * @param pathStr the filesystem path to validate; may be {@code null}
     * @return {@code true} if the path is an existing writable directory, {@code false} otherwise
     */
    public static @NonNull Boolean isValidDirectory(@Nullable String pathStr) {
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