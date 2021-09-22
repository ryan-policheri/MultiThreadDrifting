package quicksort;

import java.util.concurrent.CountDownLatch;

public class QuickSortRunnable implements Runnable{
    private final long[] array;
    private final int startIndex;
    private final int endIndex;
    private final CountDownLatch countDownLatch;

    public QuickSortRunnable (long[] array, int startIndex, int endIndex, CountDownLatch countDownLatch) {
        this.array = array;
        this.startIndex = startIndex;
        this. endIndex = endIndex;
        this.countDownLatch = countDownLatch;
    }

    public void run() {
        quickSort(this.startIndex, this.endIndex);
        countDownLatch.countDown();
    }

    private void quickSort(int startIndex, int endIndex) {
        if (startIndex < endIndex) {
            int middleIndex = partition(startIndex, endIndex);

            quickSort(startIndex, middleIndex - 1);
            quickSort(middleIndex + 1, endIndex);
        }
    }

    private int partition(int startIndex, int endIndex) {
        long pivot = this.array[endIndex];

        int i = startIndex - 1;

        for (int j = startIndex; j <= endIndex - 1; j++) {
            if (this.array[j] < pivot) {
                i++;
                swap(i, j);
            }
        }
        swap(i + 1, endIndex);
        return (i + 1);
    }

    private void swap(int element1, int element2) {
        long temporaryValue = this.array[element1];
        this.array[element1] = this.array[element2];
        this.array[element2] = temporaryValue;
    }
}
