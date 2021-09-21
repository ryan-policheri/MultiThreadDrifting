package bucketsort1;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;

public class BucketManager implements common.IHandleLong {
    private long _bucketLength;
    private Hashtable<BucketKey, Bucket> _buckets;
    private ArrayList<BucketKey> _bucketKeys;
    private ArrayList<Long> _buffer;

    public BucketManager(long bucketLength) {
        _bucketLength = bucketLength;
        _buckets = new Hashtable<BucketKey, Bucket>();
        _bucketKeys = new ArrayList<BucketKey>();
        _buffer = new ArrayList<Long>(); //if a bucket is checked out for sorting when received we can push the value here to process later so that we don't hold up I/O
    }

    @Override
    public synchronized void push(long number) {
        Bucket bucket = findBucket(number);
        if (bucket == null) {
            bucket = createBucket(number);
            BucketKey key = bucket.getBucketKey();
            _bucketKeys.add(key);
            _buckets.put(key, bucket);
        }
        bucket.addItem(number); // since items are pushed to a local buffer we do not care about it being checked out
    }

    public void donePushingLongs() {
        //Do nothing
    }

    public void flush() {
    	synchronized(_buffer) {
	        for (long number : _buffer) {
	            this.push(number);
	        }
	        _buffer.clear(); // all numbers get added
    	}
    }

    public synchronized Bucket checkoutAnyBucket() {
        for (BucketKey key : _bucketKeys) {
            Bucket bucket = _buckets.get(key);
            if (bucket != null){
                if (!bucket.isCheckedOut() && !bucket.isSorted()) { return bucket.checkout(); }
            }
        }
        return null; //all buckets are checked out
        
    }

    public boolean isEverythingSorted() {
        //System.out.println("Waiting"+_buffer.size());
        if (_buffer.size() > 0) { return false; }

        for (BucketKey key : _bucketKeys) {
            Bucket bucket = _buckets.get(key);
            //System.out.println("Bucket"+bucket.getName()+" "+bucket.isSorted());
            if (!bucket.isSorted() || bucket.isCheckedOut()) { return false; }
        }

        return true;
    }

    private Bucket findBucket(long number) {
        String bucketName = calculateBucketName(number);
        for(BucketKey key : _bucketKeys) {
            if(key.Name.equals(bucketName)) {
                return _buckets.get(key);
            }
        }
        return null;
    }

    private Bucket createBucket(long number) {
        long bucketNumber = calculateBucketNumber(number);
        String bucketName = calculateBucketName(number);
        long lowerBound = bucketNumber * _bucketLength;
        long upperBound = (lowerBound + _bucketLength) - 1;
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

    public long bucketContentSum() {
    	long sum = 0;
    	for (BucketKey key : _bucketKeys) {
            Bucket bucket = _buckets.get(key);
            sum += bucket.size();
        }
    	return sum;
    }

    public ArrayList<Long> buildResult() {
        ArrayList<Long> results = new ArrayList<Long>();
        _bucketKeys.sort(new Comparator<BucketKey>() {
            @Override
            public int compare(BucketKey o1, BucketKey o2) {
                if(o1.LowerBound == o2.LowerBound) { return 0;}
                return o1.LowerBound < o2.LowerBound ? -1 : 1;
            }
        });

        for (BucketKey key : _bucketKeys) {
            Bucket bucket = _buckets.get(key);
            results.addAll(bucket.getItems());
        }

        return  results;
    }
}
