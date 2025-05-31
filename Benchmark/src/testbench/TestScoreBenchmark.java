package testbench;

import bench.ScoreBenchmark;
import bench.ScoreBenchmark.BenchmarkData;

import java.util.List;

public class TestScoreBenchmark {
    public static void main(String[] args) {
        List<BenchmarkData> data = List.of(
                new BenchmarkData(100, 2, 2),
                new BenchmarkData(1000, 4, 16),
                new BenchmarkData(10000, 16, 256),
                new BenchmarkData(100000, 64, 1024),
                new BenchmarkData(1000000, 256, 4096)
        );

        ScoreBenchmark.printScores(data);

        //S4 for one case
        double s4 = ScoreBenchmark.scoreS4(data.get(3));
        System.out.printf("S4 for entry 4: %.4f%n", s4);
    }
}
