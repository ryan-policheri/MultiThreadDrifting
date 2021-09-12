import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

public class GenerateInputDriver {
    public static void main(String[] args) throws java.io.IOException {
        String directory = args[0];
        long inputSize = Long.parseLong(args[1]);
        String file = GenerateInputFile(directory, inputSize);
    }

    public static String GenerateInputFile(String directory, long inputSize) throws java.io.IOException  {
        String fileName = "H1_Input_Size_" + inputSize + ".txt";
        Path fullPath = Paths.get(directory, fileName);
        File file = new File(fullPath.toString());
        file.delete();

        FileWriter writer = new FileWriter(file, true);

        Random r = new Random();
        for (int i = 0; i < inputSize; i++) {
            int rand = r.nextInt(100);
            String randAsStr = String.valueOf(rand);
            writer.write(randAsStr);
            if(i != (inputSize - 1)) { writer.write(","); }
            writer.flush();
        }

        return file.getAbsolutePath();
    }
}
