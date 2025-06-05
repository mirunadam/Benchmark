package testbench;

import bench.cpu.HDDRandomAccess;
import bench.IBenchmark;

/**
 * Test class for running and validating HDD/SSD random access benchmarks.
 * Executes read/write tests in both fixed-size and fixed-time modes.
 */

public class TestHDDRandomAccess {
    public static void main(String[] args) {
        // Create benchmark instance
        IBenchmark benchmark = new HDDRandomAccess();

        // Create a 1GB test file (adjust size if needed)
        long fileSize = 1024L * 1024 * 1024; // 1GB

        // Initialize benchmark (creates the temp file)
        System.out.println("Initializing benchmark with 1GB file...");
        benchmark.initialize(fileSize);

        // Test 1: Random Read - Fixed Size
        System.out.println("\nRunning: Random Read - Fixed Size (4KB buffer)");
        benchmark.run(new Object[]{"r", "fs", 4 * 1024});
        System.out.println("Result: " + benchmark.getResult());

        // Test 2: Random Read - Fixed Time
        System.out.println("\nRunning: Random Read - Fixed Time (4KB buffer, 5 seconds)");
        benchmark.run(new Object[]{"r", "ft", 4 * 1024});
        System.out.println("Result: " + benchmark.getResult());

        // Test 3: Random Write - Fixed Size
        System.out.println("\nRunning: Random Write - Fixed Size (4KB buffer)");
        benchmark.run(new Object[]{"w", "fs", 4 * 1024});
        System.out.println("Result: " + benchmark.getResult());

        // Test 4: Random Write - Fixed Time
        System.out.println("\nRunning: Random Write - Fixed Time (4KB buffer, 5 seconds)");
        benchmark.run(new Object[]{"w", "ft", 4 * 1024});
        System.out.println("Result: " + benchmark.getResult());

        // Clean up the temporary file
        benchmark.clean();
        System.out.println("\nTemporary file cleaned up.");
    }
}
