package bucketsort1;

import java.util.ArrayList;

public class Bucket {
    private String _name;
    private long _lowerBound;
    private long _upperBound;
    private boolean _isSorted;
    private boolean _isCheckedOut;
    private ArrayList<Long> _items;
    private ArrayList<Long> _buffer;

    public Bucket(String name, long lowerBound, long upperBound) {
        _name = name;
        _lowerBound = lowerBound;
        _upperBound = upperBound;
        _isCheckedOut = false;
        _isSorted = false;
        _items = new ArrayList<Long>();
        _buffer = new ArrayList<Long>();
    }

    public void addItem(long item) {
    	synchronized(_buffer) {
	        _buffer.add(item);
	        _isSorted = false;
    	}
    }
    public void addBuffer() {
    	synchronized(_buffer) {
	    	_items.addAll(_buffer);
	    	_buffer.clear();
	        _isSorted = false;
    	}
    }

    public void sort() {
        _isSorted = true;
        synchronized(_items) {
        	_items.sort(null);
        }
    }

    public boolean isSorted() {
        return _isSorted && _buffer.size() == 0;
        // in theory these should always be the same
    }

    public boolean itemBelongs(long item) {
        if (item >= _lowerBound && item <= _upperBound) {
            return true;
        }
        else { return false; }
    }

    public String getName() {
        return _name;
    }
    public int size() {
        return _items.size();
    }

    public Bucket checkout() {
        _isCheckedOut = true;
        return this;
    }

    public boolean isCheckedOut() {
        return _isCheckedOut;
    }

    public void checkin() {
        _isCheckedOut = false;
    }
}