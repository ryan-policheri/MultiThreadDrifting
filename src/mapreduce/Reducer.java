package mapreduce;

import java.util.*;

public class Reducer {
    private ArrayList<Long> _longs;
    private ReductionResultPair _pair;

    public Reducer(ArrayList<Long> longs){
        if (longs == null) throw new IllegalArgumentException("Value null");
        _longs = longs;
    }

    public Reducer(ReductionResultPair pair){
        if (pair == null) throw new IllegalArgumentException("Value null");
        _pair = pair;
    }

    public ReductionResult Reduce() {
        int weigthedLength = 0;
        Hashtable<Long, ReducedRecord> hashtable = new Hashtable<Long, ReducedRecord>();

        if (_longs != null) {
            weigthedLength = _longs.size();
            for (Long number : _longs) {
                placeInHashTable(hashtable, number, 1);
            }
        }
        else { //pair not null
            weigthedLength = _pair.calculateWeightedLength();
            for (ReducedRecord record : _pair.Result1.Records) {
                hashtable.put(record.Number, record);
            }
            for (ReducedRecord record : _pair.Result2.Records) {
                placeInHashTable(hashtable, record.Number, record.Count);
            }
        }

        ReducedRecord[] recordArray = convertHashTableToArray(hashtable);
        ReductionResult result = new ReductionResult();
        result.Records = recordArray;
        result.RecordWeightedLength = weigthedLength;
        return result;
    }

    public ReductionResult ReduceAndSort() {
        ReductionResult result = Reduce();
        Arrays.sort(result.Records);
        return result;
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