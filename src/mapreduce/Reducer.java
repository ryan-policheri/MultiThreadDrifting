package mapreduce;

import java.util.*;

public class Reducer {
    private ArrayList<Long> _longs;

    ReducedRecord[] _arrayOne;
    ReducedRecord[] _arrayTwo;

    public Reducer(ArrayList<Long> longs){
        if (longs == null) throw new IllegalArgumentException("Value null");
        _longs = longs;
    }

    public Reducer(ReducedRecord[] arrayOne, ReducedRecord[] arrayTwo){
        if (arrayOne == null || arrayTwo == null) throw new IllegalArgumentException("Values null");
        _arrayOne = arrayOne;
        _arrayTwo = arrayTwo;
    }


    public ReducedRecord[] Reduce() {
        Hashtable<Long, ReducedRecord> hashtable = new Hashtable<Long, ReducedRecord>();

        if (_longs != null) {
            for (Long number : _longs) {
                placeInHashTable(hashtable, number, 1);
            }
        }
        else { //arrayOne and arrayTwo not null. Enforced in constructor
            for (ReducedRecord record : _arrayOne) {
                hashtable.put(record.Number, record);
            }
            for (ReducedRecord record : _arrayTwo) {
                placeInHashTable(hashtable, record.Number, record.Count);
            }
        }

        ReducedRecord[] recordArray = convertHashTableToArray(hashtable);
        return recordArray;
    }

    public ReducedRecord[] ReduceAndSort() {
        ReducedRecord[] records = Reduce();
        Arrays.sort(records);
        return records;
    }

    private void placeInHashTable(Hashtable<Long, ReducedRecord> hashtable, Long number, int incrementBy) {
        if (hashtable.containsKey(number)) {
            ReducedRecord record = hashtable.get(number);
            record.Count = record.Count + incrementBy;
        } else {
            ReducedRecord record = new ReducedRecord(number, incrementBy);
            hashtable.put(number, record);
        }
    }

    private ReducedRecord[] convertHashTableToArray(Hashtable<Long, ReducedRecord> hashtable) {
        Collection<ReducedRecord> recordCollection = hashtable.values();
        ReducedRecord[] recordArray = new ReducedRecord[recordCollection.size()];
        recordCollection.toArray(recordArray);
        return recordArray;
    }
}
