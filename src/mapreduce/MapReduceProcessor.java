package mapreduce;

import common.DataLoader;
import common.ISortFile;
import common.MetricLogger;
import common.ReadLongInputJob;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class MapReduceProcessor implements ISortFile {
    private final int _bytesPerLong = 8;
    private final int _numberOfThreads;
    private final int _chunkMultiplier;

    //private MetricLogger _metricLogger;

    public MapReduceProcessor(int numberOfThreads) {
        _numberOfThreads = numberOfThreads;
        _chunkMultiplier = 1;
        //_metricLogger = new MetricLogger();
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
                    //var temp = _metricLogger.startRecording("Taking Raw Long Chunk");
                    ArrayList<Long> chunk = reduceDataManager.takeLongChunk();
                    //_metricLogger.stopRecording(temp);

                    if (chunk != null) {
                        ReduceJob reduceJob = new ReduceJob(reduceDataManager, chunk);
                        pool.execute(reduceJob);
                    } else {
                        //temp = _metricLogger.startRecording("Checking out pair");
                        ReductionResultPair pair = reduceDataManager.tryPopReducedRecordPair();
                        //_metricLogger.stopRecording(temp);
                        if (pair != null) {
                            ReduceJob reduceJob = new ReduceJob(reduceDataManager, pair);
                            pool.execute(reduceJob);
                        }
                    }
                }
            }

            //var temp = _metricLogger.startRecording("Writing final results");
            ReducedRecordWriter.WriteRecords(outputFile, reduceDataManager.getFinalResult());
            //_metricLogger.stopRecording(temp);

            //_metricLogger.writeToConsole();
        }
    }

    public MetricLogger get_metricLogger() {
        return null;//_metricLogger;
    }

}
