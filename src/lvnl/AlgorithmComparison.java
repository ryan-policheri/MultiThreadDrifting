package lvnl;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class AlgorithmComparison {
    public static void main(String[] args) {
        List<Long> longsAsArrayList = DataLoader.getLongs("array.bin");
        long[] longsAsArray;

        longsAsArray = longsAsArrayList.stream().mapToLong(i -> i).toArray();

        quickSort(longsAsArray);
    }

    private static void quickSort(long[] array) {
        Instant startTime = Instant.now();
        QuickSort.run(array);
        Instant endTime = Instant.now();

        long timeTaken = Duration.between(startTime, endTime).toMillis();
        System.out.printf("QuickSort time: %d\n", timeTaken);
    }

    private static void timSort(long[] array) {
        Instant startTime = Instant.now();
        TimSort.run(array);
        Instant endTime = Instant.now();

        long timeTaken = Duration.between(startTime, endTime).toMillis();
        System.out.printf("TimSort time: %d\n", timeTaken);
    }
}