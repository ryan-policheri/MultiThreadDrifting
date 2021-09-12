import java.util.ArrayList;

public class Bucket {
    private String _name;
    private long _lowerBound;
    private long _upperBound;
    private boolean _isSorted;
    private ArrayList<Long> _items;

    public Bucket(String name, long lowerBound, long upperBound) {
        _name = name;
        _lowerBound = lowerBound;
        _upperBound = upperBound;
        _isSorted = true;
        _items = new ArrayList<Long>();
    }

    public void addItem(long item) {
        _items.add(item);
        _isSorted = false;
    }

    public void sort() {
        _items.sort(null);
        _isSorted = true;
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
}