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
    // Write a file with fixed file size and given buffer size, return speed MB/s
    public double streamWriteFixedFileSize(String filePrefix, String fileSuffix,
                                           int fileIndex, long fileSize, int bufferSize, boolean clean)
            throws IOException {
        String fileName = filePrefix + fileIndex + fileSuffix;
        return writeFile(fileName, bufferSize, fileSize, clean);
    }

    // Write a file with fixed buffer size and given file size, return speed MB/s
    public double streamWriteFixedBufferSize(String filePrefix, String fileSuffix,
                                             int fileIndex, int bufferSize, long fileSize, boolean clean)
            throws IOException {
        String fileName = filePrefix + fileIndex + fileSuffix;
        return writeFile(fileName, bufferSize, fileSize, clean);
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



    /**
     * Writes a file with random binary content on the disk, using a given file
     * path and buffer size.
     */
    private double writeFile(String fileName, int bufferSize,
                             long fileSize, boolean clean) throws IOException {

        int lastSepIndex = fileName.lastIndexOf(File.separator);
        File folderPath;
        if (lastSepIndex >= 0) {
            folderPath = new File(fileName.substring(0, lastSepIndex));
            if (!folderPath.isDirectory())
                folderPath.mkdirs();
        } else {
            folderPath = new File(".");
        }

        final File file = new File(fileName);
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

        final long time = timer.stop();

        double seconds = time / 1e9; // ns to sec
        double megabytes = fileSize / (1024.0 * 1024.0);
        double rate = megabytes / seconds;

        System.out.println("Done writing " + fileSize + " bytes to file: "
                + fileName + " in " + String.format("%.2f", seconds) + " s ("
                + String.format("%.2f", rate) + " MB/sec) with buffer size "
                + bufferSize / 1024 + " kB");

        if (clean) file.deleteOnExit();

        return rate;
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
