package bench.hdd;

import java.io.File;
import java.io.IOException;

import bench.IBenchmark;

public class HDDWriteSpeed implements IBenchmark {

    private String result;
    private final String prefix = "D:\\benchmark\\testfile_";  // Adjust if needed
    private final String suffix = ".dat";
    private final int repetitions = 5;

    // Keep track of created test files for cleaning
    private int maxFileIndex = -1;

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

        try {
            if (option.equals("fs")) {
                long fileSize = 512L * 1024 * 1024;
                int[] bufferSizes = {
                        1 * 1024,
                        4 * 1024,
                        16 * 1024,
                        64 * 1024,
                        256 * 1024,
                        1 * 1024 * 1024,
                        4 * 1024 * 1024,
                        16 * 1024 * 1024,
                        64 * 1024 * 1024
                };

                System.out.println("Running fs mode: fixed file size = 512MB, varying buffer sizes...");
                System.out.println("Buffer Size (KB), Average Write Speed (MB/s)");

                int fileIndex = 0;

                for (int bufSize : bufferSizes) {
                    double sumSpeed = 0;
                    for (int i = 0; i < repetitions; i++) {
                        double speed = writer.streamWriteFixedFileSize(
                                prefix, suffix, fileIndex, fileSize, bufSize, clean);
                        sumSpeed += speed;
                    }
                    double avgSpeed = sumSpeed / repetitions;
                    System.out.printf("%d, %.2f%n", bufSize / 1024, avgSpeed);
                    fileIndex++;
                }

                maxFileIndex = bufferSizes.length - 1;
                result = "fs benchmark completed. See printed speeds above.";

            } else if (option.equals("fb")) {
                int bufferSize = 2 * 1024;
                long[] fileSizes = {
                        1L * 1024 * 1024,
                        10L * 1024 * 1024,
                        100L * 1024 * 1024,
                        1024L * 1024 * 1024
                };

                System.out.println("Running fb mode: fixed buffer size = 2KB, varying file sizes...");
                System.out.println("File Size (MB), Average Write Speed (MB/s)");

                int fileIndex = 0;

                for (long fSize : fileSizes) {
                    double sumSpeed = 0;
                    for (int i = 0; i < repetitions; i++) {
                        double speed = writer.streamWriteFixedBufferSize(
                                prefix, suffix, fileIndex, bufferSize, fSize, clean);
                        sumSpeed += speed;
                    }
                    double avgSpeed = sumSpeed / repetitions;
                    System.out.printf("%d, %.2f%n", fSize / (1024 * 1024), avgSpeed);
                    fileIndex++;
                }

                maxFileIndex = fileSizes.length - 1;
                result = "fb benchmark completed. See printed speeds above.";

            } else {
                throw new IllegalArgumentException("Invalid mode: " + option + " (use 'fs' or 'fb')");
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = "Error during benchmark";
        }
    }


    @Override
    public void clean() {
        if (maxFileIndex < 0) {
            System.out.println("No files to clean.");
            return;
        }

        System.out.println("Cleaning up test files...");
        for (int i = 0; i <= maxFileIndex; i++) {
            File f = new File(prefix + i + suffix);
            if (f.exists()) {
                boolean deleted = f.delete();
                if (deleted) {
                    System.out.println("Deleted file: " + f.getAbsolutePath());
                } else {
                    System.out.println("Failed to delete file: " + f.getAbsolutePath());
                }
            }
        }
    }

    @Override
    public String getResult() {
        return result;
    }

    @Override
    public void cancel() {
        // Optional: implement cancel logic
    }
}
