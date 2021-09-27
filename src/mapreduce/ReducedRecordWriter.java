package mapreduce;

import java.io.*;
import java.nio.ByteBuffer;

public class ReducedRecordWriter {
    public static void WriteRecords(String filePath, ReducedRecord[] records) throws IOException {
        File file = new File(filePath);
        FileOutputStream fileWriter = new FileOutputStream(file);
        BufferedOutputStream bufferedWriter = new BufferedOutputStream(fileWriter);

        try {
            for (ReducedRecord record : records) {
                ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
                buffer.putLong(record.Number);
                for (int i = 0; i < record.Count; i++) {
                    bufferedWriter.write(buffer.array());
                }
            }
        }
        catch(Exception ex) {

        }
        finally {
            bufferedWriter.close();
            fileWriter.close();
        }
    }
}
