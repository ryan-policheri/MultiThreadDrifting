package mapreduce;

import java.util.ArrayList;

public class ReduceJob implements Runnable {
    private MapReduceDataManager _manager;

    private ArrayList<Long> _longs;
    private ReductionResultPair _pair;

    public ReduceJob(MapReduceDataManager manager, ArrayList<Long> longs) {
        _manager = manager;
        _longs = longs;
    }

    public ReduceJob(MapReduceDataManager manager, ReductionResultPair pair){
        _manager = manager;
        _pair = pair;
    }

    @Override
    public void run() {
        if (_longs != null) {
            Reducer reducer = new Reducer(_longs);
            ReductionResult result = reducer.ReduceAndSort();
            _manager.pushReducedChunk(result);
        }
        else { //pair not null
            Reducer reducer = new Reducer(_pair);
            ReductionResult result = reducer.ReduceAndSort();
            _manager.pushReducedChunk(result);
        }
    }
}
