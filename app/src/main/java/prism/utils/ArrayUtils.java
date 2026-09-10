package prism.utils;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

public class ArrayUtils {

    public static @NonNull Object[] add(@NonNull Object[] array, @NonNull Object value) {
        if (Objects.isNull(array) || array.length == 0) {
            throw new IllegalArgumentException(
                    "Array to add item can not be empty");
        }

        Object[] copy =
                Arrays.copyOf(array, array.length + 1);

        copy[copy.length - 1] = value;

        return copy;
    }

    public static @Nullable Object get(@NonNull Object key, @NonNull Object[] map) {
        if (Objects.isNull(map)) {
            return null;
        }

        for (Object o : map) {
            if (o.equals(key)) {
                return o;
            }
        }

        return null;
    }

    public static @Nullable Object mapGet(@NonNull Object key, @NonNull Object[] map) {
        if (Objects.isNull(map) || map.length == 0) {
            return null;
        }

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

    public static @NonNull Object[] remove(@NonNull Object[] array, @Nullable Object value) {
        if (Objects.isNull(array) || array.length == 0) {
            throw new IllegalArgumentException(
                    "Array for item removal can not be empty");
        }

        Object[] itemRemovedArray = new Object[array.length - 1];

        int newIndex = 0;

        for (Object o : array) {
            if (!Objects.equals(o, value)) {
                itemRemovedArray[newIndex] = o;
                newIndex++;
            }
        }

        return itemRemovedArray;
    }

    public static Object[][] remove(@NonNull Object[][] array, @Nullable Object key) {
        if (Objects.isNull(array) || array.length == 0) {
            throw new IllegalArgumentException(
                    "Array for item removal can not be empty");
        }

        int index = -1;

        for (int i = 0; i < array.length; i++) {
            if (Objects.equals(array[i][0], key)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            return array;
        }

        Object[][] result = new Object[array.length - 1][];

        for (int i = 0, j = 0; i < array.length; i++) {
            if (i != index) {
                result[j++] = array[i];
            }
        }

        return result;
    }
}