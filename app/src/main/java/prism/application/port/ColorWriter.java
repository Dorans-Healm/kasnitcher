package prism.application.port;

public class ColorWriter {

    private static final double[] LIGHTER_AMOUNTS = {
            0.20, 0.40, 0.60, 0.80
    };

    private static final double[] DARKER_AMOUNTS = {
            0.20, 0.40, 0.60, 0.75, 0.85
    };

    private static final int CENTER_INDEX = 4;
    private static final double STEP = 0.01;

    private enum Shade {
        LIGHTER(new int[]{3, 2, 1, 0}, LIGHTER_AMOUNTS) {
            @Override
            int adjustChannel(int value, double amount) {
                return (int) Math.round(
                        value + (15 - value) * amount
                );
            }
        },

        DARKER(new int[]{5, 6, 7, 8, 9}, DARKER_AMOUNTS) {
            @Override
            int adjustChannel(int value, double amount) {
                return (int) Math.round(
                        value * (1.0 - amount)
                );
            }
        };

        final int[] order;
        final double[] amounts;

        Shade(int[] order, double[] amounts) {
            this.order = order;
            this.amounts = amounts;
        }

        abstract int adjustChannel(int value, double amount);
    }

    public int[] calculateSpectrum(int bucket) {
        int r = (bucket >> 8) & 0xF;
        int g = (bucket >> 4) & 0xF;
        int b = bucket & 0xF;

        int[] result = new int[10];

        result[CENTER_INDEX] = bucket & 0xFFF;

        calculateShades(result, r, g, b, Shade.LIGHTER);
        calculateShades(result, r, g, b, Shade.DARKER);

        return result;
    }

    private void calculateShades(
            int[] result,
            int r,
            int g,
            int b,
            Shade shade
    ) {
        double[] amounts = shade.amounts;
        int[] order = shade.order;

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
            int[] result,
            int currentIndex,
            int r,
            int g,
            int b,
            double startAmount,
            double minAmount,
            double maxAmount,
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

    private boolean isDuplicate(
            int[] result,
            int bucket,
            int currentIndex,
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
            if (result[i] == bucket) {
                return true;
            }
        }

        return false;
    }

    private int createBucket(
            int r,
            int g,
            int b,
            double amount,
            Shade shade
    ) {
        int newR = shade.adjustChannel(r, amount);
        int newG = shade.adjustChannel(g, amount);
        int newB = shade.adjustChannel(b, amount);

        return (newR << 8) | (newG << 4) | newB;
    }
}