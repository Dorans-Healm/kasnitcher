package prism.application.port.color;

import org.jspecify.annotations.NonNull;

public class ColorWriter {

    private static final Double[] LIGHTER_AMOUNTS = {
            0.20, 0.40, 0.60, 0.80
    };

    private static final Double[] DARKER_AMOUNTS = {
            0.20, 0.40, 0.60, 0.75, 0.85
    };

    private static final Integer CENTER_INDEX = 4;
    private static final Double STEP = 0.01;

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