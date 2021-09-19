package lvnl;

public class QuickSort {
    public static void run(long[] array) {
        int arrayLength = array.length;
        quickSort(array, 0, arrayLength - 1);
    }

    static void quickSort(long[] array, int startIndex, int endIndex) {
        if (startIndex < endIndex) {
            int middleIndex = partition(array, startIndex, endIndex);

            quickSort(array, startIndex, middleIndex - 1);
            quickSort(array, middleIndex + 1, endIndex);
        }
    }

    static int partition(long[] array, int startIndex, int endIndex) {
        long pivot = array[endIndex];

        int i = startIndex - 1;

        for (int j = startIndex; j <= endIndex - 1; j++) {
            if (array[j] < pivot) {
                i++;
                swap(array, i, j);
            }
        }
        swap(array, i + 1, endIndex);
        return (i + 1);
    }

    static void swap(long[] array, int element1, int element2) {
        long temp = array[element1];
        array[element1] = array[element2];
        array[element2] = temp;
    }
}
