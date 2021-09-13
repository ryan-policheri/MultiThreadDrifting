import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class BucketManager {
    private long _bucketLength;
    private Hashtable<String, Bucket> _buckets;
    private ArrayList<String> _bucketKeys;
    private ArrayList<Long> _buffer;

    public BucketManager(long bucketLength) {
        _bucketLength = bucketLength;
        _buckets = new Hashtable<String, Bucket>();
        _bucketKeys = new ArrayList<String>();
        _buffer = new ArrayList<Long>(); //if a bucket is checked out for sorting when received we can push the value here to process later so that we don't hold up I/O
    }

    public void push(long number) {
        Bucket bucket = findBucket(number);
        if (bucket == null) {
            bucket = createBucket(number);
            String name = bucket.getName();
            _buckets.put(name, bucket);
        }
        if (!bucket.isCheckedOut()) {
            bucket.addItem(number);
        }
        else {
            _buffer.add(number);
        }
    }

    public void flush() {
        for (long number : _buffer) {
            this.push(number);
        }
    }

    public Bucket checkoutAnyBucket() {
        for (String key : _bucketKeys) {
            Bucket bucket = _buckets.get(key);
            if (bucket != null){
                if (!bucket.isCheckedOut()) { return bucket.checkout(); }
            }
        }
        return null; //all buckets are checked out
    }

    public boolean isEverythingSorted() {
        if (_buffer.size() > 0) { return false; }

        for (String key : _bucketKeys) {
            Bucket bucket = _buckets.get(key);
            if (!bucket.isSorted() || bucket.isCheckedOut()) { return false; }
        }

        return true;
    }

    private Bucket findBucket(long number) {
        String bucketName = calculateBucketName(number);
        Bucket bucket = _buckets.get(bucketName);
        return bucket;
    }

    private Bucket createBucket(long number) {
        long bucketNumber = calculateBucketNumber(number);
        String bucketName = calculateBucketName(number);
        long lowerBound = bucketNumber * _bucketLength;
        long upperBound = (lowerBound + _bucketLength) - 1;
        _bucketKeys.add(bucketName);
        return new Bucket(bucketName, lowerBound, upperBound);
    }

    private long calculateBucketNumber(long number) {
        long bucketNumber = number / _bucketLength;
        return bucketNumber;
    }

    private String calculateBucketName(long number) {
        long bucketNumber = calculateBucketNumber(number);
        String name = "Bucket_" + bucketNumber;
        return name;
    }
}
