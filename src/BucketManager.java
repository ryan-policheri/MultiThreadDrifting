import java.util.Dictionary;
import java.util.Hashtable;

public class BucketManager {
    private long _bucketLength;
    private Dictionary<String, Bucket> _buckets;

    public BucketManager(long bucketLength) {
        _bucketLength = bucketLength;
        _buckets = new Hashtable<String, Bucket>();
    }

    public void push(long number) {
        Bucket bucket = findBucket(number);
        if (bucket == null) {
            bucket = CreateBucket(number);
            String name = bucket.getName();
            _buckets.put(name, bucket);
        }
        bucket.addItem(number);
    }

    private Bucket findBucket(long number) {
        String bucketName = calculateBucketName(number);
        Bucket bucket = _buckets.get(bucketName);
        return bucket;
    }

    private Bucket CreateBucket(long number) {
        long bucketNumber = calculateBucketNumber(number);
        String bucketName = calculateBucketName(number);
        long lowerBound = bucketNumber * _bucketLength;
        long upperBound = (lowerBound + _bucketLength) - 1;
        return new Bucket(bucketName, lowerBound, upperBound);
    }

    private long calculateBucketNumber(long number) {
        double value = number / _bucketLength;
        long bucketNumber = (long) value;
        return bucketNumber;
    }

    private String calculateBucketName(long number) {
        long bucketNumber = calculateBucketNumber(number);
        String name = "Bucket_" + bucketNumber;
        return name;
    }
}
