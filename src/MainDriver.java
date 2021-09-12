import java.io.IOException;

public class MainDriver {
    public static void main(String[] args) {
        String inputFile = args[0]; //This file would come generated. But generating it below for convenience
        String outputFile = args[1];

        try { inputFile = GenerateInputDriver.GenerateInputFile(inputFile, 1000000); }
        catch (IOException ex) { System.out.println("Error generating input file"); return; }



    }
}
