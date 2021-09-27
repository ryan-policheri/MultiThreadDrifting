package common;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class DataLoader {
    public static void main(String[] args) throws IOException {
        String filePath = System.getProperty("user.dir") + "\\" + "array.bin";
        readInput(filePath, null);
    }

    public static void readInput(String filePath, IHandleLong handler) throws IOException {
        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bufferedReader = new BufferedInputStream(fis);

        byte[] bytes = new byte[8];
        ByteBuffer buffer;

        try {
            while (bufferedReader.read(bytes, 0, 8) != -1) {
                buffer = ByteBuffer.wrap(bytes);
                long l = buffer.getLong();
                handler.push(l);
            }
        } catch (EOFException e) {
        } finally {
            fis.close();
            bufferedReader.close();
        }

        handler.donePushingLongs();
    }

    public static ArrayList<Long> readInput(String filePath) throws IOException {
        ArrayList<Long> longs = new ArrayList<Long>();

        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bufferedReader = new BufferedInputStream(fis);

        byte[] bytes = new byte[8];
        ByteBuffer buffer;

        try {
            while (bufferedReader.read(bytes, 0, 8) != -1) {
                buffer = ByteBuffer.wrap(bytes);
                long l = buffer.getLong();
                longs.add(l);
            }
        } catch (EOFException e) {
        } finally {
            fis.close();
            bufferedReader.close();
        }

        return longs;
    }
}
