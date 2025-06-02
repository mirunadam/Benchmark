package bench.cpu;

import bench.IBenchmark;

public class CPUFixedPoint implements IBenchmark {
    private int size;
    private int[] num = {0, 1, 2, 3};
    private int[] a, b, c;
    private int[] res;
    private int j, k, l; // Variables for arithmetic operations

    @Override
    public void initialize(Object... params) {
        this.size = (Integer) params[0];
        this.a = new int[size];
        this.b = new int[size];
        this.c = new int[size];
        this.res = new int[size];

        // Initialize arrays with values
        for (int i = 0; i < size; i++) {
            a[i] = i % 4;
            b[i] = (i + 1) % 4;
            c[i] = (i + 2) % 4;
        }

        // Initialize variables
        j = 1;
        k = 2;
        l = 3;
    }

    public void warmUp() {
        // Warm up all test methods with smaller size
        arithmeticTest(size / 100);
        branchingTest(size / 100);
        arrayAccessTest(size / 100);
    }

    @Override
    public void run() {
        // Run all tests by default
        arithmeticTest(size);
        branchingTest(size);
        arrayAccessTest(size);
    }

    @Override
    public void run(Object... options) {
        // Run specific test based on options
        if (options.length > 0) {
            String testType = (String) options[0];
            switch (testType) {
                case "arithmetic":
                    arithmeticTest(size);
                    break;
                case "branching":
                    branchingTest(size);
                    break;
                case "array":
                    arrayAccessTest(size);
                    break;
                default:
                    run(); // Run all if invalid option
            }
        } else {
            run(); // Run all if no options provided
        }
    }

    @Override
    public void cancel() {
        // Not implemented for this benchmark
    }

    @Override
    public void clean() {
        // Clean up arrays
        a = b = c = res = null;
        System.gc();
    }

    // Integer arithmetic test
    public void arithmeticTest(int iterations) {
        for (int i = 0; i < iterations; i++) {
            j = num[1] * (k - j) * (l - k);
            k = num[3] * k - (l - j) * k;
            l = (l - k) * (num[2] + j);

            // Prevent array index out of bounds
            int index1 = Math.abs(l - 2) % res.length;
            int index2 = Math.abs(k - 2) % res.length;

            res[index1] = j + k + l;
            res[index2] = j * k * l;
        }
    }

    // Branching test
    public void branchingTest(int iterations) {
        for (int i = 0; i < iterations; i++) {
            if (j == 1) {
                j = num[2];
            } else {
                j = num[3];
            }

            if (j > 2) {
                j = num[0];
            } else {
                j = num[1];
            }

            if (j < 1) {
                j = num[1];
            } else {
                j = num[0];
            }
        }
    }

    // Array access test
    public void arrayAccessTest(int iterations) {
        for (int i = 0; i < iterations; i++) {
            int index = i % size;
            c[index] = a[b[index]];

            // Simple swap operation
            int temp = a[index];
            a[index] = b[index];
            b[index] = temp;
        }
    }

    // Method to count operations in arithmeticTest
    public int countArithmeticOperations() {
        // Per iteration:
        // j = num[1] * (k - j) * (l - k); → 7 ops
        // k = num[3] * k - (l - j) * k; → 8 ops
        // l = (l - k) * (num[2] + j); → 6 ops
        // index1 calculation → 3 ops (abs, sub, mod)
        // index2 calculation → 3 ops (abs, sub, mod)
        // res[index1] = j + k + l; → 4 ops
        // res[index2] = j * k * l; → 4 ops
        // Loop: i < iterations, i++ → 2 ops
        // Total per iteration: 7 + 8 + 6 + 3 + 3 + 4 + 4 + 2 = 37 ops
        return 37 * size;
    }

    // Method to count operations in branchingTest
    public int countBranchingOperations() {
        // Per iteration:
        // Each if-else block: comparison + assignment → 2 ops per block
        // 3 if-else blocks → 6 ops
        // Loop: i < iterations, i++ → 2 ops
        // Total per iteration: 6 + 2 = 8 ops
        return 8 * size;
    }

    // Method to count operations in arrayAccessTest
    public int countArrayAccessOperations() {
        // Per iteration:
        // int index = i % size; → 2 ops
        // c[index] = a[b[index]]; → 4 ops
        // int temp = a[index]; → 2 ops
        // a[index] = b[index]; → 3 ops
        // b[index] = temp; → 2 ops
        // Loop: i < iterations, i++ → 2 ops
        // Total per iteration: 2 + 4 + 2 + 3 + 2 + 2 = 15 ops
        return 15 * size;
    }

    public String getResult(){
        return String.valueOf(res);
    }
}
