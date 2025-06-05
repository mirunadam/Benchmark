package testbench;

import bench.cpu.CPURecursionLoopUnrolling;
import bench.ScoreBenchmark;
import bench.ScoreBenchmark.BenchmarkData;

import java.util.ArrayList;
import java.util.List;

public class TestScoreBenchmark {
    public static void main(String[] args) {
        List<BenchmarkData> realData = new ArrayList<>();
        int[] unrollLevels = {0, 1, 5, 15};
        int size = 2_000_000;

        for (int level : unrollLevels) {
            CPURecursionLoopUnrolling bench = new CPURecursionLoopUnrolling();
            bench.initialize(size);

            long start = System.nanoTime();
            if (level == 0) {
                bench.run(false);
            } else {
                bench.run(true, level);
            }
            long end = System.nanoTime();

            // Get real benchmark data from the run
            BenchmarkData d = new BenchmarkData(size, (end - start) / 1_000_000.0, bench.getLastCounter());
            System.out.println();
            realData.add(d);
        }

        // Print performance score comparison for real benchmark runs
        System.out.println("\n--- Composite Score Analysis ---");
        ScoreBenchmark.printScores(realData);
    }
}
