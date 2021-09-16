package common;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;

public class DataSetGenerator {
    public static void main(String[] args) throws IOException {
        String filePath = System.getProperty("user.dir") + "\\" + "array.bin";
        int N = 200;
        GenerateInputFile(filePath, N);
    }

    public static void GenerateInputFile(String filePath, int inputSize) throws IOException {
        long batch[] = new long[inputSize];
        Random r = new Random(1234);
        for (int i = 0; i < batch.length; i++) {
            batch[i] = r.nextLong() % 1000;
        }

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
}
