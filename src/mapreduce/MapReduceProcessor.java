package mapreduce;

import common.DataLoader;
import common.GenericInputWorker;
import common.ISortFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class MapReduceProcessor implements ISortFile {
    private int _numberOfThreads;
    private int _chunkMultiplier;

    public MapReduceProcessor(int numberOfThreads) {
        _numberOfThreads = numberOfThreads;
        _chunkMultiplier = 250000;
    }

    @Override
    public String sortFile(String inputFilePath) throws IOException {
        String outputFile = calculateOutputFilePath(inputFilePath);

        if (_numberOfThreads == 1) {
            ArrayList<Long> longs = DataLoader.readInput(inputFilePath);
            Reducer reducer = new Reducer(longs);
            ReducedRecord[] reducedRecords = reducer.ReduceAndSort();
            ReducedRecordWriter.WriteRecords(outputFile, reducedRecords);
            return outputFile;
        } else {
            File file = new File(inputFilePath);
            long length = file.length();
            int chunks = _numberOfThreads * _chunkMultiplier;
            MapReduceDataManager reduceDataManager = new MapReduceDataManager(length, chunks);

            GenericInputWorker reader = new GenericInputWorker(inputFilePath, reduceDataManager);
            reader.start();

            ReduceWorker[] reduceWorkers = new ReduceWorker[_numberOfThreads];

            ArrayList<ReducedRecord[]> reducedRecordsCollection = new ArrayList<ReducedRecord[]>();

            boolean done = false;
            while (!done) {
                int i = 1;
                if (reader != null && reader.isRunning() == false) { reader = null; }
                if (reader == null) { i = 0; } //Reader is done, let another worker in

                for (i = i; i < _numberOfThreads; i++) {
                    ReduceWorker worker = reduceWorkers[i];
                    if (worker == null) {
                        ArrayList<Long> chunk = reduceDataManager.takeChunk();
                        if (chunk != null) {
                            reduceWorkers[i] = new ReduceWorker(chunk);
                            reduceWorkers[i].start();
                        } else if(reducedRecordsCollection.size() >= 2) {
                            ReducedRecord[] set1 = reducedRecordsCollection.remove(0);
                            ReducedRecord[] set2 = reducedRecordsCollection.remove(0);
                            reduceWorkers[i] = new ReduceWorker(set1, set2);
                            reduceWorkers[i].start();
                        }
                    } else if (worker.isRunning() == false) {
                        ReducedRecord[] records = worker.getReducedRecords();
                        reducedRecordsCollection.add(records);
                        reduceWorkers[i] = null;
                    }
                }

                //if all workers are null done with loop
                if (reader == null && reducedRecordsCollection.size() == 1) {
                    done = true;
                    for (ReduceWorker worker : reduceWorkers) {
                        if (worker != null) { done = false; }
                    }
                }
            }

            ReducedRecordWriter.WriteRecords(outputFile, reducedRecordsCollection.get(0));
            return outputFile;
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
