package quicksort;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

public class QuickSortRunnable implements Runnable {
    private final int startIndex;
    private final int endIndex;

    public QuickSortRunnable(int startIndex, int endIndex) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    @Override
    public void run() {
        quickSort(this.startIndex, this.endIndex);
        int[] indices = {startIndex, endIndex};
        Driver.mergeableChunkQueue.add(indices);
    }

    private void quickSort(int startIndex, int endIndex) {
        if (startIndex < endIndex) {
            int middleIndex = partition(startIndex, endIndex);

            quickSort(startIndex, middleIndex - 1);
            quickSort(middleIndex + 1, endIndex);
        }
    }

    private int partition(int startIndex, int endIndex) {
        long pivot = Driver.longs[endIndex];

        int i = startIndex - 1;

        for (int j = startIndex; j <= endIndex - 1; j++) {
            if (Driver.longs[j] < pivot) {
                i++;
                swap(i, j);
            }
        }
        swap(i + 1, endIndex);
        return (i + 1);
    }

    private void swap(int element1, int element2) {
        long temporaryValue = Driver.longs[element1];
        Driver.longs[element1] = Driver.longs[element2];
        Driver.longs[element2] = temporaryValue;
    }
}
