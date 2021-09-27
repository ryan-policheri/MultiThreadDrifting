package quicksort;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;

public class LoadRunnable implements Runnable {
    private final FileInputStream fileInputStream;
    private long[] longsCopy = new long[QuickSortProcessor.longs.length];

    @Override
    public void run() {
        try {
            load();
            checkForPatterns();
        } catch (IOException | InterruptedException exception) {
            exception.printStackTrace();
        }
    }

    public LoadRunnable(FileInputStream fileInputStream) {
        this.fileInputStream = fileInputStream;
    }

    private void load() throws IOException {
        int chunkSize = QuickSortProcessor.longs.length / QuickSortProcessor.numberOfThreads;
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        byte[] buffer = new byte[8];
        ByteBuffer byteBuffer;

        int startIndex;
        int endIndex;
        int[] indices;

        for (int i = 0; i < QuickSortProcessor.numberOfThreads; i++) {
            startIndex = chunkSize * i;
            endIndex = startIndex + chunkSize - 1;

            if (i == QuickSortProcessor.numberOfThreads - 1) {
                endIndex = QuickSortProcessor.longs.length - 1;
            }

            for (int j = startIndex; j <= endIndex; j++) {
                bufferedInputStream.read(buffer, 0, Byte.SIZE);
                byteBuffer = ByteBuffer.wrap(buffer);
                QuickSortProcessor.longs[j] = byteBuffer.getLong();
            }

            System.arraycopy(QuickSortProcessor.longs, startIndex, longsCopy, startIndex, chunkSize);

            indices = new int[]{startIndex, endIndex};
            QuickSortProcessor.sortableChunkQueue.add(indices);
        }
    }

    private void checkForPatterns() throws InterruptedException, IOException {
        boolean isSorted = true;
        boolean isBackwards = true;
        boolean isRepeating = true;

        for (int i = 0; i < longsCopy.length - 1; i++) {
            if (longsCopy[i] > longsCopy[i + 1]) {
                isSorted = false;
                isRepeating = false;
            } else if (longsCopy[i] < longsCopy[i + 1]) {
                isBackwards = false;
                isRepeating = false;
            }
        }

        if (isSorted | isRepeating) {
            QuickSortProcessor.writeFile(longsCopy, QuickSortProcessor.PrintDirection.FORWARDS);
            killMainThread();
        } else if (isBackwards) {
            QuickSortProcessor.writeFile(longsCopy, QuickSortProcessor.PrintDirection.BACKWARDS);
            killMainThread();
        }
    }

    private void killMainThread() throws IOException {
        Instant endTime = Instant.now();
        long timeTaken = Duration.between(QuickSortProcessor.startTime, endTime).toMillis();
        System.out.printf("Total time taken: %d\n", timeTaken);
        QuickSortProcessor.verifyOutputFile();
        System.exit(0);
    }
}
