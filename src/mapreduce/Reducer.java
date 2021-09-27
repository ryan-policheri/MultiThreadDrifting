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

    public ReductionResult reduceAndSort() {
        if (_longs != null) { return performInitialReduction(_longs); }
        else { return performReduceAndMultiplex(_pair); }//pair not null
    }

    private ReductionResult performInitialReduction(ArrayList<Long> longs) {
        int weigthedLength = longs.size();
        Hashtable<Long, ReducedRecord> hashtable = new Hashtable<Long, ReducedRecord>();

        for (Long number : longs) {
            placeInHashTable(hashtable, number, 1);
        }

        ReducedRecord[] recordArray = convertHashTableToArray(hashtable);
        Arrays.sort(recordArray); //For initial reduction we want to sort, so we can multiplex when reducing pairs
        ReductionResult result = new ReductionResult();
        result.Records = recordArray;
        result.RecordWeightedLength = weigthedLength;
        return result;
    }

    private ReductionResult performReduceAndMultiplex(ReductionResultPair pair) {
        int maxLength = pair.Result1.recordCount() + pair.Result2.recordCount();
        ArrayList<ReducedRecord> records = new ArrayList<ReducedRecord>(maxLength);

        ReducedRecord[] result1 = pair.Result1.Records;
        ReducedRecord[] result2 = pair.Result2.Records;
        int result1Index = 0;
        int result2Index = 0;

        int lastUsedIndex = -1;
        int weigthedLength = 0;

        for(int i = 0; i < maxLength; i++) {
            if (result1Index >= result1.length) result1Index = -1;
            if (result2Index >= result2.length) result2Index = -1;

            if (result1Index != -1 && result2Index != -1){
                ReducedRecord result1Record = result1[result1Index];
                ReducedRecord result2Record = result2[result2Index];
                if (result1Record.Number == result2Record.Number) {
                    int combinedCount = result1Record.Count + result2Record.Count;
                    records.add(i, new ReducedRecord(result1Record.Number, combinedCount));
                    weigthedLength += combinedCount;
                    result1Index++;
                    result2Index++;
                }
                else if (result1Record.Number < result2Record.Number) {
                    records.add(i, result1Record);
                    weigthedLength += result1Record.Count;
                    result1Index++;
                }
                else {
                    records.add(i, result2Record);
                    weigthedLength += result2Record.Count;
                    result2Index++;
                }
            }
            else if (result1Index != -1 && result2Index == -1) {
                records.add(i, result1[result1Index]);
                weigthedLength += result1[result1Index].Count;
                result1Index++;
            }
            else if (result1Index == -1 && result2Index != -1) {
                records.add(i, result2[result2Index]);
                weigthedLength += result1[result2Index].Count;
                result2Index++;
            }
            else { lastUsedIndex = i - 1; break; }
        }


        ReducedRecord[] reducedAndSortedRecords = new ReducedRecord[lastUsedIndex + 1];
        records.toArray(reducedAndSortedRecords);
        ReductionResult result = new ReductionResult();
        result.Records = reducedAndSortedRecords;
        result.RecordWeightedLength = weigthedLength;

        return  result;
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