package mapreduce;

import java.util.*;

public class Reducer {
    private ArrayList<Long> _longs;

    public Reducer(ArrayList<Long> longs){
        _longs = longs;
    }

    public ReducedRecord[] Reduce() {
        Hashtable<Long, ReducedRecord> hashtable = new Hashtable<Long, ReducedRecord>();
        for(Long number : _longs){
            if (hashtable.containsKey(number)) {
                ReducedRecord record = hashtable.get(number);
                record.Count++;
            }
            else {
                ReducedRecord record = new ReducedRecord(number);
                hashtable.put(number, record);
            }
        }

        Collection<ReducedRecord> recordCollection = hashtable.values();
        ReducedRecord[] recordArray = new ReducedRecord[recordCollection.size()];
        recordCollection.toArray(recordArray);
        return recordArray;
    }

    public ReducedRecord[] ReduceAndSort() {
        ReducedRecord[] records = Reduce();
        Arrays.sort(records);
        return records;
    }
}
