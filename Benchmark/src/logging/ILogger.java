package logging;

public interface ILogger {
    void write(long value);
    void write(String message);
    void write(Object... values);
    void writeTime(long value, TimeUnit unit);
    void writeTime(String string, long value, TimeUnit unit);
    void close(); // Optional (empty for ConsoleLogger)
}
