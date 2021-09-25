package common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class BaseLineSortProcessor implements IHandleLong, ISortFile {
    private ArrayList<Long> _items;

    public BaseLineSortProcessor() {
        _items = new ArrayList<Long>();
    }

    public void sortFile(String inputFilePath, String outputFilePath) throws IOException {
        DataLoader.readInput(inputFilePath, this);
        _items.sort(null);
        LongWriter.writeLongArray(outputFilePath, _items);
    }

    public void push(long number) {
        _items.add(number);
    }

    @Override
    public void donePushingLongs() {
        //Do nothing
    }
}
