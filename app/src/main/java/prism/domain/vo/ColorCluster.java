package prism.domain.vo;

import org.jspecify.annotations.NonNull;

/**
 * A group of perceptually similar pixels: the true mean color of the group and the
 * fraction of the image it covers (0.0-1.0).
 *
 * @param color the true mean color of the cluster in OKLCH color space
 * @param share the fraction of the image covered by this cluster, from 0.0 to 1.0
 */
public record ColorCluster(@NonNull Oklch color, double share) {
}