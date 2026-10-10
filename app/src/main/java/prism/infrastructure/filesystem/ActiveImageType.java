package prism.infrastructure.filesystem;

import prism.utils.ArrayUtils;

import java.util.Locale;

/**
 * Represents the supported image types for active processing.
 * <p>
 * This enumeration provides a standardized way to reference supported image formats
 * and a utility method to check if a specific format is supported.
 */
public enum ActiveImageType {

    /** Portable Network Graphics format. */
    PNG,
    /** Joint Photographic Experts Group format. */
    JPEG,
    /** Joint Photographic Experts Group format (alternative extension). */
    JPG,
    /** Web Picture format. */
    WEBP

    ;

    /** Cached array of lowercase string representations of the enum constants. */
    private static final String[] enumMap;

    static {
        ActiveImageType[] values = values();

        String[] enumValues = new String[values.length];
        for (int i = 0; i < enumValues.length; i++) {
            enumValues[i] = values[i]
                    .name().toLowerCase(Locale.ROOT);
        }

        enumMap = enumValues;
    }

    /**
     * Checks if the provided image type string corresponds to a supported active image type.
     *
     * @param type the image type string to check (e.g., "png", "JPG"), case-insensitive
     * @return {@code true} if the type is supported; {@code false} otherwise
     */
    public static boolean has(String type) {
        return ArrayUtils.contains(
                enumMap, type.toLowerCase(Locale.ROOT));
    }
}