package quicksort;

import common.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Driver {
    public static void main(String[] args) throws IOException, InterruptedException {
        String inputFile = args[0];
        String outputFile = args[1];
        int numberOfThreads = Integer.parseInt(args[2]);
        ArrayList<Trial> trials = new ArrayList<Trial>();

        Trial baseLineTrial = new Trial();
        baseLineTrial.InputFile = inputFile;
        baseLineTrial.OutputFile = "src/quicksort/sorted_baseline.bin";
        baseLineTrial.InputSize = -1;
        baseLineTrial.SolutionName = "Baseline Processor";

        BaseLineSortProcessor baselineProcessor = new BaseLineSortProcessor();
        runProcessor(baselineProcessor, baseLineTrial);
        baseLineTrial.VerificationFile = baseLineTrial.OutputFile;
        baseLineTrial.ValidTrial = true;
        trials.add(baseLineTrial);

        Trial quickSortTrial = new Trial();
        quickSortTrial.InputFile = inputFile;
        quickSortTrial.OutputFile = outputFile;
        quickSortTrial.InputSize = -1;
        quickSortTrial.SolutionName = "QuickSort Processor";
        quickSortTrial.VerificationFile = baseLineTrial.VerificationFile;

        QuickSortProcessor processor = new QuickSortProcessor(numberOfThreads);

        runProcessor(processor, quickSortTrial);
        trials.add(quickSortTrial);

        for (Trial trial : trials) {
            if (!trial.ValidTrial) {
                BinaryFileToTextFile.ConvertBinaryLongsToTextLongs(trial.OutputFile);
            }
            System.out.println(trial.SolutionName + " took " + trial.RunTimeInSeconds + " seconds to execute " +
                    "(" + trial.RunTimeInNanoSeconds + " nanoseconds)." + "Solution is valid? " + trial.ValidTrial);
        }
    }

    private static void runProcessor(ISortFile fileProcessor, Trial trial) throws IOException, InterruptedException {
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
}
