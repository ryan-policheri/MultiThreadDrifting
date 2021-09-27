package quicksort;

import common.ISortFile;

import java.io.*;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;

public class QuickSortProcessor implements ISortFile {
    protected static int numberOfThreads;
    protected static BlockingQueue<int[]> sortableChunkQueue;
    protected static BlockingQueue<int[]> mergeableChunkQueue;
    protected static CountDownLatch mergeLevelCountDownLatch;
    protected static long[] longs;
    protected static int mergeStartIndex;

    private static ExecutorService executorService;

    public QuickSortProcessor(int numberOfThreads) {
        QuickSortProcessor.numberOfThreads = numberOfThreads;
    }

    public void sortFile(String inputFile, String outputFile) throws IOException, InterruptedException {
        Instant startTime = Instant.now();

        executorService = Executors.newFixedThreadPool(numberOfThreads);
        sortableChunkQueue = new LinkedBlockingQueue<>();
        mergeableChunkQueue = new LinkedBlockingQueue<>();

        loadWithThread(inputFile);

        quickSortWithThreads();

        for (int arraysToMerge = numberOfThreads; arraysToMerge != 1; arraysToMerge /= 2) {
            mergeWithThreads(arraysToMerge);
        }

        writeWithThread();

        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        Instant endTime = Instant.now();

        long timeTaken = Duration.between(startTime, endTime).toMillis();
        System.out.printf("Total time taken: %dms\n", timeTaken);
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

    private static void writeWithThread() throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream("src/quicksort/array_sorted.bin");
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        ByteBuffer byteBuffer = ByteBuffer.allocate(Byte.SIZE);
        byte[] buffer;

        for (long l : longs) {
            buffer = byteBuffer.putLong(0, l).array();
            bufferedOutputStream.write(buffer, 0, Byte.SIZE);
        }

        bufferedOutputStream.close();
    }
}