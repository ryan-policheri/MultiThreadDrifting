package common;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class DataSetGenerator {
    public static final String SPLIT_1000 = "SPLIT_1000";
    public static final String SPLIT_1000_SORTED = "SPLIT_1000_SORTED";
    public static final String EVEN_DISTRIBUTION = "EVEN_DISTRIBUTION";
    public static final String POSITIVE_MILLION = "POSITIVE_MILLION";
    public static final String ALL_SAME = "ALL_SAME";
    public static final String X_DIFFERENT_NUMBERS = "X_DIFFERENT_NUMBERS";

    public static void main(String[] args) throws IOException {
        String filePath = System.getProperty("user.dir") + "\\" + "array.bin";
        int N = 200;
        GenerateInputFile(filePath, N);
    }

    public static void GenerateInputFile(String filePath, int inputSize) throws IOException {
        GenerateInputFile(filePath, inputSize, SPLIT_1000, -1);
    }

    public static void GenerateInputFile(String filePath, int inputSize, String generationType) throws IOException {
        GenerateInputFile(filePath, inputSize, generationType, -1);
    }

    public static void GenerateInputFile(String filePath, int inputSize, String generationType, int differentNumberCount) throws IOException {
        long batch[] = new long[inputSize];

        if (generationType.equals(SPLIT_1000)) fillLongArraySplit1000(batch);
        else if (generationType.equals(SPLIT_1000_SORTED)) fillLongArraySplit1000Sorted(batch);
        else if (generationType.equals(EVEN_DISTRIBUTION)) fillLongArrayEvenDistribution(batch);
        else if (generationType.equals(POSITIVE_MILLION)) fillLongArrayPostiveMillion(batch);
        else if (generationType.equals(ALL_SAME)) fillLongArrayAllSame(batch);
        else if (generationType.equals(X_DIFFERENT_NUMBERS)) fillLongArrayXDifferentNumbers(batch, differentNumberCount);
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

    private static void fillLongArrayXDifferentNumbers(long[] batch, int differentNumberCount) {
        if (differentNumberCount > batch.length) throw new IllegalArgumentException("differentNumberCount cannot be greater than batch.length");
        if (differentNumberCount < 1) throw new IllegalArgumentException("differentNumberCount cannot be less than 1");

        int index = 0;
        int duplicatesPerValue = batch.length / differentNumberCount;
        int remainder = batch.length % differentNumberCount;
        HashSet<Long> hashSet = new HashSet<Long>();

        for(int i = 0; i < differentNumberCount; i++) {
            Random r = new Random(23625);
            long num = r.nextLong();
            while(hashSet.contains(num)) { num = r.nextLong(); }
            hashSet.add(num);
            for (int j = 0; j < duplicatesPerValue; j++) {
                batch[index++] = num;
            }
        }

        Iterator<Long> iterator = hashSet.iterator();
        for (int i = 0; i < remainder; i++){
            batch[index++] = iterator.next();
        }

        List<Long> arrayList = new ArrayList<>();
        for (Long number : batch){
            arrayList.add(number);
        }
        Collections.shuffle(arrayList);
        for (int i = 0; i < batch.length; i++) {
            batch[i] = arrayList.get(i);
        }
    }
}
