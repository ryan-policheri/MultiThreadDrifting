package quicksort;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.*;

public class Driver {
    protected static int numberOfThreads;
    protected static BlockingQueue<int[]> sortableChunkQueue;
    protected static BlockingQueue<int[]> mergeableChunkQueue;
    protected static CountDownLatch countDownLatch;
    protected static long[] longs;

    private static ExecutorService executorService;

    public static void main(String[] args) throws IOException, InterruptedException {
        numberOfThreads = Integer.parseInt(args[1]);
        executorService = Executors.newFixedThreadPool(numberOfThreads);
        sortableChunkQueue = new LinkedBlockingQueue<>();
        mergeableChunkQueue = new LinkedBlockingQueue<>();

        String fileName = args[0];
        loadWithThread(fileName);

        quickSortWithThreads();

        for (int arraysToMerge = numberOfThreads; arraysToMerge != 1; arraysToMerge /= 2) {
            mergeWithThreads(arraysToMerge);
            countDownLatch.await();
        }

        executorService.shutdown();
    }

    private static void loadWithThread(String fileName) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(fileName);
        DataInputStream dataInputStream = new DataInputStream(fileInputStream);
        int numberOfLongs = (int) fileInputStream.getChannel().size() / 8;
        longs = new long[numberOfLongs];

        executorService.submit(new LoadRunnable(dataInputStream));
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
        countDownLatch = new CountDownLatch(arraysToMerge / 2);

        int chunkSize = longs.length / arraysToMerge * 2;

        int startIndex;
        int endIndex;

        for (int i = 0; i < longs.length / chunkSize; i++) {
            startIndex = chunkSize * i;

            if (i == arraysToMerge - 1) {
                endIndex = longs.length - 1;
            } else {
                endIndex = chunkSize * (i + 1) - 1;
            }

            int middleIndex = startIndex + (endIndex - startIndex) / 2;

            executorService.submit(new MergeRunnable(startIndex, middleIndex, endIndex));
        }

        countDownLatch.await();
    }
}
