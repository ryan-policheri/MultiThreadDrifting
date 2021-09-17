package mapreduce;

import common.DataLoader;
import common.ISortFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MapReduceProcessor implements ISortFile {
    private int _numberOfThreads;

    public MapReduceProcessor(int numberOfThreads){
        _numberOfThreads = numberOfThreads;
    }

    @Override
    public String sortFile(String inputFilePath) throws IOException {
        if (_numberOfThreads == 1) {
            ArrayList<Long> longs = DataLoader.readInput(inputFilePath);
            Reducer reducer = new Reducer(longs);
            ReducedRecord[] reducedRecords = reducer.ReduceAndSort();
            String outputFile = calculateOutputFilePath(inputFilePath);
            ReducedRecordWriter.WriteRecords(outputFile, reducedRecords);
            return outputFile;
        } else {
            return null;
        }
    }

    private String calculateOutputFilePath(String inputFilePath) {
        File file = new File(inputFilePath);
        String directory = file.getParent();
        String name = file.getName();
        name = "MapReduceProcessor_Sorted_" + name;
        String filePath = Paths.get(directory, name).toString();
        return filePath;
    }
}
