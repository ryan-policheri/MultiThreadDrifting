package quicksort;

public class MergeRunnable implements Runnable {
    private final long[] longArray;
    private final int[] subArrayA;
    private final int[] subArrayB;
    private final int startIndex;
    private final int subArrayASize;
    private final int subArrayBSize;

    public MergeRunnable(long[] longArray, int[] subArrayA, int[] subArrayB, int startIndex) {
        this.longArray = longArray;
        this.subArrayA = subArrayA;
        this.subArrayB = subArrayB;
        this.startIndex = startIndex;
        subArrayASize = subArrayA[1] - subArrayA[0] + 1;
        subArrayBSize = subArrayB[1] - subArrayB[0] + 1;
    }

    @Override
    public void run() {
        merge();
        QuickSortProcessor.mergeLevelCountDownLatch.countDown();

        try {
            QuickSortProcessor.mergeLevelCountDownLatch.await();
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }

        int endIndex = startIndex + subArrayASize + subArrayBSize - 1;
        int[] indices = {startIndex, endIndex};
        QuickSortProcessor.mergeableChunkQueue.add(indices);
    }

    private void merge() {
        int i = subArrayA[0], j = subArrayB[0];

        int currentArrayIndex = startIndex;

        while (i < subArrayA[0] + subArrayASize && j < subArrayB[0] + subArrayBSize) {
            if (QuickSortProcessor.longs[i] <= QuickSortProcessor.longs[j]) {
                longArray[currentArrayIndex] = QuickSortProcessor.longs[i];
                i++;
            }
            else {
                longArray[currentArrayIndex] = QuickSortProcessor.longs[j];
                j++;
            }
            currentArrayIndex++;
        }

        while (i < subArrayA[0] + subArrayASize) {
            longArray[currentArrayIndex] = QuickSortProcessor.longs[i];
            i++;
            currentArrayIndex++;
        }

        while (j < subArrayB[0] + subArrayBSize) {
            longArray[currentArrayIndex] = QuickSortProcessor.longs[j];
            j++;
            currentArrayIndex++;
        }
    }
}