package prism.application.port;


public class ColorWriter {

    private static final double[] LIGHTER_AMOUNTS = {
            0.80, 0.60, 0.40, 0.20
    };

    private static final double[] DARKER_AMOUNTS = {
            0.20, 0.40, 0.60, 0.75, 0.85
    };

    public int[] calculateSpectrum(int bucket) {
        int r = (bucket >> 8) & 0xF;
        int g = (bucket >> 4) & 0xF;
        int b = bucket & 0xF;

        int[] result = new int[10];

        result[4] = bucket & 0xFFF;

        calculateLighter(result, r, g, b);
        calculateDarker(result, r, g, b);

        return result;
    }

    private void calculateLighter(int[] result, int r, int g, int b) {
        for (int i = 0; i < LIGHTER_AMOUNTS.length; i++) {
            double amount = LIGHTER_AMOUNTS[i];

            result[i] = createLighterBucket(r, g, b, amount);

            if (i > 0 && result[i] == result[i - 1]) {
                amount = findUniqueLighterAmount(r, g, b, amount, result[i - 1]);
                result[i] = createLighterBucket(r, g, b, amount);
            }
        }

        if (result[3] == result[4]) {
            double amount = LIGHTER_AMOUNTS[3];
            while (amount < 1.0) {
                amount += 0.01;
                result[3] = createLighterBucket(r, g, b, amount);
                if (result[3] != result[4]) break;
            }
        }
    }

    private void calculateDarker(int[] result, int r, int g, int b) {
        for (int i = 0; i < DARKER_AMOUNTS.length; i++) {
            double amount = DARKER_AMOUNTS[i];
            int index = i + 5;

            result[index] = createDarkerBucket(r, g, b, amount);

            if (result[index] == result[index - 1]) {
                amount = findUniqueDarkerAmount(r, g, b, amount, result[index - 1]);
                result[index] = createDarkerBucket(r, g, b, amount);
            }
        }
    }

    private double findUniqueLighterAmount(
            int r, int g, int b, double amount, int previousBucket
    ) {
        double step = 0.01;

        while (amount > 0.0) {
            amount -= step;

            int bucket = createLighterBucket(r, g, b, amount);

            if (bucket != previousBucket) {
                return amount;
            }
        }

        return amount;
    }

    private double findUniqueDarkerAmount(
            int r, int g, int b, double amount, int previousBucket
    ) {
        double step = 0.01;

        while (amount < 1.0) {
            amount += step;

            int bucket = createDarkerBucket(r, g, b, amount);

            if (bucket != previousBucket) {
                return amount;
            }
        }

        return amount;
    }

    private int createLighterBucket(int r, int g, int b, double amount) {
        int newR = lighten(r, amount);
        int newG = lighten(g, amount);
        int newB = lighten(b, amount);
        return (newR << 8) | (newG << 4) | newB;
    }

    private int createDarkerBucket(int r, int g, int b, double amount) {
        int newR = darken(r, amount);
        int newG = darken(g, amount);
        int newB = darken(b, amount);
        return (newR << 8) | (newG << 4) | newB;
    }

    private static int lighten(int value, double amount) {
        return (int) Math.round(value + (15 - value) * amount);
    }

    private static int darken(int value, double amount) {
        return (int) Math.round(value * (1.0 - amount));
    }
}