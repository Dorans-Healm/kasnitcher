package prism.application.port.color;

import org.jspecify.annotations.NonNull;

/**
 * Port responsible for generating a 10-step color spectrum from a single 12-bit quantized
 * color bucket.
 * <p>
 * The spectrum is constructed by producing lighter and darker variants of the base color,
 * ensuring each shade is unique within the result.
 */
public class ColorWriter {

    /**
     * Intensity amounts used to produce progressively lighter shades.
     */
    private static final Double[] LIGHTER_AMOUNTS = {
            0.20, 0.40, 0.60, 0.80
    };

    /**
     * Intensity amounts used to produce progressively darker shades.
     */
    private static final Double[] DARKER_AMOUNTS = {
            0.20, 0.40, 0.60, 0.75, 0.85
    };

    /**
     * The index within the result array that holds the original (center) color.
     */
    private static final Integer CENTER_INDEX = 4;

    /**
     * The increment step used when searching for a unique shade amount.
     */
    private static final Double STEP = 0.01;

    /**
     * Enum defining the two shading directions (lighter and darker) and their channel
     * adjustment logic.
     */
    private enum Shade {
        LIGHTER(new Integer[]{3, 2, 1, 0}, LIGHTER_AMOUNTS) {
            @Override
            @NonNull Integer adjustChannel(
                    @NonNull Integer value,
                    @NonNull Double amount
            ) {
                return (int) Math.round(
                        value + (15 - value) * amount
                );
            }
        },

        DARKER(new Integer[]{5, 6, 7, 8, 9}, DARKER_AMOUNTS) {
            @Override
            @NonNull Integer adjustChannel(
                    @NonNull Integer value,
                    @NonNull Double amount
            ) {
                return (int) Math.round(
                        value * (1.0 - amount)
                );
            }
        };

        final Integer[] order;
        final Double[] amounts;

        Shade(@NonNull Integer[] order, @NonNull Double[] amounts) {
            this.order = order;
            this.amounts = amounts;
        }

        abstract @NonNull Integer adjustChannel(@NonNull Integer value, @NonNull Double amount);
    }

    /**
     * Calculates a 10-step color spectrum from a single 12-bit color bucket.
     * <p>
     * The center position holds the original color, with lighter shades filling positions
     * below and darker shades filling positions above.
     *
     * @param bucket a 12-bit quantized color bucket
     * @return an array of 10 bucket values representing the spectrum from lightest to
     * darkest
     */
    public @NonNull Integer[] calculateSpectrum(Integer bucket) {
        Integer r = (bucket >> 8) & 0xF;
        Integer g = (bucket >> 4) & 0xF;
        Integer b = bucket & 0xF;

        Integer[] result = new Integer[10];

        result[CENTER_INDEX] = bucket & 0xFFF;

        calculateShades(result, r, g, b, Shade.LIGHTER);
        calculateShades(result, r, g, b, Shade.DARKER);

        return result;
    }

    /**
     * Populates the result array with shades in the given direction (lighter or darker).
     * <p>
     * If a generated shade duplicates an existing entry, a unique amount is searched for
     * within the allowed range.
     *
     * @param result the spectrum array being populated
     * @param r      the red channel of the base color (0–15)
     * @param g      the green channel of the base color (0–15)
     * @param b      the blue channel of the base color (0–15)
     * @param shade  the shading direction and adjustment strategy
     */
    private void calculateShades(
            @NonNull Integer[] result,
            @NonNull Integer r,
            @NonNull Integer g,
            @NonNull Integer b,
            @NonNull Shade shade
    ) {
        Double[] amounts = shade.amounts;
        Integer[] order = shade.order;

        double previousAmount = 0.0;

        for (int k = 0; k < amounts.length; k++) {
            Integer index = order[k];

            Double amount = Math.max(amounts[k], previousAmount);

            Double maxAmount = k < amounts.length - 1
                    ? amounts[k + 1]
                    : 1.0;

            result[index] = createBucket(r, g, b, amount, shade);

            if (isDuplicate(result, result[index], index, shade)) {
                amount = findUniqueAmount(
                        result,
                        index,
                        r, g, b,
                        amount,
                        previousAmount,
                        maxAmount,
                        shade
                );

                result[index] = createBucket(r, g, b, amount, shade);
            }

            previousAmount = amount;
        }
    }

    /**
     * Searches for an adjustment amount that produces a shade not already present in the
     * spectrum, stepping incrementally within the allowed range.
     *
     * @param result       the spectrum array being populated
     * @param currentIndex the target index in the result array
     * @param r            the red channel (0–15)
     * @param g            the green channel (0–15)
     * @param b            the blue channel (0–15)
     * @param startAmount  the initial amount to start searching from
     * @param minAmount    the minimum allowed amount
     * @param maxAmount    the maximum allowed amount
     * @param shade        the shading direction
     * @return the first amount that produces a unique shade, or {@code startAmount} if none
     * found
     */
    private @NonNull Double findUniqueAmount(
            @NonNull Integer[] result,
            @NonNull Integer currentIndex,
            @NonNull Integer r,
            @NonNull Integer g,
            @NonNull Integer b,
            @NonNull Double startAmount,
            @NonNull Double minAmount,
            @NonNull Double maxAmount,
            @NonNull Shade shade
    ) {
        int startStep = (int) Math.round(startAmount / STEP) + 1;
        int minStep = (int) Math.round(minAmount / STEP) + 1;
        int maxStep = (int) Math.round(maxAmount / STEP);

        startStep = Math.max(startStep, minStep);

        for (int step = startStep; step <= maxStep; step++) {
            double amount = step * STEP;

            Integer bucket = createBucket(r, g, b, amount, shade);

            if (!isDuplicate(result, bucket, currentIndex, shade)) {
                return amount;
            }
        }

        return startAmount;
    }

    /**
     * Checks whether a bucket value already exists among the previously calculated shades
     * for the given direction.
     *
     * @param result       the spectrum array
     * @param bucket       the bucket value to check
     * @param currentIndex the index of the shade being validated
     * @param shade        the shading direction (determines the comparison range)
     * @return {@code true} if the bucket already appears in the relevant range
     */
    private @NonNull Boolean isDuplicate(
            @NonNull Integer[] result,
            @NonNull Integer bucket,
            @NonNull Integer currentIndex,
            @NonNull Shade shade
    ) {
        int fromIndex;
        int toIndex;

        if (shade == Shade.DARKER) {
            fromIndex = CENTER_INDEX;
            toIndex = currentIndex - 1;
        } else {
            fromIndex = currentIndex + 1;
            toIndex = CENTER_INDEX;
        }

        for (int i = fromIndex; i <= toIndex; i++) {
            if (result[i].equals(bucket)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Creates a new 12-bit color bucket by adjusting each channel of the base color by the
     * given amount in the specified shading direction.
     *
     * @param r      the red channel (0–15)
     * @param g      the green channel (0–15)
     * @param b      the blue channel (0–15)
     * @param amount the intensity of the adjustment (0.0–1.0)
     * @param shade  the shading direction and adjustment strategy
     * @return a new 12-bit bucket representing the adjusted color
     */
    private @NonNull Integer createBucket(
            @NonNull Integer r,
            @NonNull Integer g,
            @NonNull Integer b,
            @NonNull Double amount,
            @NonNull Shade shade
    ) {
        Integer newR = shade.adjustChannel(r, amount);
        Integer newG = shade.adjustChannel(g, amount);
        Integer newB = shade.adjustChannel(b, amount);

        return (newR << 8) | (newG << 4) | newB;
    }
}