package bench;

public interface IBenchmark {
    void run();                   // Core benchmark code
    void run(Object... params);   // Overloaded with params
    void initialize(Object... params); // Setup/preparation
    void clean();                // Cleanup after run
    void cancel();              // Graceful cancel support
    void warmUp();
    String getResult();
}
