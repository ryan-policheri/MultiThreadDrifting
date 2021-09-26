package mapreduce;

import common.DataLoader;
import common.ISortFile;
import common.ReadLongInputJob;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class MapReduceProcessor implements ISortFile {
    private final int _bytesPerLong = 8;
    private final int _numberOfThreads;
    private final int _chunkMultiplier;

    public MapReduceProcessor(int numberOfThreads) {
        _numberOfThreads = numberOfThreads;
        _chunkMultiplier = 2;
    }

    @Override
    public void sortFile(String inputFilePath, String outputFile) throws IOException {
        if (_numberOfThreads == 1) {
            ArrayList<Long> longs = DataLoader.readInput(inputFilePath);
            Reducer reducer = new Reducer(longs);
            ReductionResult result = reducer.ReduceAndSort();
            ReducedRecordWriter.WriteRecords(outputFile, result.Records);
        } else {
            File file = new File(inputFilePath);
            long lengthInBytes = file.length();
            int longCount = (int)(lengthInBytes / _bytesPerLong);
            int chunks = _numberOfThreads * _chunkMultiplier;
            MapReduceDataManager reduceDataManager = new MapReduceDataManager(longCount, chunks);

            BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(1024);
            ThreadPoolExecutor pool = new ThreadPoolExecutor(_numberOfThreads, _numberOfThreads, 10, TimeUnit.SECONDS, workQueue);

            ReadLongInputJob readerJob = new ReadLongInputJob(inputFilePath, reduceDataManager);
            pool.execute(readerJob);

            boolean done = false;
            while (!done) {
                if (reduceDataManager.hasReducedAllData()) { done = true; }
                else {
                    ArrayList<Long> chunk = reduceDataManager.takeLongChunk();

                    if (chunk != null) {
                        ReduceJob reduceJob = new ReduceJob(reduceDataManager, chunk);
                        pool.execute(reduceJob);
                    } else {
                        ReductionResultPair pair = reduceDataManager.tryPopReducedRecordPair();
                        if (pair != null) {
                            ReduceJob reduceJob = new ReduceJob(reduceDataManager, pair);
                            pool.execute(reduceJob);
                        }
                    }
                }
            }

            ReducedRecordWriter.WriteRecords(outputFile, reduceDataManager.getFinalResult());
        }
    }
}
