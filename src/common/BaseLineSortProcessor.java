package common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class BaseLineSortProcessor implements IHandleLong {
    private ArrayList<Long> _items;

    public BaseLineSortProcessor() {
        _items = new ArrayList<Long>();
    }

    public String sortFile(String inputFilePath) throws IOException {
        DataLoader.readInput(inputFilePath, this);
        _items.sort(null);
        String outputFilePath = calculateOutputFilePath(inputFilePath);
        LongWriter.writeLongArray(outputFilePath, _items);
        return outputFilePath;
    }

    public void push(long number) {
        _items.add(number);
    }

    private String calculateOutputFilePath(String inputFilePath) {
        File file = new File(inputFilePath);
        String directory = file.getParent();
        String name = file.getName();
        name = "BaseLineSortProcessor_Sorted_" + name;
        String filePath = Paths.get(directory, name).toString();
        return filePath;
    }
}
