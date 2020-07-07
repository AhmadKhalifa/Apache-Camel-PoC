package com.rtt.collector.collectorpoc.camel.utils;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentTasksMonitor {

    private int totalTasks;
    private final AtomicBoolean isProcessing = new AtomicBoolean(false);
    private final AtomicInteger remainingTasksCount = new AtomicInteger(0);
    private final Callback callback;

    public ConcurrentTasksMonitor(Callback callback) {
        this.callback = callback;
    }

    public void notifyTasksStarted(int tasksCount) {
        if (!isProcessing.get()) {
            totalTasks = tasksCount;
            isProcessing.set(true);
            remainingTasksCount.set(tasksCount);
            if (Objects.nonNull(callback)) {
                callback.onStart(totalTasks);
            }
        }
    }

    public boolean isProcessing() {
        return isProcessing.get();
    }

    public int getRemainingTasksCount() {
        return remainingTasksCount.get();
    }

    public synchronized void notifyTaskEnded() {
        remainingTasksCount.decrementAndGet();
        if (Objects.nonNull(callback)) {
            callback.onProgress(totalTasks - getRemainingTasksCount(), totalTasks);
        }
        if (getRemainingTasksCount() == 0) {
            isProcessing.set(false);
            if (Objects.nonNull(callback)) {
                callback.onFinish(totalTasks);
            }
        }
    }

    public interface Callback {

        void onStart(int totalTasks);

        void onProgress(int finishedTasks, int totalTasks);

        void onFinish(int totalTasks);
    }
}
