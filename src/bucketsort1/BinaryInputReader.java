package bucketsort1;

import common.DataLoader;
import java.io.IOException;

public class BinaryInputReader implements Runnable {
    private String _filePath;
    private BucketManager _sharedBucketManager;
    private Thread _thread;

    public BinaryInputReader(String filePath, BucketManager bucketManager) {
        _filePath = filePath;
        _sharedBucketManager = bucketManager;
    }

    @Override
    public void run() {
        try {
            DataLoader.readInput(_filePath, _sharedBucketManager);
        } catch (IOException ex) { /*What to do?*/ }
    }

    public void start() {
        if (_thread == null) {
            _thread = new Thread(this);
            _thread.start();
        }
    }

    public boolean isRunning() {
        return _thread.isAlive();
    }
}
