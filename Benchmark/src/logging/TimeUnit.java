package logging;

public class TimeUnit {
    public static final TimeUnit NANO = new TimeUnit("ns", 1);
    public static final TimeUnit MICRO = new TimeUnit("us", 1_000);
    public static final TimeUnit MILLI = new TimeUnit("ms", 1_000_000);
    public static final TimeUnit SEC = new TimeUnit("sec", 1_000_000_000);

    private final String symbol;
    private final long nanosPerUnit;

    // Private constructor to prevent external instantiation
    private TimeUnit(String symbol, long nanosPerUnit) {
        this.symbol = symbol;
        this.nanosPerUnit = nanosPerUnit;
    }

    // Convert FROM nanoseconds TO this unit
    public double fromNano(long nanoseconds) {
        return (double) nanoseconds / nanosPerUnit;
    }

    // Convert TO nanoseconds FROM this unit
    public long toNano(double value) {
        return (long) (value * nanosPerUnit);
    }

    // Convert BETWEEN any two units
    public static double convert(double value, TimeUnit fromUnit, TimeUnit toUnit) {
        return value * fromUnit.nanosPerUnit / (double) toUnit.nanosPerUnit;
    }

    public String getSymbol() {
        return symbol;
    }
    // Auto-formatting (e.g., "1.500 ms")
    public String format(double value) {
        return String.format("%.3f %s", value, symbol);
    }

}
