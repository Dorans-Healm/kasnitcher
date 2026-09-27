package prism.infrastructure.filesystem;

import prism.utils.ArrayUtils;

import java.util.Locale;

public enum ActiveImageType {

    PNG,
    JPEG,
    JPG,
    WEBP

    ;

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

    public static boolean has(String type) {
        return ArrayUtils.contains(
                enumMap, type.toLowerCase(Locale.ROOT));
    }
}