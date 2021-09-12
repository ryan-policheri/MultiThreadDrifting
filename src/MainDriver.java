import java.io.FileNotFoundException;
import java.io.IOException;

public class MainDriver {
    private static final char _delimiter = ',';
    private static final long _bucketLength = 10000;

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        String inputFile = args[0]; //This file would come generated. But generating it below for convenience
        String outputFile = args[1];

        try { inputFile = GenerateInputDriver.GenerateInputFile(inputFile, 1000000); }
        catch (IOException ex) { System.out.println("Error generating input file"); return; }

        long startTime = System.nanoTime();

        BucketManager manager = new BucketManager(_bucketLength);
        InputReader reader = new InputReader(inputFile, _delimiter, manager);
        reader.start();
    }
}
