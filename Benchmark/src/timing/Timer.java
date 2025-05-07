package timing;

public class Timer implements ITimer {
    private long startTime;
    private long totalTime;
    private long resumeTime;

    @Override
    public void start() {
        totalTime = 0;
        startTime = System.nanoTime();
    }

    @Override
    public long stop() {
        totalTime += System.nanoTime() - startTime;
        return totalTime;
    }

    @Override
    public void resume() {
        resumeTime = System.nanoTime();
    }

    @Override
    public long pause() {
        long elapsed = System.nanoTime() - resumeTime;
        totalTime += elapsed;
        return elapsed;
    }
}
