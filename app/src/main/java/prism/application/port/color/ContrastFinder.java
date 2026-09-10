package prism.application.port.color;

public class ContrastFinder {

    public int findBestByAverage(int[] buckets) {
        double[] luminances = this.getLuminances(buckets);

        int bestGray = 0;
        double bestAvgContrast = -1;

        for (int gray = 0; gray <= 255; gray++) {
            double grayLuminance = this.relativeLuminance(gray, gray, gray);

            double totalContrast = 0;
            for (double luminance : luminances) {
                totalContrast += this.contrastRatio(grayLuminance, luminance);
            }
            double avgContrast = totalContrast / luminances.length;

            if (avgContrast > bestAvgContrast) {
                bestAvgContrast = avgContrast;
                bestGray = gray;
            }
        }

        return bestGray;
    }

    public int findBestByWorst(int[] buckets) {
        double[] luminances = this.getLuminances(buckets);

        int bestGray = 0;
        double bestWorstContrast = -1;

        for (int gray = 0; gray <= 255; gray++) {
            double grayLuminance = relativeLuminance(gray, gray, gray);

            double worstContrast = Double.MAX_VALUE;
            for (double luminance : luminances) {
                worstContrast = Math.min(
                        worstContrast,
                        contrastRatio(grayLuminance, luminance)
                );
            }

            if (worstContrast > bestWorstContrast) {
                bestWorstContrast = worstContrast;
                bestGray = gray;
            }
        }

        return bestGray;
    }

    public double[] getLuminances(int[] buckets) {
        double[] luminances = new double[buckets.length];

        for (int i = 0; i < buckets.length; i++) {
            int r = ((buckets[i] >> 8) & 0xF) * 17;
            int g = ((buckets[i] >> 4) & 0xF) * 17;
            int b = (buckets[i] & 0xF) * 17;

            luminances[i] = this.relativeLuminance(r, g, b);
        }

        return luminances;
    }

    private double relativeLuminance(int r, int g, int b) {
        double rl = linearize(r / 255.0);
        double gl = linearize(g / 255.0);
        double bl = linearize(b / 255.0);
        return 0.2126 * rl + 0.7152 * gl + 0.0722 * bl;
    }

    private double linearize(double c) {
        return c <= 0.03928
                ? c / 12.92
                : Math.pow((c + 0.055) / 1.055, 2.4);
    }

    private double contrastRatio(double l1, double l2) {
        double lighter = Math.max(l1, l2);
        double darker = Math.min(l1, l2);
        return (lighter + 0.05) / (darker + 0.05);
    }
}