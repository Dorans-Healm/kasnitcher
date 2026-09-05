package prism.utils;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class ArrayUtils {

    public static @NonNull Boolean contains(@Nullable Object[] array, @Nullable Object value) {
        if (Objects.isNull(array) || array.length == 0) {
            return false;
        }

        if (Objects.isNull(value)) {
            return false;
        }

        for (Object item : array) {
            assert item != null;

            if (item.equals(value)) {
                return true;
            }
        }

        return false;
    }
}