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
        int chunkSize = Driver.longs.length / Driver.numberOfThreads;
        int startIndex;
        int endIndex;
        int[] indices;

        for (int i = 0; i < Driver.numberOfThreads; i++) {
            startIndex = chunkSize * i;
            endIndex = chunkSize * i + chunkSize - 1;

            if (i == Driver.numberOfThreads - 1) {
                endIndex = Driver.longs.length - 1;
            }

            for (int j = startIndex; j <= endIndex; j++) {
                Driver.longs[j] = dataInputStream.readLong();
            }

            indices = new int[]{startIndex, endIndex};
            Driver.sortableChunkQueue.add(indices);
        }

        dataInputStream.close();
    }
}
