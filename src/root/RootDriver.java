package root;

import common.BinaryFileToTextFile;
import common.DataSetGenerator;

import java.io.IOException;

public class RootDriver {
    public static void main(String[] args) throws IOException { //Main driver that can all our solutions and serve as a utility for generating files
        if (args[0].equals("--GenerateFile")) generateFile(args); //user called the utility for generating files
    }

    private static void generateFile(String[] args) throws IOException {
        String path = args[1];
        String fileLength = args[2];
        String generationType = args[3];

        path = path.replace("\"", "");
        int inputSize = Integer.parseInt(fileLength);
        generationType = generationType.toUpperCase();

        DataSetGenerator.GenerateInputFile(path, inputSize, generationType);
        BinaryFileToTextFile.ConvertBinaryLongsToTextLongs(path);
    }
}
