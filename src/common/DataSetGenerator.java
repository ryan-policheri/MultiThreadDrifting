package common;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Random;

public class DataSetGenerator {
    public static final String SPLIT_1000 = "SPLIT_1000";
    public static final String SPLIT_1000_SORTED = "SPLIT_1000_SORTED";
    public static final String EVEN_DISTRIBUTION = "EVEN_DISTRIBUTION";
    public static final String POSITIVE_MILLION = "POSITIVE_MILLION";
    public static final String ALL_SAME = "ALL_SAME";

    public static void main(String[] args) throws IOException {
        String filePath = System.getProperty("user.dir") + "\\" + "array.bin";
        int N = 200;
        GenerateInputFile(filePath, N);
    }

    public static void GenerateInputFile(String filePath, int inputSize) throws IOException {
        GenerateInputFile(filePath, inputSize, SPLIT_1000);
    }

    public static void GenerateInputFile(String filePath, int inputSize, String generationType) throws IOException {
        long batch[] = new long[inputSize];
        if (generationType.equals(SPLIT_1000)) fillLongArraySplit1000(batch);
        else if (generationType.equals(SPLIT_1000_SORTED)) fillLongArraySplit1000Sorted(batch);
        else if (generationType.equals(EVEN_DISTRIBUTION)) fillLongArrayEvenDistribution(batch);
        else if (generationType.equals(POSITIVE_MILLION)) fillLongArrayPostiveMillion(batch);
        else if (generationType.equals(ALL_SAME)) fillLongArrayAllSame(batch);
        else throw new IllegalArgumentException("Generation Type Unknown");

        File file = new File(filePath);
        if(file.exists()) { Files.delete(file.toPath()); }
        FileOutputStream fos = new FileOutputStream(file);
        DataOutputStream dos = new DataOutputStream(fos);

        for (int i = 0; i < batch.length; i++) {
            dos.writeLong(batch[i]);
        }
        dos.close();
        fos.close();
    }

    private static void fillLongArraySplit1000(long[] batch) { //ranged from -999 to 999
        Random r = new Random(23625);
        for (int i = 0; i < batch.length; i++) {
            batch[i] = r.nextLong() % 1000;
        }
    }

    private static void fillLongArraySplit1000Sorted(long[] batch) { //ranged from -999 to 999, sorted
        fillLongArraySplit1000(batch);
        Arrays.sort(batch);
    }

    private static void fillLongArrayEvenDistribution(long[] batch) {
        Random r = new Random(23625);
        for (int i = 0; i < batch.length; i++) {
            batch[i] = r.nextLong();
        }
    }

    private static void fillLongArrayPostiveMillion(long[] batch) {
        Random r = new Random(23625);
        for (int i = 0; i < batch.length; i++) {
            batch[i] = 1 + r.nextInt(1000000);
        }
    }

    private static void fillLongArrayAllSame(long[] batch) {
        Random r = new Random(23625);
        long num = r.nextLong();
        for (int i = 0; i < batch.length; i++) {
            batch[i] = num;
        }
    }
}
