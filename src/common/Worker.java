package common;

public abstract class Worker implements Runnable {
    private Thread _thread;

    public void start() {
        if (_thread == null) {
            _thread = new Thread(this);
            _thread.start();
        }
    }

    public boolean isRunning() {
        return _thread.isAlive();
    }
}
