package common;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class LongWriter {

    public static void writeLongArray(String filePath, long[] longs) throws IOException {
        File file = new File(filePath);
        FileOutputStream fileWriter = new FileOutputStream(file);
        BufferedOutputStream bufferedWriter = new BufferedOutputStream(fileWriter);

        try {
            for (long num : longs) {
                ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
                buffer.putLong(num);
                bufferedWriter.write(buffer.array());
            }
        }
        catch(Exception ex) {

        }
        finally {
            bufferedWriter.close();
            fileWriter.close();
        }
    }

    public static void writeLongArray(String filePath, ArrayList<Long> longs) throws IOException {
        long[] asArray = new long[longs.size()];
        for(int i = 0; i <= asArray.length; i++) {
            asArray[i] = longs.get(i);
        }
        writeLongArray(filePath, asArray);
    }
}
