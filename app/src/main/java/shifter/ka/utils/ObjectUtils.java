package shifter.ka.utils;

import org.jspecify.annotations.Nullable;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class ObjectUtils {

    public static @NotNull Boolean stringsNotEqualsCaseInsensitive(@Nullable String a, @Nullable String b) {
        if (Objects.isNull(a) || Objects.isNull(b)) {
            return false;
        }

        return !Objects.equals(a.toLowerCase(), b.toLowerCase());
    }

    public static @NotNull Boolean notEquals(@Nullable Object a, @Nullable Object b) {
        return !Objects.equals(a, b);
    }
}