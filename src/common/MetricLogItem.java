package common;

public class MetricLogItem {
    private long _startTimeNanoSeconds;
    private long _endTimeNanoSeconds;
    private long _durationNanoSeconds;

    private String _actionDescription;

    public MetricLogItem(String action){
        _actionDescription = action;
    }

    public void start() {
        _startTimeNanoSeconds = System.nanoTime();
    }

    public void stop() {
        _endTimeNanoSeconds = System.nanoTime();
        _durationNanoSeconds  = _endTimeNanoSeconds - _startTimeNanoSeconds;
    }

    public String get_actionDescription() {
        return _actionDescription;
    }

    public long get_durationNanoSeconds() {
        return _durationNanoSeconds;
    }


    @Override
    public String toString() {
        String str = "The action " + _actionDescription + " took " + _durationNanoSeconds + " to process";
        return str;
    }
}
