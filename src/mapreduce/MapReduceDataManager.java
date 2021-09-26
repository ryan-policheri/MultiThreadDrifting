package mapreduce;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import common.IHandleLong;

public class MapReduceDataManager implements IHandleLong {
    private final int _longCount;
    private final int _chunkCount;
    private final int _delimitEveryXLongs;

    private long _itemsInChunk; //only used for one job (input reading)
    private int _currentChunkNumber; //only used for one job (input reading)
    private ArrayList<Long> _currentLongChunkValues; //only used for one job (input reading)
    private Queue<ArrayList<Long>> _longChunks; //used across multiple jobs (reading job adds chunks, reduce job takes chunks)

    private Queue<ReductionResult> _reducedResults; //used across multiple jobs (multiple reduce jobs and take and push chunks)
    private int _reducedNumbersWeightedCount; //used across multiple jobs (multiple reduce jobs and take and push chunks)

    private Object _longManagmentLock; //processes that involve working with the initial long reading should sync on this lock
    private Object _reducedManagementLock; //processes that involve working with reduced chunks should sync on this lock

    public MapReduceDataManager(int longCount, int chunkCount) {
        _longCount = longCount;
        _chunkCount = chunkCount;
        _delimitEveryXLongs = _longCount / _chunkCount;

        _itemsInChunk = 0;
        _currentChunkNumber = 1;
        _currentLongChunkValues = new ArrayList<Long>(_delimitEveryXLongs + _chunkCount);
        _longChunks = new LinkedList<ArrayList<Long>>();

        _reducedResults = new LinkedList<ReductionResult>();
        _reducedNumbersWeightedCount = 0;

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
            _itemsInChunk = 0;
            _currentChunkNumber++;
            _currentLongChunkValues = new ArrayList<Long>(_delimitEveryXLongs + _chunkCount);
            _currentLongChunkValues.add(number);
            _itemsInChunk++;
        }
    }

    @Override
    public void donePushingLongs() {
        synchronized (_longManagmentLock) {
            _longChunks.add(_currentLongChunkValues);
        }
    }

    public ArrayList<Long> takeLongChunk() {
        synchronized (_longManagmentLock) {
            return _longChunks.poll();
        }
    }

    public void pushReducedChunk(ReductionResult result) {
        synchronized (_reducedManagementLock) {
            _reducedNumbersWeightedCount += result.RecordWeightedLength;
            _reducedResults.add(result);
        }
    }

    public ReductionResultPair tryPopReducedRecordPair() {
        synchronized (_reducedManagementLock) {
            if (_reducedResults.size() >= 2) {
                ReductionResultPair pair = new ReductionResultPair();
                pair.Result1 = _reducedResults.poll();
                pair.Result2 = _reducedResults.poll();
                _reducedNumbersWeightedCount -= pair.calculateWeightedLength();
                return pair;
            } else {
                return null;
            }
        }
    }

    public boolean hasReducedAllData() {
        synchronized (_reducedManagementLock) {
            return (_reducedNumbersWeightedCount == _longCount);
        }
    }

    public ReducedRecord[] getFinalResult() {
        synchronized (_reducedManagementLock) {
            var records = _reducedResults.get(0).Records;
            Arrays.sort(records);
            return records;
        }
    }
}