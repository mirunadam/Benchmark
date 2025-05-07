package logging;

public class ConsoleLogger implements ILogger {

    @Override
    public void write(long value) {
        System.out.println(value);
    }

    @Override
    public void write(String message) {
        System.out.println(message);
    }

    @Override
    public void write(Object... values) {
        for (Object value : values) {
            System.out.print(value + " ");
        }
        System.out.println();
    }

    @Override
    public void close() {
        // Nothing to close for console
    }
}
