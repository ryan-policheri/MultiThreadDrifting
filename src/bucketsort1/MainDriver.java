package bucketsort1;

import common.BaseLineSortProcessor;
import common.DataSetGenerator;
import common.GenerateInputDriver;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainDriver {
    private static final long _bucketLength = 50000000000000000L;
    private static final int _workerThreads = 4;
    private static final int _inputLength = 10;

    public static void main(String[] args) throws FileNotFoundException, IOException {
        String inputFile = args[0]; //This file would come generated. But generating it below for convenience
        if (inputFile == null) { inputFile = System.getProperty("user.dir") + "\\" + "array.bin"; }
        String outputFile = args[1];

        try { DataSetGenerator.GenerateInputFile(inputFile, _inputLength); }
        catch (IOException ex) { System.out.println("Error generating input file"+ex); return; }

        BaseLineSortProcessor baselineProcessor = new BaseLineSortProcessor();
        String baseLineOutputFile = baselineProcessor.sortFile(inputFile);

        long startTime = System.nanoTime();

        BucketSort1Processor bucketSort1Processor = new BucketSort1Processor(_bucketLength, _workerThreads);
        String bucketSortOutputFile = bucketSort1Processor.sortFile(inputFile);

        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        long durationInSeconds = duration / 1000000000;
        System.out.println("Program took " + durationInSeconds + " to execute");
    }
}
