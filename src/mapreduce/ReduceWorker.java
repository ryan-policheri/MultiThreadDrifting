package mapreduce;

import common.Worker;
import java.io.InvalidObjectException;
import java.util.ArrayList;

public class ReduceWorker extends Worker {
    ArrayList<Long> _longs;

    ReducedRecord[] _arrayOne;
    ReducedRecord[] _arrayTwo;

    ReducedRecord[] _reducedRecords;

    public ReduceWorker(ArrayList<Long> longs) {
        if (longs == null) throw new IllegalArgumentException("Value null");
        _longs = longs;
    }

    public ReduceWorker(ReducedRecord[] arrayOne, ReducedRecord[] arrayTwo){
        if (arrayOne == null || arrayTwo == null) throw new IllegalArgumentException("Values null");
        _arrayOne = arrayOne;
        _arrayTwo = arrayTwo;
    }

    @Override
    public void run() {
        if (_longs != null) {
            Reducer reducer = new Reducer(_longs);
            _reducedRecords = reducer.ReduceAndSort();
        }
        else { //arrayOne and arrayTwo are not null. Enforced in constructor
            Reducer reducer = new Reducer(_arrayOne, _arrayTwo);
            _reducedRecords = reducer.ReduceAndSort();
        }
    }

    public ReducedRecord[] getReducedRecords() throws InvalidObjectException { //Should only be called when thread is finished
        if (this.isRunning()) throw new InvalidObjectException("Thread is running");
        return _reducedRecords;
    }
}
