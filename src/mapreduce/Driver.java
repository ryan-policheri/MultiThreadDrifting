package mapreduce;

import common.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Driver {
    private static final int _workerThreads = 16;
    private static final int _inputLength = 1000000;

    public static void main(String[] args) throws IOException, InterruptedException {
        String inputFile = args[0]; //This file would come generated. But generating it below for convenience
        inputFile = System.getProperty("user.dir") + "\\" + "array.bin";
        String outputFile = args[1];

        try {
            DataSetGenerator.GenerateInputFile(inputFile, _inputLength);
        } catch (IOException ex) {
            System.out.println("Error generating input file" + ex);
            return;
        }

        ArrayList<Trial> trials = new ArrayList<Trial>();

        Trial baseLineTrial = new Trial();
        baseLineTrial.InputFile = inputFile;
        baseLineTrial.InputSize = _inputLength;
        baseLineTrial.SolutionName = "Baseline Processor";

        BaseLineSortProcessor baselineProcessor = new BaseLineSortProcessor();
        runProcessor(baselineProcessor, baseLineTrial);
        baseLineTrial.VerificationFile = baseLineTrial.OutputFile;
        baseLineTrial.ValidTrial = true;
        trials.add(baseLineTrial);

        Trial mapReduceTrial = new Trial();
        mapReduceTrial.InputFile = inputFile;
        mapReduceTrial.InputSize = _inputLength;
        mapReduceTrial.SolutionName = "Map Reduce Processor";
        mapReduceTrial.VerificationFile = baseLineTrial.VerificationFile;

        MapReduceProcessor mapReduceProcessor = new MapReduceProcessor(_workerThreads);
        runProcessor(mapReduceProcessor, mapReduceTrial);
        trials.add(mapReduceTrial);

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
