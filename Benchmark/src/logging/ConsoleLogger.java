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
    public void writeTime(long value, TimeUnit unit) {
        double convertedTime = unit.fromNano(value);
        System.out.printf("%.3f %s%n", convertedTime, unit.getSymbol());
    }

    public void writeTime(String string, long value, TimeUnit unit) {
        double convertedTime = unit.fromNano(value);
        System.out.printf("%s %.3f %s%n", string, convertedTime, unit.getSymbol());
    }

    @Override
    public void close() {
        // Nothing to close for console
    }
}
