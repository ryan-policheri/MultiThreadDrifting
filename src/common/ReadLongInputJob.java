package common;

import java.io.IOException;

public class ReadLongInputJob implements Runnable {
    private final String _filePath;
    private final IHandleLong _longHandler;

    public ReadLongInputJob(String filePath, IHandleLong longHandler) {
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