package testbench;

import bench.hdd.HDDWriteSpeed;
import bench.IBenchmark;
import bench.hdd.HDDWriteSpeed;

public class TestHDDWriteSpeed {

    public static void main(String[] args) {
        HDDWriteSpeed benchmark = new HDDWriteSpeed();
        benchmark.run("fb", true);
        System.out.println(benchmark.getResult());
        benchmark.clean();
    }

}
