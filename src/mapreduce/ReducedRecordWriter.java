package mapreduce;

import java.io.*;
import java.util.ArrayList;

public class ReducedRecordWriter {
    public static void WriteRecords(String filePath, ReducedRecord[] records) throws IOException {
        File file = new File(filePath);
        FileOutputStream fos = new FileOutputStream(file);
        DataOutputStream dos = new DataOutputStream(fos);

        for(ReducedRecord record : records){
            for (int i = 0; i < record.Count; i++) {
                dos.writeLong(record.Number);
            }
        }

        dos.close();
        fos.close();
    }

    public static void WriteRecords(String filePath, ArrayList<ReducedRecord> reducedRecords) throws IOException  {
        ReducedRecord[] records = new ReducedRecord[reducedRecords.size()];
        reducedRecords.toArray(records);
        WriteRecords(filePath, records);
    }
}
