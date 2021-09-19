package lvnl;

public class MergeSort {
    public static void run(long[] array) {
        int startingIndex = 0;
        int endingIndex = array.length - 1;

        split(array, startingIndex, endingIndex);
    }

    private static void split(long[] array, int startingIndex, int endingIndex) {
        if (startingIndex < endingIndex) {
            int middleIndex = startingIndex + (endingIndex - startingIndex) / 2;

            split(array, startingIndex, middleIndex);
            split(array, middleIndex + 1, endingIndex);

            merge(array, startingIndex, middleIndex, endingIndex);
        }
    }

    private static void merge(long[] array, int startingIndex, int middleIndex, int endingIndex) {
        int leftArraySize = middleIndex - startingIndex + 1;
        int rightArraySize = endingIndex - middleIndex;

        long[] leftArray = new long[leftArraySize];
        long[] rightArray = new long[rightArraySize];

        System.arraycopy(array, startingIndex, leftArray, 0, leftArraySize);
        System.arraycopy(array, middleIndex + 1, rightArray, 0, rightArraySize);

        int i = 0, j = 0;

        int currentArrayIndex = startingIndex;
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