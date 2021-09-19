package mapreduce;

import common.Worker;
import java.util.ArrayList;

public class ReduceWorker extends Worker {
    private MapReduceDataManager _manager;

    private ArrayList<Long> _longs;
    private ReducedRecordPair _pair;

    public ReduceWorker(MapReduceDataManager manager, ArrayList<Long> longs) {
        _manager = manager;
        if (longs == null) throw new IllegalArgumentException("Value null");
        _longs = longs;
    }

    public ReduceWorker(MapReduceDataManager manager, ReducedRecordPair pair){
        _manager = manager;
        if (pair == null) throw new IllegalArgumentException("Values null");
        _pair = pair;
    }

    @Override
    public void run() {
        if (_longs != null) {
            Reducer reducer = new Reducer(_longs);
            ReducedRecord[] reducedRecords = reducer.ReduceAndSort();
            _manager.pushReducedChunk(reducedRecords);
        }
        else { //arrayOne and arrayTwo are not null. Enforced in constructor
            Reducer reducer = new Reducer(_pair.Set1, _pair.Set2);
            ReducedRecord[] reducedRecords = reducer.ReduceAndSort();
            _manager.pushReducedChunk(reducedRecords, _pair.PairId);
        }
    }
}
