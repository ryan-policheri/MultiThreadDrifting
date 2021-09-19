package mapreduce;

import common.DataLoader;
import common.GenericInputWorker;
import common.ISortFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

public class MapReduceProcessor implements ISortFile {
    private int _numberOfThreads;
    private int _chunkMultiplier;

    public MapReduceProcessor(int numberOfThreads) {
        _numberOfThreads = numberOfThreads;
        _chunkMultiplier = 2;
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

            BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(1024);
            ThreadPoolExecutor pool = new ThreadPoolExecutor(_numberOfThreads, _numberOfThreads, 10, TimeUnit.SECONDS, workQueue);

            GenericInputWorker reader = new GenericInputWorker(inputFilePath, reduceDataManager);
            pool.execute(reader);

            boolean done = false;
            while (!done) {
                if (reduceDataManager.hasReducedAllData()) { done = true; }
                else {
                    ArrayList<Long> chunk = reduceDataManager.takeLongChunk();

                    if (chunk != null) {
                        ReduceWorker worker = new ReduceWorker(reduceDataManager, chunk);
                        pool.execute(worker);
                    } else {
                        ReducedRecordPair pair = reduceDataManager.tryPopReducedRecordPair();
                        if (pair != null) {
                            ReduceWorker worker = new ReduceWorker(reduceDataManager, pair);
                            pool.execute(worker);
                        }
                    }
                }
            }

            ReducedRecordWriter.WriteRecords(outputFile, reduceDataManager.getFinalResult());
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
