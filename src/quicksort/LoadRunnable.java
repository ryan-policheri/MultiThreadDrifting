package quicksort;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class LoadRunnable implements Runnable {
    private final FileInputStream fileInputStream;

    @Override
    public void run() {
        try {
            load();
        } catch (IOException exception) {
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
            endIndex = chunkSize * i + chunkSize - 1;

            if (i == QuickSortProcessor.numberOfThreads - 1) {
                endIndex = QuickSortProcessor.longs.length - 1;
            }

            for (int j = startIndex; j <= endIndex; j++) {
                bufferedInputStream.read(buffer, 0, 8);
                byteBuffer = ByteBuffer.wrap(buffer);
                QuickSortProcessor.longs[j] = byteBuffer.getLong();
            }

            indices = new int[]{startIndex, endIndex};
            QuickSortProcessor.sortableChunkQueue.add(indices);
        }
    }
}
