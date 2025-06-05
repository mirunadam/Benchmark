package bench.cpu;

import bench.IBenchmark;

// Benchmarks recursive prime number summation with and without loop unrolling.
// Measures performance and stack usage while handling possible stack overflows.

public class CPURecursionLoopUnrolling implements IBenchmark {
    private int size;
    private long lastReached = 0;
    private int lastCounter = 0;

    @Override
    public void initialize(Object... params) {
        this.size = (int) params[0];
    }

    @Override
    public void run() {
        run(false); // default: no unrolling
    }

    @Override
    public void run(Object... params) {
        boolean unroll = (boolean) params[0];
        lastReached = 0;
        lastCounter = 0;

        long startTime = System.nanoTime();

        try {
            if (!unroll) {
                recursive(1, size, 0);
                // Required output for non-unrolled case
                System.out.printf("Reached nr %d/%d after %d calls.\n", lastReached, size, lastCounter);
            } else {
                int unrollLevel = (int) params[1];
                recursiveUnrolled(1, unrollLevel, size, 0);
                // Only print for low unroll levels (per assignment)
                if (unrollLevel <= 5) {
                    System.out.printf("Reached nr %d/%d at %d levels after %d calls\n", lastReached, size, unrollLevel, lastCounter);
                }
            }
        } catch (StackOverflowError e) {
            System.out.printf("Reached nr %d/%d after %d calls (Stack overflow exe caught).\n", lastReached, size, lastCounter);
        }

        long endTime = System.nanoTime();
        System.out.printf("Finished in %.4f Milli\n", (endTime - startTime) / 1_000_000.0);
    }

    private long recursive(long start, long size, int counter) {
        if (start > size) return 0;

        lastReached = start;
        lastCounter = counter;

        long result = isPrime((int) start) ? start : 0;
        return result + recursive(start + 1, size, counter + 1);
    }

    private long recursiveUnrolled(long start, int unrollLevel, int size, int counter) {
        if (start > size) return 0;

        long sum = 0;
        for (int i = 0; i < unrollLevel && start <= size; i++, start++) {
            if (isPrime((int) start)) {
                sum += start;
            }
            lastReached = start;
            lastCounter = counter + i;
        }

        return sum + recursiveUnrolled(start, unrollLevel, size, lastCounter + 1);
    }

    private boolean isPrime(int x) {
        if (x <= 2) return true;
        for (int i = 2; i <= Math.sqrt(x); i++) {
            if (x % i == 0) return false;
        }
        return true;
    }

    @Override
    public void cancel() {
        // Not implemented
    }

    @Override
    public void clean() {

    }
    public void warmUp() {
        recursive(1, Math.min(size, 1000), 0);
    }

    @Override
    public String getResult() {
        return String.format("Last reached: %d, calls: %d", lastReached, lastCounter);
    }
}