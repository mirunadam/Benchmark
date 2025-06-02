package testbench;

import bench.cpu.CPURecursionLoopUnrolling;

public class TestCPURecursionLoopUnrolling {
    public static void main(String[] args) {
        CPURecursionLoopUnrolling bench = new CPURecursionLoopUnrolling();
        bench.initialize(2_000_000); // Check primes up to 2 million

        System.out.println("No unrolling:");
        bench.run(false);

        System.out.println("\nUnroll level 1:");
        bench.run(true, 1);

        System.out.println("\nUnroll level 5:");
        bench.run(true, 5);

        System.out.println("\nUnroll level 15:");
        bench.run(true, 15);
    }
}
