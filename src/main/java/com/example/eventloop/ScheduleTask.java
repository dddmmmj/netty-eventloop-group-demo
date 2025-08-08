package com.example.eventloop;

public class ScheduleTask implements Runnable ,Comparable<ScheduleTask>{
    private final Runnable runnable;
    private long delay;
    private long period;

    public ScheduleTask(Runnable runnable, long delay, long period) {
        this.runnable = runnable;
        this.delay = delay;
        this.period = period;
    }

    public void run() {
        try {
            runnable.run();
        } catch (Exception ignore) {
        } finally {
            //周期执行的定时任务再次加入队列
            if (period >= 0) {
                delay = System.currentTimeMillis() + period;
            }
        }
    }

    public int compareTo(ScheduleTask o) {
        return Long.compare(o.delay,this.delay);
    }

    public long getDelay() {
        return delay;
    }
}
