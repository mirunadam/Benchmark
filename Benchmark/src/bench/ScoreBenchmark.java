package bench;

import java.util.List;

public class ScoreBenchmark {
    public static class BenchmarkData {
        public double n;  // size
        public double t;  // time
        public double r;  // output/result

        public BenchmarkData(double n, double t, double r) {
            this.n = n;
            this.t = t;
            this.r = r;
        }
    }

    public static double scoreS1(BenchmarkData d) {
        return d.n / d.t;
    }

    public static double scoreS2(BenchmarkData d) {
        return d.n / (d.t * d.r);
    }

    public static double scoreS3(BenchmarkData d) {
        return d.n / (log2(d.t) * Math.sqrt(d.r) + 1);
    }

    public static double scoreS4(BenchmarkData d) {
        return log2(d.n / log2(d.t));
    }

    private static double log2(double value) {
        return Math.log(value) / Math.log(2);
    }

    // Display all scores for a given list of benchmark data
    public static void printScores(List<BenchmarkData> dataList) {
        System.out.printf("%-10s %-10s %-10s %-10s %-10s %-10s%n", "n", "t", "r", "S1", "S2", "S3");
        for (BenchmarkData d : dataList) {
            double s1 = scoreS1(d);
            double s2 = scoreS2(d);
            double s3 = scoreS3(d);
            System.out.printf("%-10.2f %-10.2f %-10.2f %-10.4f %-10.4f %-10.4f%n", d.n, d.t, d.r, s1, s2, s3);
        }
    }
}
