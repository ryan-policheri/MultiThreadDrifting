package common;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class BinaryFileToTextFile {
    public static void ConvertBinaryLongsToTextLongs(String binaryFile) throws IOException {
        ArrayList<Long> longs = DataLoader.readInput(binaryFile);

        String textFile = binaryFile.replace(".bin", ".txt");
        PrintWriter writer = new PrintWriter(textFile);
        for(Long number : longs) {
            writer.println(number);
        }
        writer.close();
    }
}
