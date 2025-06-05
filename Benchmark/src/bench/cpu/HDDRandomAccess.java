package bench.cpu;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

import timing.Timer;
import bench.IBenchmark;

public class HDDRandomAccess implements IBenchmark {

    private final static String PATH = "D:\\test.raf";
    private String result;

    @Override
    public void initialize(Object... params) {
        File tempFile = new File(PATH);
        RandomAccessFile rafFile;
        long fileSizeInBytes = (Long) params[0];

        // Create a temporary file with random content to be used for
        // reading and writing
        try {
            rafFile = new RandomAccessFile(tempFile, "rw");
            Random rand = new Random();
            int bufferSize = 4 * 1024; // 4KB
            long toWrite = fileSizeInBytes / bufferSize;
            byte[] buffer = new byte[bufferSize];
            long counter = 0;

            while (counter++ < toWrite) {
                rand.nextBytes(buffer);
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
        // have a Mountain Dew or Red Bull
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Use run(Object[]) instead");
    }

    @Override
    public void run(Object ...options) {
        // ex. {"r", "fs", 4*1024}
        Object[] param = (Object[]) options;
        // used by the fixed size option
        final int steps = 25000; // number of I/O ops
        // used by the fixed time option
        final int runtime = 5000; // ms

        try {
            // read benchmark
            if (String.valueOf(param[0]).toLowerCase().equals("r")) {
                // buffer size given as parameter
                int bufferSize = Integer.parseInt(String.valueOf(param[2]));

                // read a fixed size and measure time
                if (String.valueOf(param[1]).toLowerCase().equals("fs")) {

                    long timeMs = new RandomAccess().randomReadFixedSize(PATH,
                            bufferSize, steps);
                    result = steps + " random reads in " + timeMs + " ms ["
                            + (steps * bufferSize / 1024 / 1024) + " MB, "
                            + 1.0 * (steps * bufferSize / 1024 / 1024) / timeMs * 1000 + "MB/s]";
                }
                // read for a fixed time amount and measure time
                else if (String.valueOf(param[1]).toLowerCase().equals("ft")) {

                    int ios = new RandomAccess().randomReadFixedTime(PATH,
                            bufferSize, runtime);
                    result = ios + " I/Os per second ["
                            + (ios * bufferSize / 1024 / 1024) + " MB, "
                            + 1.0 * (ios * bufferSize / 1024 / 1024) / runtime * 1000 + "MB/s]";
                } else
                    throw new UnsupportedOperationException("Read option \""
                            + String.valueOf(param[1])
                            + "\" is not implemented");

            }
            // write benchmark
            else if (String.valueOf(param[0]).toLowerCase().equals("w")) {
                int bufferSize = Integer.parseInt(String.valueOf(param[2]));
                // your code here: implement all cases for param[[0]: fs, ft, other
                if (String.valueOf(param[1]).toLowerCase().equals("fs")) {
                    long timeMs = new RandomAccess().randomWriteFixedSize(PATH, bufferSize, steps);
                    result = steps + " random writes in " + timeMs + " ms ["
                            + (steps * bufferSize / 1024 / 1024) + " MB, "
                            + 1.0 * (steps * bufferSize / 1024 / 1024) / timeMs * 1000 + " MB/s]";
                } else if (String.valueOf(param[1]).toLowerCase().equals("ft")) {
                    int ios = new RandomAccess().randomWriteFixedTime(PATH, bufferSize, runtime);
                    result = ios + " I/Os in " + runtime + " ms ["
                            + (ios * bufferSize / 1024 / 1024) + " MB, "
                            + 1.0 * (ios * bufferSize / 1024 / 1024) / runtime * 1000 + " MB/s]";
                }
            } else
                throw new UnsupportedOperationException("Benchmark option \""
                        + String.valueOf(param[0]) + "\" is not implemented");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clean() {
        // remove created file perhaps?
        new File(PATH).delete();
    }

    @Override
    public void cancel() {
        //
    }

    @Override
    public String getResult() {
        return String.valueOf(result);
    }

    class RandomAccess {
        private Random random;

        RandomAccess() {
            random = new Random();
        }

        /**
         * Reads data from random positions into a fixed size buffer from a
         * given file using RandomAccessFile
         *
         * @param filePath
         *            Full path to file on disk
         * @param bufferSize
         *            Size of byte buffer to read at each step
         * @param toRead
         *            Number of steps to repeat random read
         * @return Amount of time needed to finish given reads in milliseconds
         * @throws IOException
         */
        public long randomReadFixedSize(String filePath, int bufferSize, int toRead) throws IOException {
            // file to read from
            RandomAccessFile file = new RandomAccessFile(filePath, "rw");
            // size of file
            long fileSize = file.getChannel().size();
            // counter for number of reads
            int counter = 0;
            // buffer for reading
            byte[] bytes = new byte[bufferSize];
            Random random = new Random();
            // timer
            Timer timer = new Timer();

            timer.start();
            while (counter++ < toRead) {
                // go to random spot in file
                int position = random.nextInt((int)(fileSize - bufferSize));
                file.seek(position);

                // read the bytes into buffer
                file.read(bytes);
            }

            file.close();
            return timer.stop() / 1000000; // ns to ms!
        }

        /**
         * Reads data from random positions into a fixed size buffer from a
         * given file using RandomAccessFile for one second, or any other given
         * time
         *
         * @param filePath
         *            Full path to file on disk
         * @param bufferSize
         *            Size of byte buffer to read at each step
         * @param millis
         *            Total time to read from file
         * @return Number of reads in the given amount of time
         * @throws IOException
         */
        public int randomReadFixedTime(String filePath, int bufferSize,
                                       int millis) throws IOException {
            // file to read from
            RandomAccessFile file = new RandomAccessFile(filePath, "rw");
            // size of file
            long fileSize = file.getChannel().size();
            // counter for number of reads
            int counter = 0;
            // buffer for reading
            byte[] bytes = new byte[bufferSize];

            long now = System.nanoTime();

            Random random = new Random();

            long startTime = System.nanoTime(); // start in nanoseconds
            long endTime = startTime + millis * 1_000_000L; // convert millis to nanoseconds

            // read for a fixed amount of time
            while (System.nanoTime() < endTime) {
                // go to random spot in file
                int position = random.nextInt((int)(fileSize - bufferSize));
                file.seek(position);

                // read the bytes into buffer
                file.read(bytes);
                counter++;
            }

            file.close();
            return counter;
        }

        /**
         * Read data from a file at a specific position
         *
         * @param filePath
         *            Path to file
         * @param position
         *            Position in file
         * @param size
         *            Number of bytes to reads from the given position
         * @return Data that was read
         * @throws IOException
         */
        public byte[] readFromFile(String filePath, int position, int size)
                throws IOException {

            RandomAccessFile file = new RandomAccessFile(filePath, "rw");
            file.seek(position);
            byte[] bytes = new byte[size];
            file.read(bytes);
            file.close();
            return bytes;
        }

        /**
         * Write data to a file at a specific position
         *
         * @param filePath
         *            Path to file
         * @param data
         *            Data to be written
         * @param position
         *            Start position in file
         * @throws IOException
         */
        public void writeToFile(String filePath, String data, int position)
                throws IOException {

            RandomAccessFile file = new RandomAccessFile(filePath, "rw");
            file.seek(position);
            file.write(data.getBytes());
            file.close();
        }
        public long randomWriteFixedSize(String filePath, int bufferSize, int toWrite) throws IOException {
            RandomAccessFile file = new RandomAccessFile(filePath, "rw");
            long fileSize = file.length();
            byte[] bytes = new byte[bufferSize];
            new Random().nextBytes(bytes);
            Timer timer = new Timer();
            timer.start();

            for (int i = 0; i < toWrite; i++) {
                int position = random.nextInt((int) (fileSize - bufferSize));
                file.seek(position);
                file.write(bytes);
            }

            file.close();
            return timer.stop() / 1_000_000; // ns to ms
        }
        public int randomWriteFixedTime(String filePath, int bufferSize, int millis) throws IOException {
            RandomAccessFile file = new RandomAccessFile(filePath, "rw");
            long fileSize = file.length();
            byte[] bytes = new byte[bufferSize];
            new Random().nextBytes(bytes);
            int count = 0;
            long end = System.currentTimeMillis() + millis;

            while (System.currentTimeMillis() < end) {
                int position = random.nextInt((int) (fileSize - bufferSize));
                file.seek(position);
                file.write(bytes);
                count++;
            }

            file.close();
            return count;
        }
    }

}
