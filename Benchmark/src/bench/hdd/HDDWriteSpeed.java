package bench.hdd;

import java.io.IOException;

import bench.IBenchmark;

public class HDDWriteSpeed implements IBenchmark {

    private String result;
    @Override
    public void initialize(Object... params) {
        // Optional: Use this to initialize test parameters if needed
    }

    @Override
    public void warmUp() {
        // Optional: implement warm-up logic to prepare disk cache, etc.
    }

    @Override
    public void run() {
        System.out.println("hello");
    }

    @Override
    public void run(Object... options) {
        if (options.length < 2) {
            throw new IllegalArgumentException("Expected two parameters: mode (fs/fb) and clean (boolean)");
        }

        FileWriter writer = new FileWriter();
        String option = (String) options[0];     // "fs" or "fb"
        boolean clean = (Boolean) options[1];    // true/false

        String prefix = "D:\\benchmark\\testfile_";  // You can change to your valid local path
        String suffix = ".dat";

        int minIndex = 0;
        int maxIndex = 8;

        long fileSize = 256L * 1024 * 1024; // 256 MB
        int bufferSize = 4 * 1024;          // 4 KB

        double score;
        try {
            if (option.equals("fs")) {
                score = writer.streamWriteFixedFileSize(prefix, suffix, minIndex, maxIndex, fileSize, clean);
            } else if (option.equals("fb")) {
                score = writer.streamWriteFixedBufferSize(prefix, suffix, minIndex, maxIndex, bufferSize, clean);
            } else {
                throw new IllegalArgumentException("Invalid mode: " + option + " (use 'fs' or 'fb')");
            }
            result = String.format("Final write speed: %.2f MB/sec", score);
        } catch (IOException e) {
            e.printStackTrace();
            result = "Error during benchmark";
        }
    }

    @Override
    public void clean() {
        // Optional: implement cleanup logic if needed
    }

    @Override
    public String getResult() {
        return result; // You can return the final average write speed here if desired
    }

    @Override
    public void cancel() {
        // Optional: implement cancel logic
    }
}
