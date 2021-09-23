package quicksort;

import java.util.Arrays;

public class MergeRunnable implements Runnable {
    private final int startIndex;
    private final int middleIndex;
    private final int endIndex;

    public MergeRunnable(int startIndex, int middleIndex, int endIndex) {
        this.startIndex = startIndex;
        this.middleIndex = middleIndex;
        this.endIndex = endIndex;
    }

    public void run() {
        merge();
        Driver.countDownLatch.countDown();
    }

    private void merge() {
        int leftArraySize = middleIndex - startIndex + 1;
        int rightArraySize = endIndex - middleIndex;

        long[] leftArray = new long[leftArraySize];
        long[] rightArray = new long[rightArraySize];

        System.arraycopy(Driver.longs, startIndex, leftArray, 0, leftArraySize);
        System.arraycopy(Driver.longs, middleIndex + 1, rightArray, 0, rightArraySize);

        int i = 0, j = 0;

        int currentArrayIndex = startIndex;
        while (i < leftArraySize && j < rightArraySize) {
            if (leftArray[i] <= rightArray[j]) {
                Driver.longs[currentArrayIndex] = leftArray[i];
                i++;
            }
            else {
                Driver.longs[currentArrayIndex] = rightArray[j];
                j++;
            }
            currentArrayIndex++;
        }

        while (i < leftArraySize) {
            Driver.longs[currentArrayIndex] = leftArray[i];
            i++;
            currentArrayIndex++;
        }

        while (j < rightArraySize) {
            Driver.longs[currentArrayIndex] = rightArray[j];
            j++;
            currentArrayIndex++;
        }
    }
}