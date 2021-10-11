package root;

import common.BinaryFileToTextFile;
import common.DataSetGenerator;
import common.ISortFile;
import mapreduce.MapReduceProcessor;
import quicksort.QuickSortProcessor;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

public class RootDriver {
    public static String MAP_REDUCE = "MAP_REDUCE";
    public static String QUICK_SORT = "QUICK_SORT";

    public static void main(String[] args) throws IOException, InterruptedException { //Main driver that can all our solutions and serve as a utility for generating files
        if (args[0].equals("--GenerateFile")) generateFile(args); //user called the utility for generating files
        else { //normal input to call our solutions
            String inputFilePath = args[0].replace("\"", "");
            String outputFilePath = args[1].replace("\"", "");
            int numberOfThreads = Integer.parseInt(args[2]);
            String solution;
            if (args.length >= 4) solution = args[3].toUpperCase(); //You can specify MAP_REDUCE or QUICK_SORT as the 4th arg. Otherwise default to QUICK_SORT
            else solution = QUICK_SORT;

            ISortFile processor;
            if (solution.equals(MAP_REDUCE)) processor = new MapReduceProcessor(numberOfThreads);
            else if (solution.equals(QUICK_SORT)) processor = new QuickSortProcessor(numberOfThreads);
            else throw new IllegalArgumentException("Solution name not known");

            Instant totalStartTime = Instant.now();
            processor.sortFile(inputFilePath, outputFilePath);
            Instant totalEndTime = Instant.now();
            long totalTimeTaken = Duration.between(totalStartTime, totalEndTime).toMillis();
            System.out.printf("Total time taken for " + solution + " with " + numberOfThreads + " threads" + ": %dms\n", totalTimeTaken);
            System.exit(0);
        }
    }

    private static void generateFile(String[] args) throws IOException {
        String path = args[1];
        String fileLength = args[2];
        String generationType = args[3];

        path = path.replace("\"", "");
        int inputSize = Integer.parseInt(fileLength);
        generationType = generationType.toUpperCase();

        int differentNumberCount = -1;
        if (generationType.equals(DataSetGenerator.X_DIFFERENT_NUMBERS)) {
            differentNumberCount = Integer.parseInt(args[4]);
        }

        DataSetGenerator.GenerateInputFile(path, inputSize, generationType, differentNumberCount);

        if (!generationType.equals(DataSetGenerator.X_DIFFERENT_NUMBERS) && args.length == 5 && args[4].toUpperCase().equals("TRUE"))
            BinaryFileToTextFile.ConvertBinaryLongsToTextLongs(path);
        else if (generationType.equals(DataSetGenerator.X_DIFFERENT_NUMBERS) && args.length == 6 && args[5].toUpperCase().equals("TRUE"))
            BinaryFileToTextFile.ConvertBinaryLongsToTextLongs(path);
    }
}