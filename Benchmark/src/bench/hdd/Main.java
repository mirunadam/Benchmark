package bench.hdd;

import bench.IBenchmark;
import bench.hdd.HDDWriteSpeed;

public class Main {
    public static void main(String[] args) {
        HDDWriteSpeed benchmark = new HDDWriteSpeed();
        benchmark.run("fs", true);
        System.out.println(benchmark.getResult());
    }

}
