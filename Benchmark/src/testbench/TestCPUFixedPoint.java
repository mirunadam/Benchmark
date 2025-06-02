package testbench;

import logging.ILogger;
import logging.TimeUnit;
import timing.ITimer;
import timing.Timer;
import bench.cpu.CPUFixedPoint;

public class TestCPUFixedPoint {
    public static void main(String[] args) {
        ITimer timer = new Timer();
        ILogger log = new logging.ConsoleLogger();
        TimeUnit timeUnit = TimeUnit.MILLI;

        CPUFixedPoint bench = new CPUFixedPoint();
        final int size = 10_000_000; // 10 million iterations

        // Initialize benchmark
        bench.initialize(size);
        bench.warmUp();

        // Test 1: Arithmetic operations
        timer.start();
        bench.arithmeticTest(size);
        long arithmeticTime = timer.stop();
        int arithmeticOps = bench.countArithmeticOperations();
        double arithmeticMOPS = (arithmeticOps / (arithmeticTime / 1_000_000_000.0)) / 1_000_000;
        log.write("Arithmetic Test - Operations:", arithmeticOps);
        double arithmeticTimeConverted = timeUnit.fromNano(arithmeticTime);
        log.write("Time (ms): " + timeUnit.format(arithmeticTimeConverted));
        log.write("MOPS: " + String.format("%.2f", arithmeticMOPS));
        log.write("");

        // Test 2: Branching operations
        timer.start();
        bench.branchingTest(size);
        long branchingTime = timer.stop();
        int branchingOps = bench.countBranchingOperations();
        double branchingMOPS = (branchingOps / (branchingTime / 1_000_000_000.0)) / 1_000_000;
        log.write("Branching Test - Operations:", branchingOps);
        double branchingTimeConverted = timeUnit.fromNano(branchingTime);
        log.write("Time (ms): " +  timeUnit.format(branchingTimeConverted));
        log.write("MOPS: " + String.format("%.2f", branchingMOPS));
        log.write("");

        // Test 3: Array access operations
        timer.start();
        bench.arrayAccessTest(size);
        long arrayTime = timer.stop();
        int arrayOps = bench.countArrayAccessOperations();
        double arrayMOPS = (arrayOps / (arrayTime / 1_000_000_000.0)) / 1_000_000;
        log.write("Array Access Test - Operations:", arrayOps);
        double arrayTimeConverted = timeUnit.fromNano(arrayTime);
        log.write("Time (ms): " + timeUnit.format(arrayTimeConverted));
        log.write("MOPS: " + String.format("%.2f", arrayMOPS));


        bench.clean();
        log.close();
    }
}