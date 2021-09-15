package bucketsort1;

import java.io.FileNotFoundException;
import java.io.FileWriter;

public class BucketSort1Processor {
    private long _bucketLength;
    private int _workerThreads;

    public BucketSort1Processor(long bucketLength, int workerThreads) {
        _bucketLength = bucketLength;
        _workerThreads = workerThreads;
    }

    public String sortFile(String inputFilePath) throws FileNotFoundException {
        BucketManager manager = new BucketManager(_bucketLength);
        BinaryInputReader reader = new BinaryInputReader(inputFilePath, manager);

        SortWorker[] workers = new SortWorker[_workerThreads];
        for (int i = 0; i < _workerThreads; i++) {
            workers[i] = new SortWorker(manager);
        }

        reader.start();
        for (SortWorker worker : workers) {
            worker.start();
        }

        boolean done = false;

        while (!done) {
            boolean readerDone = !reader.isRunning();

            for (int i = 0; i < _workerThreads; i++) {
                if (!workers[i].isRunning()) {
                    workers[i] = new SortWorker(manager); //worker completed, make a new one
                    workers[i].start();
                }
            }

            if (readerDone) {
                if (manager.isEverythingSorted()) {done = true; }
                else { manager.flush(); }

            }
        }

        System.out.println("Program put " + manager.bucketContentSum() + " items into buckets");
        return null;
    }
}
