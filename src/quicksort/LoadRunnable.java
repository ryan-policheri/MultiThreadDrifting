package quicksort;

import java.io.DataInputStream;
import java.io.IOException;

public class LoadRunnable implements Runnable {
    private final DataInputStream dataInputStream;

    @Override
    public void run() {
        try {
            load();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public LoadRunnable(DataInputStream dataInputStream) {
        this.dataInputStream = dataInputStream;
    }

    private void load() throws IOException {
        int chunkSize = QuickSortProcessor.longs.length / QuickSortProcessor.numberOfThreads;
        int startIndex;
        int endIndex;
        int[] indices;

        for (int i = 0; i < QuickSortProcessor.numberOfThreads; i++) {
            startIndex = chunkSize * i;
            endIndex = chunkSize * i + chunkSize - 1;

            if (i == QuickSortProcessor.numberOfThreads - 1) {
                endIndex = QuickSortProcessor.longs.length - 1;
            }

            for (int j = startIndex; j <= endIndex; j++) {
                QuickSortProcessor.longs[j] = dataInputStream.readLong();
            }

            indices = new int[]{startIndex, endIndex};
            QuickSortProcessor.sortableChunkQueue.add(indices);
        }

        dataInputStream.close();
    }
}
