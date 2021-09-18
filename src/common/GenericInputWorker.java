package common;

import java.io.IOException;

public class GenericInputWorker extends Worker {
    String _filePath;
    IHandleLong _longHandler;

    public GenericInputWorker(String filePath, IHandleLong longHandler) {
        _filePath = filePath;
        _longHandler = longHandler;
    }

    @Override
    public void run() {
        try {
            DataLoader.readInput(_filePath, _longHandler);
        } catch (IOException ex) {
            ex.printStackTrace();/*What to do?*/
        }
    }
}
