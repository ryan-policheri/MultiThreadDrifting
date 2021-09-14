package lvnl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class TestFileGenerator {
    public static void main(String[] args) {
        int numberOfLongs = Integer.parseInt(args[0]);
        String fileName = String.format("test_file_size_%d.csv", numberOfLongs);

        createFile(fileName);

        Random random = new Random();
        LongStream longs = random.longs(numberOfLongs);
        String longsAsCSV = longStreamToCSV(longs);

        writeToFile(fileName, longsAsCSV);
    }

    private static void createFile(String fileName) {
        File file = new File(fileName);

        if (file.exists()) {
            file.delete();
        }

        try {
            file.createNewFile();
        } catch (IOException exception) {
            System.err.println(String.format("An error ocurred while creating \"%s\"!", fileName));
        }
    }

    private static void writeToFile(String fileName, String content) {
        try {
            Writer writer = new FileWriter(fileName);
            writer.write(content);
            writer.close();
        } catch (IOException exception) {
            System.err.println(String.format("An error ocurred while writing to \"%s\"!", fileName));
        }
    }

    private static String longStreamToCSV(LongStream longs) {
        Stream<String> longsAsStrings = longs.mapToObj(i -> String.valueOf(i));
        String longsAsCSV = longsAsStrings.collect(Collectors.joining(","));
        
        return longsAsCSV;
    } 
}