package quicksort;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class MergeRunnable implements Runnable {
    private final long[] array;
    private final int startIndex;
    private final int middleIndex;
    private final int endIndex;
    private final CountDownLatch countDownLatch;

    public MergeRunnable(long[] array, int startIndex, int middleIndex, int endIndex, CountDownLatch countDownLatch) {
        this.array = array;
        this.startIndex = startIndex;
        this.middleIndex = middleIndex;
        this.endIndex = endIndex;
        this.countDownLatch = countDownLatch;
    }

    public void run() {
        merge();
        countDownLatch.countDown();
    }

    private void merge() {
        int leftArraySize = middleIndex - startIndex + 1;
        int rightArraySize = endIndex - middleIndex;

        long[] leftArray = new long[leftArraySize];
        long[] rightArray = new long[rightArraySize];

        System.arraycopy(array, startIndex, leftArray, 0, leftArraySize);
        System.arraycopy(array, middleIndex + 1, rightArray, 0, rightArraySize);

        int i = 0, j = 0;

        int currentArrayIndex = startIndex;
        while (i < leftArraySize && j < rightArraySize) {
            if (leftArray[i] <= rightArray[j]) {
                array[currentArrayIndex] = leftArray[i];
                i++;
            }
            else {
                array[currentArrayIndex] = rightArray[j];
                j++;
            }
            currentArrayIndex++;
        }

        while (i < leftArraySize) {
            array[currentArrayIndex] = leftArray[i];
            i++;
            currentArrayIndex++;
        }

        while (j < rightArraySize) {
            array[currentArrayIndex] = rightArray[j];
            j++;
            currentArrayIndex++;
        }
    }
}
