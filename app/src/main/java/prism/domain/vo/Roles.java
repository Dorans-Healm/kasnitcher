package prism.domain.vo;

import org.jspecify.annotations.NonNull;

public record Roles(
        @NonNull Oklch lux,
        @NonNull Oklch core,
        @NonNull Oklch wave,
        @NonNull Oklch flare,
        @NonNull Oklch spark
) {
}