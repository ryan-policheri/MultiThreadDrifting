package common;

import java.io.IOException;

public interface ISortFile {
    void sortFile(String inputFilePath, String outputFilePath) throws IOException;
}
