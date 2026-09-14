package prism.application.port.color;

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
            Integer adjustChannel(Integer value, Double amount) {
                return (int) Math.round(
                        value + (15 - value) * amount
                );
            }
        },

        DARKER(new Integer[]{5, 6, 7, 8, 9}, DARKER_AMOUNTS) {
            @Override
            Integer adjustChannel(Integer value, Double amount) {
                return (int) Math.round(
                        value * (1.0 - amount)
                );
            }
        };

        final Integer[] order;
        final Double[] amounts;

        Shade(Integer[] order, Double[] amounts) {
            this.order = order;
            this.amounts = amounts;
        }

        abstract Integer adjustChannel(Integer value, Double amount);
    }

    public Integer[] calculateSpectrum(Integer bucket) {
        int r = (bucket >> 8) & 0xF;
        int g = (bucket >> 4) & 0xF;
        int b = bucket & 0xF;

        Integer[] result = new Integer[10];

        result[CENTER_INDEX] = bucket & 0xFFF;

        calculateShades(result, r, g, b, Shade.LIGHTER);
        calculateShades(result, r, g, b, Shade.DARKER);

        return result;
    }

    private void calculateShades(
            Integer[] result,
            Integer r,
            Integer g,
            Integer b,
            Shade shade
    ) {
        Double[] amounts = shade.amounts;
        Integer[] order = shade.order;

        double previousAmount = 0.0;

        for (int k = 0; k < amounts.length; k++) {
            int index = order[k];

            double amount = Math.max(amounts[k], previousAmount);

            double maxAmount = k < amounts.length - 1
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

    private double findUniqueAmount(
            Integer[] result,
            Integer currentIndex,
            Integer r,
            Integer g,
            Integer b,
            Double startAmount,
            Double minAmount,
            Double maxAmount,
            Shade shade
    ) {
        int startStep = (int) Math.round(startAmount / STEP) + 1;
        int minStep = (int) Math.round(minAmount / STEP) + 1;
        int maxStep = (int) Math.round(maxAmount / STEP);

        startStep = Math.max(startStep, minStep);

        for (int step = startStep; step <= maxStep; step++) {
            double amount = step * STEP;

            int bucket = createBucket(
                    r, g, b, amount, shade
            );

            if (!isDuplicate(result, bucket, currentIndex, shade)) {
                return amount;
            }
        }

        return startAmount;
    }

    private Boolean isDuplicate(
            Integer[] result,
            Integer bucket,
            Integer currentIndex,
            Shade shade
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

    private Integer createBucket(
            Integer r,
            Integer g,
            Integer b,
            Double amount,
            Shade shade
    ) {
        int newR = shade.adjustChannel(r, amount);
        int newG = shade.adjustChannel(g, amount);
        int newB = shade.adjustChannel(b, amount);

        return (newR << 8) | (newG << 4) | newB;
    }
}