package logging;

public interface ILogger {
    void write(long value);
    void write(String message);
    void write(Object... values);
    void close(); // Optional (empty for ConsoleLogger)
}
