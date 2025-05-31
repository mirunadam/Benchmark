package bench;

public class DummyBenchmark implements IBenchmark {

    private boolean running = true;
    private int lastIteration = -1;
    private double lastResult = 0;

    public void run() {
        run(1000000); // default value
    }

    public void run(Object... params) {
        int iterations = (int) params[0];

        for (int i = 0; i < iterations && running; i++) {
            Math.sqrt(i); // Simulate CPU load
        }
    }

    @Override
    public void initialize(Object... params) {
        System.out.println("DummyBenchmark initialized with " + params.length + " param(s)");
    }
    public void warmUp() {
        int warmupIterations = 10000; // 1% of the default run, enough to warm up the JIT

        for (int i = 0; i < warmupIterations && running; i++) {
            lastResult = Math.sqrt(i); // Same operation as in run
            lastIteration = i;
        }

        System.out.println("DummyBenchmark warmup completed.");
    }

    @Override
    public void clean() {
        System.out.println("DummyBenchmark cleanup done.");
    }

    @Override
    public String getResult() {
        return running
                ? String.format("Completed %d iterations. Last sqrt(%d) = %.5f", lastIteration + 1, lastIteration, lastResult)
                : "Benchmark was cancelled.";
    }

    @Override
    public void cancel() {
        running = false;
    }
}
