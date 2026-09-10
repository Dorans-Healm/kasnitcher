package prism.utils;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

/**
 * Utility methods for common {@link Object} array operations such as
 * adding, removing, searching, etc.
 */
public class ArrayUtils {

    /**
     * Creates a new array with the given value appended at the end.
     *
     * @param array the source array to append to; must not be {@code null} or empty
     * @param value the value to append
     * @return a new array containing all elements of {@code array} followed by {@code value}
     * @throws IllegalArgumentException if {@code array} is {@code null} or empty
     */
    public static <O> @NonNull O[] add(@NonNull O[] array, @NonNull O value) {
        if (Objects.isNull(array) || array.length == 0) {
            throw new IllegalArgumentException(
                    "Array to add item can not be empty");
        }

        O[] copy =
                Arrays.copyOf(array, array.length + 1);

        copy[copy.length - 1] = value;

        return copy;
    }

    /**
     * Searches the array for an element equal to the given key using linear search.
     *
     * @param key the object to search for
     * @param map the array to search in
     * @return the first element equal to {@code key}, or {@code null} if not found
     *         or if {@code map} is {@code null}
     */
    public static <O> @Nullable O get(@NonNull O key, @NonNull O[] map) {
        if (Objects.isNull(map)) {
            return null;
        }

        for (O o : map) {
            if (o.equals(key)) {
                return o;
            }
        }

        return null;
    }

    /**
     * Looks up a value in a flat array treated as key-value pairs.
     * <p>
     * The array is expected to contain alternating keys and values
     * (i.e. {@code [key0, value0, key1, value1, ...]}).
     *
     * @param key the key to look up
     * @param map the flat key-value array to search in
     * @return the value associated with {@code key}, or {@code null} if the key is not
     *         found or {@code map} is {@code null} or empty
     */
    public static <O> @Nullable O mapGet(@NonNull O key, @NonNull O[] map) {
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

    /**
     * Checks whether the given array contains an element equal to the specified value.
     *
     * @param array the array to search; may be {@code null} or empty
     * @param value the value to search for; may be {@code null}
     * @return {@code true} if the array contains the value, {@code false} otherwise
     */
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

    /**
     * Creates a new array with the first occurrence of the specified value removed.
     *
     * @param array the source array; must not be {@code null} or empty
     * @param value the value to remove; may be {@code null}
     * @return a new array with the matching element removed
     * @throws IllegalArgumentException if {@code array} is {@code null} or empty
     */
    @SuppressWarnings("unchecked")
    public static <O> @NonNull O[] remove(@NonNull O[] array, @Nullable O value) {
        if (Objects.isNull(array) || array.length == 0) {
            throw new IllegalArgumentException(
                    "Array for item removal can not be empty");
        }

        O[] itemRemovedArray = (O[]) new Object[array.length - 1];

        int newIndex = 0;

        for (O o : array) {
            if (!Objects.equals(o, value)) {
                itemRemovedArray[newIndex] = o;
                newIndex++;
            }
        }

        return itemRemovedArray;
    }

    /**
     * Removes the first row whose first element matches the given key from a 2D array.
     * <p>
     * Each row is expected to have its key at index {@code 0}. If no row matches,
     * the original array is returned unchanged.
     *
     * @param array the 2D source array; must not be {@code null} or empty
     * @param key   the key to match against each row's first element; may be {@code null}
     * @return a new 2D array without the matching row, or the original array if no match is found
     * @throws IllegalArgumentException if {@code array} is {@code null} or empty
     */
    @SuppressWarnings("unchecked")
    public static <O> O[][] remove(@NonNull O[][] array, @Nullable O key) {
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

        O[][] result = (O[][]) new Object[array.length - 1][];

        for (int i = 0, j = 0; i < array.length; i++) {
            if (i != index) {
                result[j++] = array[i];
            }
        }

        return result;
    }
}