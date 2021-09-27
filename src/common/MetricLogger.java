package common;

import java.util.ArrayList;

public class MetricLogger {
    private ArrayList<MetricLogItem> _metricLogs;

    public MetricLogger() {
        _metricLogs = new ArrayList<MetricLogItem>();
    }

    public MetricLogItem startRecording(String actionDescription) {
        MetricLogItem item = new MetricLogItem(actionDescription);
        item.start();
        return item;
    }

    public void stopRecording(MetricLogItem logItem) {
        logItem.stop();
        _metricLogs.add(logItem);
    }

    public void writeToConsole() {
        for(MetricLogItem item : _metricLogs) {
            System.out.println(item.toString());
        }
    }

    public void writeActionExpense(String action) {
        long expenseInNanoSeconds = 0;
        long longestTimeInNanoSeconds = Long.MIN_VALUE;
        long shortestTimeInNanoSeconds = Long.MAX_VALUE;
        int actionCount = 0;

        for(MetricLogItem metricLogItem : _metricLogs) {
            if (metricLogItem.get_actionDescription() == action) {
                long duration = metricLogItem.get_durationNanoSeconds();
                expenseInNanoSeconds += metricLogItem.get_durationNanoSeconds();
                if (duration < shortestTimeInNanoSeconds) shortestTimeInNanoSeconds = duration;
                if (duration > longestTimeInNanoSeconds) longestTimeInNanoSeconds = duration;
                actionCount++;
            }
        }
        long averageExpenseNanoseconds = expenseInNanoSeconds / actionCount;
        System.out.println("Action: " + action + " Total Expense: " + expenseInNanoSeconds + " nanoseconds Longest Expense:" + longestTimeInNanoSeconds + " nanoseconds " +
                " Shortest Expense: " + shortestTimeInNanoSeconds + " nanoseconds Average Expense: " + averageExpenseNanoseconds + " nanoseconds");
    }
}


