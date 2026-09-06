package prism.utils;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

public class ArrayUtils {

    public static @Nullable Object get(Object key, @NonNull Object[] map) {
        for (int i = 0; i < map.length; i += 2) {
            if (map[i].equals(key)) {
                return map[i + 1];
            }
        }

        return null;
    }

    public static @NonNull Boolean contains(@Nullable Object[] array, @Nullable Object value) {
        if (Objects.isNull(array) || array.length == 0) {
            return false;
        }

        if (Objects.isNull(value)) {
            return false;
        }

        for (Object item : array) {
            if (value.equals(item)) {
                return true;
            }
        }

        return false;
    }
}