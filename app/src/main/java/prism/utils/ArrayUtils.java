package prism.utils;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

public class ArrayUtils {

    public static Object[] add(Object[] array, Object value) {
        Object[] copy =
                Arrays.copyOf(array, array.length + 1);

        copy[copy.length - 1] = value;

        return copy;
    }

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

    public static Object[] remove(@Nullable Object[] array, Object value) {
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

    public static Object[][] remove(@Nullable Object[][] array, Object key) {
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