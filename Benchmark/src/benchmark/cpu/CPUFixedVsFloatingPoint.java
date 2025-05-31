package benchmark.cpu;

import bench.IBenchmark;

public class CPUFixedVsFloatingPoint implements IBenchmark {

    private int result;
    private int size;
    private NumberRepresentation option;

    @Override
    public void initialize(Object ... params) {
        this.size = (Integer)params[0];
        this.option = (NumberRepresentation)params[1];
    }


    public void warmUp() {
        for (int i = 0; i < size/10; ++i) {
            result += i >> 8; // fixed point warmup (division by 256 optimized as right shift)
            result += (int)(i / 256.0); // floating point warmup
        }
    }

    @Override
    @Deprecated
    public void run() {
    }

    @Override
    public void run(Object ...options) {
        result = 0;

        switch ((NumberRepresentation) options[0]) {
            case FLOATING:
                for (int i = 0; i < size; ++i)
                    result += i / 256.0;
                break;
            case FIXED:
                for (int i = 0; i < size; ++i)
                    result += i >> 8; // equivalent to i/256 but much faster
                //optimized with right shift (since 256 is 2^8)
                break;
            default:
                break;
        }

    }

    @Override
    public void cancel() {

    }

    @Override
    public void clean() {
        result = 0;
    }

    public String getResult() {
        return String.valueOf(result);
    }

}
