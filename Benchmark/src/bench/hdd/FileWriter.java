package bench.hdd;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

import timing.Timer;

public class FileWriter {

    private static final int MIN_BUFFER_SIZE = 1024 * 1; // 1 KB
    private static final int MAX_BUFFER_SIZE = 1024 * 1024 * 32; // 32 MB
    private static final long MIN_FILE_SIZE = 1024 * 1024 * 1; // 1 MB
    private static final long MAX_FILE_SIZE = 1024 * 1024 * 512; // 512 MB
    private Timer timer = new Timer();
    private Random rand = new Random();
    private double benchScore;

    /**
     * Writes files on disk using a variable write buffer and fixed file size.
     *
     * @param filePrefix
     *            - Path and file name
     * @param fileSuffix
     *            - file extension
     * @param minIndex
     *            - start buffer size index
     * @param maxIndex
     *            - end buffer size index
     * @param fileSize
     *            - size of the benchmark file to be written in the disk
     * @throws IOException
     */
    public double streamWriteFixedFileSize(String filePrefix, String fileSuffix,
                                           int minIndex, int maxIndex, long fileSize, boolean clean)
            throws IOException {

        System.out.println("Stream write benchmark with fixed file size");
        int currentBufferSize = MIN_BUFFER_SIZE;
        String fileName;
        int fileIndex = 0;
        benchScore = 0;

        while (currentBufferSize <= MAX_BUFFER_SIZE
                && fileIndex <= maxIndex - minIndex) {
            fileName = filePrefix + fileIndex + fileSuffix;
            writeFile(fileName, currentBufferSize, fileSize, clean);
            currentBufferSize *= 2;
            fileIndex++;
        }

        benchScore /= (maxIndex - minIndex + 1);

        String partition;
        if (filePrefix.contains(":\\")) {
            partition = filePrefix.substring(0, filePrefix.indexOf(":\\"));
        } else if (filePrefix.startsWith("/")) {
            partition = "/";
        } else {
            partition = "unknown";
        }

        System.out.println("File write score on partition " + partition + ": "
                + String.format("%.2f", benchScore) + " MB/sec");

        return benchScore;  // <-- ADD THIS LINE
    }


    /**
     * Writes files on disk using a variable file size and fixed buffer size.
     *
     * @param filePrefix
     *            - Path and file name
     * @param fileSuffix
     *            - file extension
     * @param minIndex
     *            - start file size index
     * @param maxIndex
     *            - end file size index
     * @param bufferSize
     *            - size of the write buffer to be used
     */

    public double streamWriteFixedBufferSize(String filePrefix, String fileSuffix,
                                             int minIndex, int maxIndex, int bufferSize, boolean clean)
            throws IOException {

        System.out.println("Stream write benchmark with fixed buffer size");
        long currentFileSize = MIN_FILE_SIZE;
        int fileIndex = 0;
        benchScore = 0;

        while (currentFileSize <= MAX_FILE_SIZE
                && fileIndex <= maxIndex - minIndex) {
            String fileName = filePrefix + fileIndex + fileSuffix;
            writeFile(fileName, bufferSize, currentFileSize, clean);
            currentFileSize *= 2;
            fileIndex++;
        }

        benchScore /= (maxIndex - minIndex + 1);

        String partition;
        if (filePrefix.contains(":\\")) {
            partition = filePrefix.substring(0, filePrefix.indexOf(":\\"));
        } else if (filePrefix.startsWith("/")) {
            partition = "/";
        } else {
            partition = "unknown";
        }

        System.out.println("File write score on partition " + partition + ": "
                + String.format("%.2f", benchScore) + " MB/sec");

        return benchScore;  // <-- ADD THIS LINE
    }


    /**
     * Writes a file with random binary content on the disk, using a given file
     * path and buffer size.
     */
    private void writeFile(String fileName, int bufferSize,
                           long fileSize, boolean clean) throws IOException {

        int lastSepIndex = fileName.lastIndexOf(File.separator);
        File folderPath;
        if (lastSepIndex >= 0) {
            folderPath = new File(fileName.substring(0, lastSepIndex));
            // create folder path to benchmark output if it doesn't exist
            if (!folderPath.isDirectory())
                folderPath.mkdirs();
        } else {
            // no folder path in fileName, use current directory
            folderPath = new File(".");
        }

        final File file = new File(fileName);
        // create stream writer with given buffer size
        final BufferedOutputStream outputStream =
                new BufferedOutputStream(new FileOutputStream(file), bufferSize);

        byte[] buffer = new byte[bufferSize];
        long toWrite = fileSize / bufferSize;
        long remainder = fileSize % bufferSize;

        timer.start();
        for (long i = 0; i < toWrite; i++) {
            rand.nextBytes(buffer);
            outputStream.write(buffer);
        }
        if (remainder > 0) {
            byte[] lastBuffer = new byte[(int) remainder];
            rand.nextBytes(lastBuffer);
            outputStream.write(lastBuffer);
        }

        outputStream.flush();
        outputStream.close();

        printStats(fileName, fileSize, bufferSize);

        if (clean)
            file.deleteOnExit();
    }


    private void printStats(String fileName, long totalBytes, int bufferSize) {
        final long time = timer.stop();

        NumberFormat nf = new DecimalFormat("#.00");
        double seconds = time / 1e9; // nanoseconds to seconds
        double megabytes = totalBytes / (1024.0 * 1024.0);
        double rate = megabytes / seconds;

        System.out.println("Done writing " + totalBytes + " bytes to file: "
                + fileName + " in " + nf.format(seconds) + " s ("
                + nf.format(rate) + " MB/sec) with a buffer size of "
                + bufferSize / 1024 + " kB");

        // actual score is write speed (MB/s)
        benchScore += rate;
    }
}
