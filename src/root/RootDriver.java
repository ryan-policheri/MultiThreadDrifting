package root;

import bucketsort1.BucketSort1Processor;
import common.BaseLineSortProcessor;
import common.BinaryFileToTextFile;
import common.DataSetGenerator;
import common.ISortFile;
import mapreduce.MapReduceProcessor;

import java.io.IOException;

public class RootDriver {
    public static String BASELINE = "BASELINE";
    public static String BUCKET_SORT = "BUCKET_SORT";
    public static String MAP_REDUCE = "MAP_REDUCE";
    public static String QUICK_SORT = "QUICK_SORT";

    public static void main(String[] args) throws IOException { //Main driver that can all our solutions and serve as a utility for generating files
        if (args[0].equals("--GenerateFile")) generateFile(args); //user called the utility for generating files
        else { //normal input to call our solutions
            String inputFilePath = args[0].replace("\"", "");
            String outputFilePath = args[1].replace("\"", "");
            int numberOfThreads = Integer.parseInt(args[2]);
            String solution = args[3].toUpperCase();
            int inputSize = Integer.parseInt(args[1]);

            ISortFile processor;
            if (solution.equals(BASELINE)) processor = new BaseLineSortProcessor();
            else if (solution.equals(BUCKET_SORT)) processor = new BucketSort1Processor(100, numberOfThreads); //we don't know what a good bucket length is ahead of time, that's the problem w/ bucket sort
            else if (solution.equals(MAP_REDUCE)) processor = new MapReduceProcessor(numberOfThreads);
        }
    }

    private static void generateFile(String[] args) throws IOException {
        String path = args[1];
        String fileLength = args[2];
        String generationType = args[3];

        path = path.replace("\"", "");
        int inputSize = Integer.parseInt(fileLength);
        generationType = generationType.toUpperCase();

        DataSetGenerator.GenerateInputFile(path, inputSize, generationType);
        if (args.length == 5 && args[4].toUpperCase().equals("TRUE")) BinaryFileToTextFile.ConvertBinaryLongsToTextLongs(path);
    }
}
