package mapreduce;

import common.*;
import java.io.IOException;
import java.util.ArrayList;

public class MainDriver {
    private static final int _workerThreads = 4;
    private static final int _inputLength = 1000000;


    public static void main(String[] args) throws IOException {
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
            System.out.println(trial.SolutionName + " took " + trial.RunTimeInSeconds + " seconds to execute. Solution is valid? " + trial.ValidTrial);
        }
    }

    private static void runProcessor(ISortFile fileProcessor, Trial trial) throws IOException {
        long startTime = System.nanoTime();
        trial.OutputFile = fileProcessor.sortFile(trial.InputFile);
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        long durationInSeconds = duration / 1000000000;
        trial.RunTimeInNanoSeconds = duration;
        trial.RunTimeInSeconds = durationInSeconds;

        TrialValidator validator = new TrialValidator();
        validator.validateTrial(trial);
    }
}
