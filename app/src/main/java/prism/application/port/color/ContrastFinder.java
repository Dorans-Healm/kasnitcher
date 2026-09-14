package prism.application.port.color;

public class ContrastFinder {

    public Integer findBestByAverage(Integer[] buckets) {
        Double[] luminances = this.getLuminances(buckets);

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

    public int findBestByWorst(Integer[] buckets) {
        Double[] luminances = this.getLuminances(buckets);

        int bestGray = 0;
        double bestWorstContrast = -1;

        for (int gray = 0; gray <= 255; gray++) {
            double grayLuminance = relativeLuminance(gray, gray, gray);

            double worstContrast = Double.MAX_VALUE;
            for (Double luminance : luminances) {
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

    public Double[] getLuminances(Integer[] buckets) {
        Double[] luminances = new Double[buckets.length];

        for (int i = 0; i < buckets.length; i++) {
            int r = ((buckets[i] >> 8) & 0xF) * 17;
            int g = ((buckets[i] >> 4) & 0xF) * 17;
            int b = (buckets[i] & 0xF) * 17;

            luminances[i] = this.relativeLuminance(r, g, b);
        }

        return luminances;
    }

    private Double relativeLuminance(Integer r, Integer g, Integer b) {
        double rl = linearize(r / 255.0);
        double gl = linearize(g / 255.0);
        double bl = linearize(b / 255.0);

        return 0.2126 * rl + 0.7152 * gl + 0.0722 * bl;
    }

    private Double linearize(Double c) {
        return c <= 0.03928
                ? c / 12.92
                : Math.pow((c + 0.055) / 1.055, 2.4);
    }

    private Double contrastRatio(Double l1, Double l2) {
        double lighter = Math.max(l1, l2);
        double darker = Math.min(l1, l2);

        return (lighter + 0.05) / (darker + 0.05);
    }
}