package quicksort;

import common.ISortFile;

import java.io.*;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;

public class QuickSortProcessor implements ISortFile {
    protected static int numberOfThreads;
    protected static ExecutorService executorService;
    protected static long[] longs;
    protected static BlockingQueue<int[]> sortableChunkQueue;
    protected static BlockingQueue<int[]> mergeableChunkQueue;
    protected static CountDownLatch mergeLevelCountDownLatch;
    protected static int mergeStartIndex;
    protected static Instant startTime;
    protected enum PrintDirection {
        FORWARDS,
        BACKWARDS
    }

    private static String outputFile;

    public QuickSortProcessor(int numberOfThreads) {
        QuickSortProcessor.numberOfThreads = numberOfThreads;
    }

    public void sortFile(String inputFile, String outputFile) throws IOException, InterruptedException {
        startTime = Instant.now();

        QuickSortProcessor.outputFile = outputFile;

        executorService = Executors.newFixedThreadPool(numberOfThreads);
        sortableChunkQueue = new LinkedBlockingQueue<>();
        mergeableChunkQueue = new LinkedBlockingQueue<>();

        loadWithThread(inputFile);

        quickSortWithThreads();

        for (int arraysToMerge = numberOfThreads; arraysToMerge != 1; arraysToMerge /= 2) {
            mergeWithThreads(arraysToMerge);
        }

        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        writeFile(longs, PrintDirection.FORWARDS);

        Instant endTime = Instant.now();

        long timeTaken = Duration.between(startTime, endTime).toMillis();
        System.out.printf("Total time taken: %dms\n", timeTaken);

        verifyOutputFile();
    }

    private static void loadWithThread(String fileName) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(fileName);
        int numberOfLongs = (int) fileInputStream.getChannel().size() / 8;
        longs = new long[numberOfLongs];

        executorService.submit(new LoadRunnable(fileInputStream));
    }

    private static void quickSortWithThreads() throws InterruptedException {
        int[] indices;
        int startIndex;
        int endIndex;

        for (int i = 0; i < numberOfThreads; i++) {
            indices = sortableChunkQueue.take();
            startIndex = indices[0];
            endIndex = indices[1];
            executorService.submit(new QuickSortRunnable(startIndex, endIndex));
        }
    }

    private static void mergeWithThreads(int arraysToMerge) throws InterruptedException {
        int[] subArrayA;
        int[] subArrayB;
        int subArrayASize;
        int subArrayBSize;

        int mergesToPerform = arraysToMerge / 2;
        mergeStartIndex = 0;

        long[] longsCopy = new long[longs.length];
        System.arraycopy(longs, 0, longsCopy, 0, longsCopy.length);

        mergeLevelCountDownLatch = new CountDownLatch(mergesToPerform);

        for (int i = 0; i < mergesToPerform; i++) {
            subArrayA = mergeableChunkQueue.take();
            subArrayB = mergeableChunkQueue.take();
            executorService.submit(new MergeRunnable(longsCopy, subArrayA, subArrayB, mergeStartIndex));
            subArrayASize = subArrayA[1] - subArrayA[0] + 1;
            subArrayBSize = subArrayB[1] - subArrayB[0] + 1;
            mergeStartIndex += subArrayASize + subArrayBSize;
        }

        mergeLevelCountDownLatch.await();
        System.arraycopy(longsCopy, 0, longs, 0, longs.length);
    }

    protected static void writeFile(long[] longsToPrint, PrintDirection printDirection) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        ByteBuffer byteBuffer = ByteBuffer.allocate(Byte.SIZE);
        byte[] buffer;

        if (printDirection == PrintDirection.FORWARDS) {
            for (int i = 0; i < longsToPrint.length; i++) {
                buffer = byteBuffer.putLong(0, longsToPrint[i]).array();
                bufferedOutputStream.write(buffer, 0, Byte.SIZE);
            }
        } else if (printDirection == PrintDirection.BACKWARDS) {
            for (int i = longsToPrint.length - 1; i >= 0; i--) {
                buffer = byteBuffer.putLong(0, longsToPrint[i]).array();
                bufferedOutputStream.write(buffer, 0, Byte.SIZE);
            }
        }

        bufferedOutputStream.close();
    }

    protected static void verifyOutputFile() throws IOException {
        FileInputStream fileInputStream = new FileInputStream(outputFile);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

        byte[] buffer = new byte[8];
        ByteBuffer byteBuffer;

        for (int i = 0; i < longs.length; i++) {
            bufferedInputStream.read(buffer, 0, Byte.SIZE);
            byteBuffer = ByteBuffer.wrap(buffer);
            longs[i] = byteBuffer.getLong();
        }

        for (int i = 0; i < longs.length - 1; i++) {
            if (longs[i] > longs[i + 1]) {
                System.err.println("Not Sorted");
            }
        }

        System.out.println("Sorted");
    }
}