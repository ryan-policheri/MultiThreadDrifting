package mapreduce;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import common.IHandleLong;

public class MapReduceDataManager implements IHandleLong {
    private final int _bytesPerLong = 8;
    private final long _numberOfLongs;
    private final int _chunkCount;
    private final long _delimitEveryXLongs;

    private long _itemsInChunk; //only used for one job (input reading)
    private int _currentChunkNumber; //only used for one job (input reading)
    private ArrayList<Long> _currentLongChunkValues; //only used for one job (input reading)
    private Queue<ArrayList<Long>> _longChunks; //used across multiple jobs (reading job adds chunks, reduce job takes chunks)
    private boolean _donePushingLongs; //used across multiple jobs (input reader job writes to it, main thread reads from it)

    private ArrayList<ReducedRecord[]> _reducedChunks; //used across multiple jobs (multiple reduce jobs and take and push chunks)
    private int _pairIdCounter; //used across multiple jobs (multiple reduce jobs and take and push chunks)
    private ArrayList<Integer> _reducedPairsInProgress; //used across multiple jobs (multiple reduce jobs and take and push chunks)

    private Object _longManagmentLock; //processes that involve working with the initial long reading should sync on this lock
    private Object _reducedManagementLock; //processes that involve working with reduced chunks should sync on this lock

    public MapReduceDataManager(long fileLengthInBytes, int chunkCount) {
        _numberOfLongs = (fileLengthInBytes / _bytesPerLong);
        _chunkCount = chunkCount;
        _delimitEveryXLongs = _numberOfLongs / _chunkCount;

        _itemsInChunk = 0;
        _currentChunkNumber = 1;
        _currentLongChunkValues = new ArrayList<Long>();
        _longChunks = new LinkedList<ArrayList<Long>>();
        _donePushingLongs = false;

        _reducedChunks = new ArrayList<>();
        _pairIdCounter = 0;
        _reducedPairsInProgress = new ArrayList<>();

        _longManagmentLock = new Object();
        _reducedManagementLock = new Object();
    }

    @Override
    public void push(long number) {
        if ((_itemsInChunk < _delimitEveryXLongs) || _currentChunkNumber == _chunkCount) { //If there is overflow throw it all in the last chunk
            _currentLongChunkValues.add(number);
            _itemsInChunk++;
        } else {
            synchronized (_longManagmentLock) {
                _longChunks.add(_currentLongChunkValues);
            }
            _currentLongChunkValues = new ArrayList<Long>();

            _itemsInChunk = 0;
            _currentChunkNumber++;
            _currentLongChunkValues = new ArrayList<Long>();
            _currentLongChunkValues.add(number);
            _itemsInChunk++;
        }
    }

    @Override
    public void donePushingLongs() {
        synchronized (_longManagmentLock) {
            _donePushingLongs = true;
            _longChunks.add(_currentLongChunkValues);
        }
    }

    public ArrayList<Long> takeLongChunk() {
        synchronized (_longManagmentLock) {
            return _longChunks.poll();
        }
    }

    public void pushReducedChunk(ReducedRecord[] records) {
        synchronized (_reducedManagementLock) {
            _reducedChunks.add(records);
        }
    }

    public void pushReducedChunk(ReducedRecord[] records, int pairId) {
        synchronized (_reducedManagementLock) {
            _reducedChunks.add(records);
            _reducedPairsInProgress.removeIf(x -> x == pairId);
        }
    }

    public boolean hasReducedAllData() {
        synchronized (_longManagmentLock) {
            if (_donePushingLongs == false || _longChunks.size() != 0)  { return false; }
        }
        synchronized (_reducedManagementLock) {
            if (_reducedChunks.size() != 1 || _reducedPairsInProgress.size() != 0 || calculateWieghtedCount() != _numberOfLongs) { return false; }
        }
        return true;
    }

    public ReducedRecordPair tryPopReducedRecordPair() {
        synchronized (_reducedManagementLock) {
            if (_reducedChunks.size() >= 2) {
                ReducedRecordPair pair = new ReducedRecordPair();
                pair.PairId = ++_pairIdCounter;
                _reducedPairsInProgress.add(_pairIdCounter);
                pair.Set1 = _reducedChunks.remove(0);
                pair.Set2 = _reducedChunks.remove(0);
                return pair;
            } else {
                return null;
            }
        }
    }

    public ReducedRecord[] getFinalResult() {
        synchronized (_reducedManagementLock) {
            int count = calculateWieghtedCount();
            return _reducedChunks.get(0);
        }
    }

    private int calculateWieghtedCount() {
        int count = 0;
        for (ReducedRecord record : _reducedChunks.get(0)) {
            count = count + record.Count;
        }
        return count;
    }
}
