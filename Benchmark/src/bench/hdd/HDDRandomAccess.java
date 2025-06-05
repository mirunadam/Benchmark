// File: src/bench/hdd/HDDRandomAccess.java
// -------------------------------------------------
// Implements:
//   - initialize(long fileSize) → creates a temp file of exactly fileSize bytes (filled randomly).
//   - run("r","fs",bufferSize) → random read fixed‐size (25 000 I/Os).
//   - run("r","ft",bufferSize) → random read fixed‐time (5000 ms).
//   - run("w","fs",bufferSize) → random write fixed‐size (25 000 I/Os).
//   - run("w","ft",bufferSize) → random write fixed‐time (5000 ms).
//
// Usage example (in a small main or JShell):
//   HDDRandomAccess bench = new HDDRandomAccess();
//   bench.initialize(1L * 1024 * 1024 * 1024); // 1 GB file
//   bench.run("r", "fs", 4096);  System.out.println(bench.getResult());
//   bench.run("r", "ft", 4096);  System.out.println(bench.getResult());
//   bench.run("w", "fs", 4096);  System.out.println(bench.getResult());
//   bench.run("w", "ft", 4096);  System.out.println(bench.getResult());
//   bench.clean();

package bench.hdd;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

import timing.Timer;
import bench.IBenchmark;

public class HDDRandomAccess implements IBenchmark {

    private static final String PATH = "D:\\test.raf"; // Adjust if you want another location
    private String result;

    @Override
    public void initialize(Object... params) {
        // params[0] must be a Long for fileSizeInBytes
        long fileSizeInBytes = (Long) params[0];
        File tempFile = new File(PATH);

        try {
            // Create or overwrite the file and fill it with random data in 4 KB chunks
            RandomAccessFile rafFile = new RandomAccessFile(tempFile, "rw");
            Random rnd = new Random(0);
            int bufferSize = 4 * 1024; // 4 KB
            long chunks = fileSizeInBytes / bufferSize;
            byte[] buffer = new byte[bufferSize];

            for (long i = 0; i < chunks; i++) {
                rnd.nextBytes(buffer);
                rafFile.write(buffer);
            }
            rafFile.close();
            tempFile.deleteOnExit();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    public void warmUp() {
        // no‐op
    }

    @Override
    public void run() {
        System.out.println("Not implemented for this part");
    }

    @Override
    public void run(Object... options) {
        // options[0] = "r" or "w"
        // options[1] = "fs" or "ft"
        // options[2] = bufferSize (Integer)
        String mode = String.valueOf(options[0]).toLowerCase(); // "r" or "w"
        String style = String.valueOf(options[1]).toLowerCase(); // "fs" or "ft"
        int bufferSize = Integer.parseInt(String.valueOf(options[2]));

        // fixed‐size: exactly this many I/Os
        final int STEPS = 25000;
        // fixed‐time: run this many milliseconds
        final int RUNTIME_MS = 5000;

        try {
            if (mode.equals("r")) {
                // → RANDOM READ
                if (style.equals("fs")) {
                    long timeMs = new Ops().randomReadFixedSize(PATH, bufferSize, STEPS);
                    double mbRead = (double) (STEPS * bufferSize) / (1024.0 * 1024.0);
                    double mbps = mbRead / (timeMs / 1000.0);
                    result = String.format(
                            "%d random‐read(I/Os) of %d bytes in %d ms → %.2f MB, %.2f MB/s",
                            STEPS, bufferSize, timeMs, mbRead, mbps
                    );
                }
                else if (style.equals("ft")) {
                    int ios = new Ops().randomReadFixedTime(PATH, bufferSize, RUNTIME_MS);
                    double mbRead = (double) (ios * bufferSize) / (1024.0 * 1024.0);
                    double mbps = mbRead / (RUNTIME_MS / 1000.0);
                    result = String.format(
                            "%d random‐read(I/Os) in %d ms → %.2f MB, %.2f MB/s (%.2f IOPS)",
                            ios, RUNTIME_MS, mbRead, mbps, ios / (RUNTIME_MS / 1000.0)
                    );
                }
                else {
                    throw new UnsupportedOperationException("Unknown style: " + style);
                }
            }
            else if (mode.equals("w")) {
                // → RANDOM WRITE
                if (style.equals("fs")) {
                    long timeMs = new Ops().randomWriteFixedSize(PATH, bufferSize, STEPS);
                    double mbWritten = (double) (STEPS * bufferSize) / (1024.0 * 1024.0);
                    double mbps = mbWritten / (timeMs / 1000.0);
                    result = String.format(
                            "%d random‐write(I/Os) of %d bytes in %d ms → %.2f MB, %.2f MB/s",
                            STEPS, bufferSize, timeMs, mbWritten, mbps
                    );
                }
                else if (style.equals("ft")) {
                    int ios = new Ops().randomWriteFixedTime(PATH, bufferSize, RUNTIME_MS);
                    double mbWritten = (double) (ios * bufferSize) / (1024.0 * 1024.0);
                    double mbps = mbWritten / (RUNTIME_MS / 1000.0);
                    result = String.format(
                            "%d random‐write(I/Os) in %d ms → %.2f MB, %.2f MB/s (%.2f IOPS)",
                            ios, RUNTIME_MS, mbWritten, mbps, ios / (RUNTIME_MS / 1000.0)
                    );
                }
                else {
                    throw new UnsupportedOperationException("Unknown style: " + style);
                }
            }
            else {
                throw new UnsupportedOperationException("Unknown mode: " + mode);
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = "ERROR: " + e.getMessage();
        }
    }

    @Override
    public void clean() {
        new File(PATH).delete();
    }

    @Override
    public void cancel() {
        // no‐op
    }

    @Override
    public String getResult() {
        return result;
    }

    /**
     * Inner helper class that performs random‐seek + read/write on a RandomAccessFile.
     */
    private class Ops {
        private final Random rnd = new Random();

        // Fixed‐size random read: exactly toRead I/Os
        long randomReadFixedSize(String filePath, int bufferSize, int toRead) throws IOException {
            RandomAccessFile file = new RandomAccessFile(filePath, "rw");
            long fileSize = file.length();
            byte[] buffer = new byte[bufferSize];

            Timer timer = new Timer();
            timer.start();
            for (int i = 0; i < toRead; i++) {
                long offset = randomPos(fileSize, bufferSize);
                file.seek(offset);
                file.readFully(buffer);
            }
            long elapsedNs = timer.stop();
            file.close();
            return elapsedNs / 1_000_000; // ns → ms
        }

        // Fixed‐time random read: as many I/Os as possible in millis ms
        int randomReadFixedTime(String filePath, int bufferSize, int millis) throws IOException {
            RandomAccessFile file = new RandomAccessFile(filePath, "rw");
            long fileSize = file.length();
            byte[] buffer = new byte[bufferSize];

            long startNs = System.nanoTime();
            int count = 0;
            while (true) {
                long nowNs = System.nanoTime();
                long elapsedMs = (nowNs - startNs) / 1_000_000;
                if (elapsedMs >= millis) break;

                long offset = randomPos(fileSize, bufferSize);
                file.seek(offset);
                file.readFully(buffer);
                count++;
            }
            file.close();
            return count;
        }

        // Fixed‐size random write: exactly toWrite I/Os
        long randomWriteFixedSize(String filePath, int bufferSize, int toWrite) throws IOException {
            RandomAccessFile file = new RandomAccessFile(filePath, "rw");
            long fileSize = file.length();
            byte[] buffer = new byte[bufferSize];
            new Random(0).nextBytes(buffer);

            Timer timer = new Timer();
            timer.start();
            for (int i = 0; i < toWrite; i++) {
                long offset = randomPos(fileSize, bufferSize);
                file.seek(offset);
                file.write(buffer);
            }
            long elapsedNs = timer.stop();
            file.close();
            return elapsedNs / 1_000_000; // ns → ms
        }

        // Fixed‐time random write: as many I/Os as possible in millis ms
        int randomWriteFixedTime(String filePath, int bufferSize, int millis) throws IOException {
            RandomAccessFile file = new RandomAccessFile(filePath, "rw");
            long fileSize = file.length();
            byte[] buffer = new byte[bufferSize];
            new Random(0).nextBytes(buffer);

            long startNs = System.nanoTime();
            int count = 0;
            while (true) {
                long nowNs = System.nanoTime();
                long elapsedMs = (nowNs - startNs) / 1_000_000;
                if (elapsedMs >= millis) break;

                long offset = randomPos(fileSize, bufferSize);
                file.seek(offset);
                file.write(buffer);
                count++;
            }
            file.close();
            return count;
        }

        // Utility: random offset in [0 .. fileSize - bufferSize]
        private long randomPos(long fileSize, int bufferSize) {
            long maxStart = fileSize - bufferSize;
            return (long) (rnd.nextDouble() * maxStart);
        }
    }
}