package testbench;

import bench.DummyBenchmark;
import bench.IBenchmark;
import logging.ConsoleLogger;
import logging.ILogger;
import timing.ITimer;
import timing.Timer;

public class TestDummyBenchmark {
    public static void main(String[] args) {
        ITimer timer = new Timer();
        ILogger logger = new ConsoleLogger();
        IBenchmark benchmark = new DummyBenchmark();

        benchmark.initialize(1000000);
        timer.start();
        benchmark.run(1000000);
        long time = timer.stop();

        logger.write("Execution time:", time, "ns");

        benchmark.clean();
        logger.close();
    }
}
