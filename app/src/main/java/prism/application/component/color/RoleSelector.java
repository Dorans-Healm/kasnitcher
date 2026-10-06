package prism.application.component.color;

import org.jspecify.annotations.NonNull;
import prism.domain.vo.ColorCluster;
import prism.domain.vo.Oklch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

/**
 * Chooses the base color of each Prism role from the image's color clusters.
 * <ul>
 *   <li><b>core</b> - the most used color.</li>
 *   <li><b>flare</b> - the brightest <em>and</em> most vivid color (not plain WCAG
 *   luminance, which favors pale grays over saturated colors).</li>
 *   <li><b>wave</b> - same hue family as core, but heavier and with more chroma.</li>
 *   <li><b>spark</b> - the accent: the hue farthest from the other roles.</li>
 *   <li><b>lux</b> - a neutral tinted with core's hue, for text.</li>
 * </ul>
 * When the image has no suitable candidate for a role, one is derived from core so the
 * palette still hangs together.
 */
public final class RoleSelector {

    /** Clusters smaller than this share of the image can't become flare, wave or spark. */
    private static final double MIN_SHARE = 0.003;

    /** Chroma below this is treated as gray (its hue is meaningless). */
    private static final double GRAY_CHROMA = 0.02;

    /** Roles must be at least this far apart from each other (OKLab distance). */
    private static final double FLARE_MIN_DISTANCE = 0.08;
    private static final double WAVE_MIN_DISTANCE = 0.10;
    private static final double SPARK_MIN_DISTANCE = 0.08;

    public record Roles(
            @NonNull Oklch lux,
            @NonNull Oklch core,
            @NonNull Oklch wave,
            @NonNull Oklch flare,
            @NonNull Oklch spark
    ) {
    }

    private RoleSelector() {
    }

    public static @NonNull Roles select(@NonNull List<ColorCluster> clusters) {
        if (clusters.isEmpty()) {
            throw new IllegalArgumentException("The image has no visible pixels");
        }

        List<ColorCluster> sorted = clusters.stream()
                .sorted(Comparator.comparingDouble(ColorCluster::share).reversed())
                .toList();

        List<ColorCluster> pool = sorted.stream()
                .filter(c -> c.share() >= MIN_SHARE)
                .toList();
        if (pool.isEmpty()) {
            pool = sorted.subList(0, Math.min(8, sorted.size()));
        }

        Oklch core = pool.get(0).color();
        boolean coreHasHue = core.c() > GRAY_CHROMA;
        List<Oklch> taken = new ArrayList<>(List.of(core));

        Oklch flare = best(pool, taken, FLARE_MIN_DISTANCE,
                _ -> true,
                c -> c.color().l() * (0.25 + c.color().c() / 0.25) * Math.pow(c.share(), 0.25))
                .orElseGet(() -> new Oklch(0.88, Math.max(0.10, core.c()), core.h()));
        taken.add(flare);

        Oklch wave = best(pool, taken, WAVE_MIN_DISTANCE,
                c -> (!coreHasHue || Oklch.hueGap(c.color().h(), core.h()) < 50)
                        && c.color().l() <= core.l() + 0.15,
                c -> (c.color().c() + 0.03) * Math.sqrt(c.share()))
                .orElseGet(() -> new Oklch(
                        Math.max(0.15, core.l() - 0.08), Math.max(0.06, core.c() * 1.6), core.h()));
        taken.add(wave);

        Oklch spark = best(pool, taken, SPARK_MIN_DISTANCE,
                c -> c.color().c() > 0.04,
                c -> minHueGap(c.color(), taken) * (c.color().c() + 0.05) * Math.pow(c.share(), 0.25))
                .orElseGet(() -> new Oklch(0.65, 0.09, (core.h() + 150) % 360));

        // Tint lux with the core hue only when core has a real hue; near-black/gray cores have an arbitrary one.
        Oklch lux = new Oklch(0.55, coreHasHue ? 0.012 : 0.0, core.h());

        return new Roles(lux, core, wave, flare, spark);
    }

    private static Optional<Oklch> best(
            List<ColorCluster> pool,
            List<Oklch> taken,
            double minDistance,
            Predicate<ColorCluster> eligible,
            ToDoubleFunction<ColorCluster> score
    ) {
        return pool.stream()
                .filter(c -> taken.stream().allMatch(t -> t.dist(c.color()) >= minDistance))
                .filter(eligible)
                .max(Comparator.comparingDouble(score))
                .map(ColorCluster::color);
    }

    private static double minHueGap(Oklch color, List<Oklch> taken) {
        return taken.stream()
                .filter(t -> t.c() > GRAY_CHROMA)
                .mapToDouble(t -> Oklch.hueGap(color.h(), t.h()))
                .min()
                .orElse(180);
    }
}