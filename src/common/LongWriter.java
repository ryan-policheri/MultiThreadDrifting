package common;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class LongWriter {

    public static void writeLongArray(String filePath, long[] longs) throws IOException {
        File file = new File(filePath);
        FileOutputStream fos = new FileOutputStream(file);
        DataOutputStream dos = new DataOutputStream(fos);

        for (int i = 0; i < longs.length; i++) {
            dos.writeLong(longs[i]);
        }
        dos.close();
        fos.close();
    }

    public static void writeLongArray(String filePath, Long[] longs) throws IOException {
        File file = new File(filePath);
        FileOutputStream fos = new FileOutputStream(file);
        DataOutputStream dos = new DataOutputStream(fos);

        for (int i = 0; i < longs.length; i++) {
            dos.writeLong(longs[i]);
        }
        dos.close();
        fos.close();
    }

    public static void writeLongArray(String filePath, ArrayList<Long> longs) throws IOException {
        File file = new File(filePath);
        FileOutputStream fos = new FileOutputStream(file);
        DataOutputStream dos = new DataOutputStream(fos);

        for (int i = 0; i < longs.size(); i++) {
            dos.writeLong(longs.get(i));
        }
        dos.close();
        fos.close();
    }
}
