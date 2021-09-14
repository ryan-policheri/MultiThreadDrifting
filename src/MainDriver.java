import java.io.FileNotFoundException;
import java.io.IOException;

public class MainDriver {
    private static final char _delimiter = ',';
    private static final long _bucketLength = 50000000000000000L;
    private static final int _workerThreads = 4;
    private static final int _inputLength = 10000000;

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        String inputFile = args[0]; //This file would come generated. But generating it below for convenience
        String outputFile = args[1];

        try { inputFile = GenerateInputDriver.GenerateInputFile(inputFile, _inputLength); }
        catch (IOException ex) { System.out.println("Error generating input file"+ex); return; }

        long startTime = System.nanoTime();

        BucketManager manager = new BucketManager(_bucketLength);
        InputReader reader = new InputReader(inputFile, _delimiter, manager);

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
                if (manager.isEverythingSorted()) { done = true; }
                else { manager.flush(); }
                
            }
        }

        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        long durationInSeconds = duration / 1000000000;
        System.out.println("Program took " + durationInSeconds + " to execute");
    }
}
