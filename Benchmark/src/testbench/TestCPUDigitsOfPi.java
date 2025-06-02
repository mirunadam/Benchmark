package testbench;

import bench.cpu.CPUDigitsOfPi;
import bench.IBenchmark;
import logging.ConsoleLogger;
import logging.ILogger;
import logging.TimeUnit;
import timing.ITimer;
import timing.Timer;

import java.math.BigDecimal;

public class TestCPUDigitsOfPi {
    public static void main(String[] args) {
        ITimer timer = new Timer();
        ILogger log = new ConsoleLogger();
        IBenchmark bench = new CPUDigitsOfPi();

        int digits = 10000;
        int runs = 5;
        int algorithm = 0; // 0=Leibniz, 1=Nilakantha, 2=Math

        String algoName = switch (algorithm) {
            case 0 -> "Leibniz";
            case 1 -> "Nilakantha";
            case 2 -> "Math (arctan)";
            default -> "Unknown";
        };

        log.write("--- CPU Pi Digits Benchmark ---");
        log.write("Algorithm: ", algoName);
        log.write("Digits to compute: ", digits);
        log.write("Number of runs: ", runs);

        bench.initialize(digits);
        log.write("Starting warmup...");
        bench.warmUp();
        log.write("Warmup completed");

        for (int i = 0; i < runs; i++) {
            timer.start();
            bench.run(algorithm);
            long time = timer.stop();
            log.writeTime("Run " + (i + 1) + " completed in", time, TimeUnit.MILLI);
        }

        //print first digits
        BigDecimal result = ((CPUDigitsOfPi) bench).getPi();
        log.write("First 100 digits of Pi: " + result.toString().substring(0, 102));

        bench.clean();
        log.close();
    }
}
