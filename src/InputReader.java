import java.io.*;
import java.nio.charset.Charset;

public class InputReader implements Runnable {
    private String _filePath;
    private char _delimiter;
    private BucketManager _sharedBucketManager;
    private Thread _thread;

    private FileInputStream _fileStream;

    public InputReader(String filePath, char delimeter, BucketManager bucketManager) {
        _filePath = filePath;
        _delimiter = delimeter;
        _sharedBucketManager = bucketManager;
    }


    @Override
    public void run() {
        InputStreamReader inputStreamReader = new InputStreamReader(_fileStream, Charset.forName("UTF-8"));
        BufferedReader reader = new BufferedReader(inputStreamReader);

        try {
            int c;
            String workingItem = "";
            while ((c = reader.read()) != -1) {
                char character = (char) c;
                if(character != _delimiter) {
                    workingItem += character;
                }
                else {
                    _sharedBucketManager.push(Long.parseLong(workingItem));
                    workingItem = "";
                }
            }
        } catch (IOException ex) { /*What to do?*/ }
    }

    public void start() throws FileNotFoundException {
        if (_thread == null) {
            File file = new File(_filePath);
            _fileStream = new FileInputStream(file);
            _thread = new Thread(this);
            _thread.start();
        }
    }
}
