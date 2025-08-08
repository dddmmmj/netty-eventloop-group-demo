package com.example.eventloop;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultEventLoop implements EventLoop {
    private final BlockingQueue<Runnable> taskQueue = new ArrayBlockingQueue<Runnable>(1024);
    private final PriorityBlockingQueue<ScheduleTask> scheduleTasks = new PriorityBlockingQueue<ScheduleTask>(1024);
    private final static AtomicInteger threadNums = new AtomicInteger(0);
    private final Thread thread;
    private static final Runnable WEAK_UP = () -> {
    };

    public DefaultEventLoop() {
        this.thread = new DefaultEventLoopThread("Default-Event-Loop-Thread" + threadNums.incrementAndGet());
        this.thread.start();
    }

    public void execute(Runnable runnable) {
        if (!taskQueue.offer(runnable)) {
            throw new RuntimeException("任务队列已满");
        }
    }

    public void schedule(Runnable runnable, long delay, TimeUnit unit) {
        scheduleTasks.offer(new ScheduleTask(runnable, unit.toMillis(delay) + System.currentTimeMillis(), -1));
        //唤醒
        taskQueue.offer(WEAK_UP);
    }

    public void scheduleAtFixed(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
        scheduleTasks.offer(new ScheduleTask(runnable, unit.toMillis(initialDelay) + System.currentTimeMillis(), unit.toMillis(period)));
        //唤醒
        taskQueue.offer(WEAK_UP);
    }

    private Runnable getTask() {
        ScheduleTask scheduleTask = scheduleTasks.peek();
        if (scheduleTask == null) {
            // 没有定时任务，直接阻塞等待普通任务
            try {
                Runnable task = taskQueue.take();
                if (task == WEAK_UP) return null;
                return task;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }

        long executeLeftTime = scheduleTask.getDelay() - System.currentTimeMillis();

        if (executeLeftTime <= 0) {
            // 定时任务到点了，非阻塞取出执行,但是和peek的不一定是同一个
            return scheduleTasks.poll();
        }

        // 定时任务还没到时间，先等待普通任务，等待时间为定时任务剩余时间
        try {
            Runnable task = taskQueue.poll(executeLeftTime, TimeUnit.MILLISECONDS);
            if (task == WEAK_UP) return null;
            return task;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }


    class DefaultEventLoopThread extends Thread {
        public DefaultEventLoopThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            for (; ; ) {
                try {
                    Runnable task = getTask();
                    if (task != null) {
                        task.run();
                    }
                } catch (Exception ignore) {
                }
            }
        }
    }
}
