package common;

import java.io.*;
import java.util.ArrayList;

public class DataLoader {
    public static void main(String[] args) throws IOException {
        String filePath = System.getProperty("user.dir") + "\\" + "array.bin";
        readInput(filePath, null);
    }

    public static void readInput(String filePath, IHandleLong handler) throws IOException {
        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);
        DataInputStream dis = new DataInputStream(fis);

        try {
            long index = 0;
            while (true) {
                long l = dis.readLong();
                if (handler != null) {
                    handler.push(l);
                } else {
                    System.out.println(index + " " + l);
                }
                index += 1;
            }
        } catch (EOFException e) {
            fis.close();
        }

        handler.donePushingLongs();
    }

    public static ArrayList<Long> readInput(String filePath) throws IOException {
        ArrayList<Long> longs = new ArrayList<Long>();

        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);
        DataInputStream dis = new DataInputStream(fis);

        try {
            long index = 0;
            while (true) {
                long l = dis.readLong();
                longs.add(l);
                index += 1;
            }
        } catch (EOFException e) {
            fis.close();
        }

        return longs;
    }
}
