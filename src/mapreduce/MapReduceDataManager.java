package mapreduce;

import common.IHandleLong;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class MapReduceDataManager implements IHandleLong {
    private boolean _donePushingLongs = false;

    private final int _bytesPerLong = 8;

    private final int _chunkCount;
    private final long _delimitEveryXLongs;

    private long _itemsInChunk;
    private int _currentChunkNumber;
    private ArrayList<Long> _currentChunkValues;

    private Queue<ArrayList<Long>> _chunks;

    public MapReduceDataManager(long fileLengthInBytes, int chunkCount) {
        long amountOfLongs = (fileLengthInBytes / _bytesPerLong);

        _chunkCount = chunkCount;
        _delimitEveryXLongs = amountOfLongs / _chunkCount;

        _itemsInChunk = 0;
        _currentChunkNumber = 1;
        _currentChunkValues = new ArrayList<Long>();

        _chunks = new LinkedList<ArrayList<Long>>();
    }

    @Override
    public void push(long number) {
        if((_itemsInChunk < _delimitEveryXLongs) || _currentChunkNumber == _chunkCount) { //If there is overflow throw it all in the last chunk
            _currentChunkValues.add(number);
            _itemsInChunk++;
        } else {
            addToChunks(_currentChunkValues);
            _currentChunkValues = new ArrayList<Long>();

            _itemsInChunk = 0;
            _currentChunkNumber++;
            _currentChunkValues = new ArrayList<Long>();
            _currentChunkValues.add(number);
            _itemsInChunk++;
        }
    }

    @Override
    public synchronized void donePushingLongs() {
        _donePushingLongs = true;
        _chunks.add(_currentChunkValues);
    }

    public synchronized ArrayList<Long> takeChunk() {
        return _chunks.poll();
    }

    public synchronized boolean isDonePusingLongs() {
        return _donePushingLongs;
    }

    private synchronized void addToChunks(ArrayList<Long> chunk) {
        _chunks.add(chunk);
    }
}
