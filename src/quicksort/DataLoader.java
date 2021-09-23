package quicksort;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {
    public static List<Long> getLongs(String fileName) {
        List<Long> longs = new ArrayList<Long>();

        try {
            FileInputStream fileInputStream = new FileInputStream(fileName);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);

            try {
                while (true) {
                    long currentLong = dataInputStream.readLong();
                    longs.add(currentLong);
                }
            } catch (EOFException exception) {
                fileInputStream.close();
                return longs;
            }
        } catch (IOException exception) {
            System.err.println(exception);
        }

        return longs;
    }
}