package prism.domain.vo;

import org.jspecify.annotations.NonNull;

/**
 * Represents the primary color roles within a color palette (spectrum).
 * <p>
 * This record holds the foundational colors used for generating a complete
 * color scheme, mapping to different tonal purposes.
 *
 * @param lux   The grayscale or neutral background tone.
 * @param core  The dominant primary color shade.
 * @param wave  The secondary supporting color shade.
 * @param flare A bright accent color used for contrast and highlights.
 * @param spark A secondary accent color used for sharp details.
 */
public record Roles(
        @NonNull Oklch lux,
        @NonNull Oklch core,
        @NonNull Oklch wave,
        @NonNull Oklch flare,
        @NonNull Oklch spark
) {
}