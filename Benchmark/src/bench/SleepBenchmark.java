package bench;

public class SleepBenchmark implements IBenchmark{
    private int n; // Sleep duration in ms
    private int[] array;
    private boolean running = true;

    public void initialize(Object... params) {
        this.n = (Integer) params[0];
    }

    public void run() {
        run(n); // default run with full array size
    }

    public void run(Object... params) {
        try {
            Thread.sleep(n);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void clean() {
        array = null;
        n = 0;
    }

    public void cancel() {
        running = false;
    }
}
