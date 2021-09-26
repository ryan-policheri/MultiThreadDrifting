package quicksort;

import common.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class Driver {
    public static void main(String[] args) throws IOException, InterruptedException {
        String inputFile = args[0];
        int numberOfThreads = Integer.parseInt(args[1]);
        ArrayList<Trial> trials = new ArrayList<Trial>();

        Trial baseLineTrial = new Trial();
        baseLineTrial.InputFile = inputFile;
        baseLineTrial.InputSize = -1;
        baseLineTrial.SolutionName = "Baseline Processor";

        BaseLineSortProcessor baselineProcessor = new BaseLineSortProcessor();
        runProcessor(baselineProcessor, baseLineTrial);
        baseLineTrial.VerificationFile = baseLineTrial.OutputFile;
        baseLineTrial.ValidTrial = true;
        trials.add(baseLineTrial);

        Trial quickSortTrial = new Trial();
        quickSortTrial.InputFile = inputFile;
        quickSortTrial.InputSize = -1;
        quickSortTrial.SolutionName = "QuickSort Processor";
        quickSortTrial.VerificationFile = baseLineTrial.VerificationFile;

        QuickSortProcessor processor = new QuickSortProcessor(numberOfThreads);

        runProcessor(processor, quickSortTrial);
        trials.add(quickSortTrial);

        for (Trial trial : trials) {
            if (trial.ValidTrial == false) {
                BinaryFileToTextFile.ConvertBinaryLongsToTextLongs(trial.OutputFile);
            }
            System.out.println(trial.SolutionName + " took " + trial.RunTimeInSeconds + " seconds to execute " +
                    "(" + trial.RunTimeInNanoSeconds + " nanoseconds)." + "Solution is valid? " + trial.ValidTrial);
        }
    }

    private static void runProcessor(ISortFile fileProcessor, Trial trial) throws IOException, InterruptedException {
        trial.OutputFile = calculateOutputFilePath(trial.InputFile, trial.SolutionName);
        long startTime = System.nanoTime();
        fileProcessor.sortFile(trial.InputFile, trial.OutputFile);
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        long durationInSeconds = duration / 1000000000;
        trial.RunTimeInNanoSeconds = duration;
        trial.RunTimeInSeconds = durationInSeconds;

        TrialValidator validator = new TrialValidator();
        validator.validateTrial(trial);
    }

    private static String calculateOutputFilePath(String inputFilePath, String solutionName) {
        File file = new File(inputFilePath);
        String directory = file.getParent();
        String name = file.getName();
        name = solutionName + "_Sorted_" + name;
        String filePath = Paths.get(directory, name).toString();
        return filePath;
    }
}
