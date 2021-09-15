package common;

import java.io.*;

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
    }
}
