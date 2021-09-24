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

    public void run() {
        merge();
        Driver.mergeLevelCountDownLatch.countDown();

        try {
            Driver.mergeLevelCountDownLatch.await();
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }

        int endIndex = startIndex + subArrayASize + subArrayBSize - 1;
        int[] indices = {startIndex, endIndex};
        Driver.mergeableChunkQueue.add(indices);
    }

    private void merge() {
        int i = subArrayA[0], j = subArrayB[0];

        int currentArrayIndex = startIndex;

        while (i < subArrayA[0] + subArrayASize && j < subArrayB[0] + subArrayBSize) {
            if (Driver.longs[i] <= Driver.longs[j]) {
                longArray[currentArrayIndex] = Driver.longs[i];
                i++;
            }
            else {
                longArray[currentArrayIndex] = Driver.longs[j];
                j++;
            }
            currentArrayIndex++;
        }

        while (i < subArrayA[0] + subArrayASize) {
            longArray[currentArrayIndex] = Driver.longs[i];
            i++;
            currentArrayIndex++;
        }

        while (j < subArrayB[0] + subArrayBSize) {
            longArray[currentArrayIndex] = Driver.longs[j];
            j++;
            currentArrayIndex++;
        }
    }
}