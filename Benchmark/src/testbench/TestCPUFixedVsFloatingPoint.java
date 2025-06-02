package testbench;

import logging.ConsoleLogger;
import logging.ILogger;
import logging.TimeUnit;
import timing.ITimer;
import timing.Timer;
import bench.IBenchmark;
import bench.cpu.CPUFixedVsFloatingPoint;
import bench.cpu.NumberRepresentation;

public class TestCPUFixedVsFloatingPoint {

    public static void main(String[] args) {
        ITimer timer = new Timer();
        ILogger log = /* new FileLogger("bench.log"); */new ConsoleLogger();
        TimeUnit timeUnit = TimeUnit.MILLI;

        IBenchmark bench = new CPUFixedVsFloatingPoint();
        final int size = 100_000_000; // 100 million iterations

        log.write("=== Fixed vs Floating Point Comparison ===");
        log.write("Test size: " + size + " iterations");
        log.write("");

        // Test FIXED point
        bench.initialize(size, NumberRepresentation.FIXED);
        bench.warmUp();
        timer.start();
        bench.run(NumberRepresentation.FIXED);
        long fixedTime = timer.stop();
        log.write("Fixed Point Results:");
        log.write("Result: " + bench.getResult());
        double fixedTimeConverted = timeUnit.fromNano(fixedTime);
        log.write("Time (ms): " +  timeUnit.format(fixedTimeConverted));
        double fixedMOPS = size / (fixedTime / 1_000_000_000.0) / 1_000_000;
        log.write("MOPS: " + String.format("%.2f", fixedMOPS));
        log.write("");

        // Test FLOATING point
        bench.initialize(size, NumberRepresentation.FLOATING);
        bench.warmUp();
        timer.start();
        bench.run(NumberRepresentation.FLOATING);
        long floatTime = timer.stop();
        log.write("Floating Point Results:");
        log.write("Result: " + bench.getResult());
        double floatTimeConverted = timeUnit.fromNano(floatTime);
        log.write("Time (ms): " +  timeUnit.format(floatTimeConverted));
        double floatMOPS = size / (floatTime / 1_000_000_000.0) / 1_000_000;
        log.write("MFLOPS: " + String.format("%.2f", floatMOPS));
        log.write("");

        // Performance comparison
        double ratio = (double)floatTime / fixedTime;
        log.write("Performance Comparison:");
        log.write("Fixed point was " + String.format("%.2f", ratio) + "x faster");


        bench.clean();
        log.close();
    }
}
