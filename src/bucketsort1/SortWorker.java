package bucketsort1;

import bucketsort1.BucketManager;

public class SortWorker implements Runnable {
    private BucketManager _sharedBucketManager;
    private Thread _thread;

    public SortWorker(BucketManager bucketManager) {
        _sharedBucketManager = bucketManager;
    }

    @Override
    public void run() {
        Bucket bucket = _sharedBucketManager.checkoutAnyBucket();
        if (bucket != null) {
            //System.out.println("Sorted"+bucket.getName()+" "+bucket.isSorted());
        	bucket.addBuffer();
            bucket.sort();
            bucket.checkin();
        }
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
