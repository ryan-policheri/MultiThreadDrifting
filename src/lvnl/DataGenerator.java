package lvnl;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class DataGenerator {
    public static void main(String[] args) {
        int numberOfLongs = Integer.parseInt(args[0]);

        long longs[] = new long[numberOfLongs];
        Random r = new Random(1234);
        for (int i = 0; i < longs.length; i++) {
            longs[i] = r.nextLong() % 1000;
        }

        try {
            FileOutputStream fileOutputStream = new FileOutputStream("array.bin");
            DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);

            for (int i = 0; i < longs.length; i++) {
                dataOutputStream.writeLong(longs[i]);
            }

            dataOutputStream.close();
            fileOutputStream.close();
        } catch (IOException exception) {
            System.err.println(exception);
        }
    }
}