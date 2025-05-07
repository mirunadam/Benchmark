package testbench;

import bench.DemoBenchmark;
import bench.IBenchmark;
import bench.SleepBenchmark;
import logging.ConsoleLogger;
import logging.ILogger;
import logging.TimeUnit;
import timing.ITimer;
import timing.Timer;

public class TestDemoBench {
    public static void main(String[] args) {
        ITimer timer = new Timer();
        ILogger log = new ConsoleLogger();

        IBenchmark bench = new DemoBenchmark();
        int size = (int)Math.pow(10, 6);
        log.write("--- Running benchmark with size: " + size+ " ---");

        bench.initialize(size);
        timer.start();
        bench.run();
        long stoptime = timer.stop();

        log.write("Finished in", stoptime, "ns");
        bench.clean();

        log.write("\n--- Testing timer accuracy with Thread.sleep ---");
        IBenchmark sleepBench = new SleepBenchmark();
        int n = 100;
        sleepBench.initialize(n); // 100ms sleep
        timer.start();
        sleepBench.run();
        stoptime = timer.stop();
        double expected = n * Math.pow(10, 6); // 100ms in ns
        double offset = 100.0 * (stoptime - expected) / expected;
        log.write(String.format("Expected: %.2f ns, Actual: %d ns, Offset: %.2f%%", expected, stoptime, offset));

        log.write("\n--- Testing pause and resume using a loop ---");
        int workloand = 100;
        bench.initialize(workloand);
        for (int i = 0; i < 12; ++i) {
            timer.resume();
            bench.run();
            long time = timer.pause();
            log.write("Run " + i + ": "+ time + " ms");
        }
        TimeUnit timeUnit = TimeUnit.MILLI;
        log.writeTime("Finished in", timer.stop(), timeUnit);
        //log.write("Finished in", timer.stop(), "ns");
        log.close();
    }
}
