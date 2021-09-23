package quicksort;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Driver {
    private static long[] longsArray;
    private static int numberOfThreads;
    private static ExecutorService executor;

    public static void main (String[] args) {
        longsArray = DataLoader.getLongs("array.bin").stream().mapToLong(i -> i).toArray();
        numberOfThreads = Integer.parseInt(args[0]);
        executor = Executors.newFixedThreadPool(numberOfThreads);

        quickSortWithThreads();

        for (int arraysToMerge = numberOfThreads; arraysToMerge != 1; arraysToMerge /= 2) {
            mergeWithThreads(arraysToMerge);
        }

        executor.shutdown();
    }

    private static void quickSortWithThreads() {
        int threadChunkSize = longsArray.length / numberOfThreads;
        CountDownLatch countDownLatch = new CountDownLatch(numberOfThreads);

        int startIndex;
        int endIndex;

        for (int i = 0; i < numberOfThreads; i++) {
            startIndex = threadChunkSize * i;

            if (i == numberOfThreads - 1) {
                endIndex = longsArray.length - 1;
            } else {
                endIndex = threadChunkSize * (i + 1) - 1;
            }

            executor.execute(new QuickSortRunnable(longsArray, startIndex, endIndex, countDownLatch));
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException exception) {
            System.err.println(exception);
        }
    }

    private static void mergeWithThreads(int arraysToMerge) {
        int threadChunkSize = longsArray.length / arraysToMerge * 2;
        CountDownLatch countDownLatch = new CountDownLatch(longsArray.length / threadChunkSize);

        int startIndex;
        int endIndex;

        for (int i = 0; i < longsArray.length / threadChunkSize; i++) {
            startIndex = threadChunkSize * i;

            if (i == arraysToMerge - 1) {
                endIndex = longsArray.length - 1;
            } else {
                endIndex = threadChunkSize * (i + 1) - 1;
            }

            int middleIndex = startIndex + (endIndex - startIndex) / 2;
            executor.execute(new MergeRunnable(longsArray, startIndex, middleIndex, endIndex, countDownLatch));
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException exception) {
            System.err.println(exception);
        }
    }
}
