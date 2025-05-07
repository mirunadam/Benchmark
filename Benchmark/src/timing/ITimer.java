package timing;

public interface ITimer {
    void start();
    long stop();       // Total elapsed time since start
    void resume();
    long pause();      // Time since last resume/start
}
