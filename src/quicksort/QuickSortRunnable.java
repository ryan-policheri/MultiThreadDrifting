package quicksort;

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
        QuickSortProcessor.mergeableChunkQueue.add(indices);
    }

    private void quickSort(int startIndex, int endIndex) {
        if (startIndex < endIndex) {
            int middleIndex = partition(startIndex, endIndex);

            quickSort(startIndex, middleIndex - 1);
            quickSort(middleIndex + 1, endIndex);
        }
    }

    private int partition(int startIndex, int endIndex) {
        long pivot = QuickSortProcessor.longs[endIndex];

        int i = startIndex - 1;

        for (int j = startIndex; j < endIndex; j++) {
            if (QuickSortProcessor.longs[j] < pivot) {
                i++;
                swap(i, j);
            }
        }
        swap(i + 1, endIndex);
        return (i + 1);
    }

    private void swap(int element1, int element2) {
        long temporaryValue = QuickSortProcessor.longs[element1];
        QuickSortProcessor.longs[element1] = QuickSortProcessor.longs[element2];
        QuickSortProcessor.longs[element2] = temporaryValue;
    }
}
