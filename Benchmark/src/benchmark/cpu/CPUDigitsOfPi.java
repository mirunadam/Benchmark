package benchmark.cpu;

import bench.IBenchmark;

import java.math.BigDecimal;
import java.math.MathContext;

public class CPUDigitsOfPi implements IBenchmark {

    private int digits;
    private BigDecimal pi;
    private boolean running = true;

    public void initialize(Object... params) {
        this.digits = (Integer) params[0];
        this.pi = BigDecimal.ZERO;
    }
    public void run() {
        run(0); // Default algorithm
    }

    public void run(Object... options){
        int option =(Integer)options[0];
        switch(option){
            case 0:
                computePiByLeibniz();
                break;
            case 1:
                computePiByNilakantha();
                break;
            case 2:
                computePiUsingMaths();
                break;
            default:
                throw new IllegalArgumentException("Invalid option");
        }
    }
    private void computePiByLeibniz() {
        MathContext mc = new MathContext(digits + 5); //to set the precision for all BigDecimal calculations
        BigDecimal sum = BigDecimal.ZERO;
        boolean add = true;

        for (int i = 0; i < digits * 10; i++) { //needs more terms for precision
            BigDecimal denominator = BigDecimal.valueOf(2 * i + 1);
            BigDecimal term = BigDecimal.ONE.divide(denominator, mc);
            sum = add ? sum.add(term, mc) : sum.subtract(term, mc);
            add = !add;

            if (!running) break; // Allow canceling
        }

        this.pi = sum.multiply(BigDecimal.valueOf(4), mc);
    }

    private void computePiByNilakantha() {
        MathContext mc = new MathContext(digits + 5); // to set the precision for all BigDecimal calculations
        BigDecimal sum = BigDecimal.valueOf(3);
        boolean add = true;

        for (int i = 2, count = 0; count < digits * 2; i += 2, count++) {
            BigDecimal denom = BigDecimal.valueOf(i)
                    .multiply(BigDecimal.valueOf(i + 1), mc)
                    .multiply(BigDecimal.valueOf(i + 2), mc);
            BigDecimal term = BigDecimal.valueOf(4).divide(denom, mc);
            sum = add ? sum.add(term, mc) : sum.subtract(term, mc);
            add = !add;

            if (!running) break;
        }

        this.pi = sum;
    }

    private void computePiUsingMaths() {
        MathContext mc = new MathContext(digits + 10); // Extra buffer
        BigDecimal arctan1_5 = arctan(BigDecimal.valueOf(1).divide(BigDecimal.valueOf(5), mc), mc);
        BigDecimal arctan1_239 = arctan(BigDecimal.valueOf(1).divide(BigDecimal.valueOf(239), mc), mc);

        pi = arctan1_5.multiply(BigDecimal.valueOf(16), mc)
                .subtract(arctan1_239.multiply(BigDecimal.valueOf(4), mc), mc);
    }

    private BigDecimal arctan(BigDecimal x, MathContext mc) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal term = x;
        BigDecimal xSquared = x.multiply(x, mc);
        int i = 1;
        boolean add = true;

        while (term.abs().compareTo(BigDecimal.ZERO) > 0 && i < digits * 5) {
            if (add) {
                result = result.add(term, mc);
            } else {
                result = result.subtract(term, mc);
            }

            i += 2;
            term = term.multiply(xSquared, mc).divide(BigDecimal.valueOf(i), mc);
            add = !add;

            if (!running) break; // support cancellation
        }

        return result;
    }

    public void warmUp() {
        // Compute first 1000 digits as warmup
        int originalDigits = this.digits;
        this.digits = 1000;
        computePiByNilakantha(); // Warmup using Nilakantha(faster)
        this.digits = originalDigits; // Restore original
    }

    public void clean() {
        pi = null;
    }

    public void cancel() {
        running = false;
    }
    public BigDecimal getPi() {
        return pi;
    }

    @Override
    public String getResult() {
        return "";
    }
}

