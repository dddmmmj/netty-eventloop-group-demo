package com.example.eventloop;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultEventLoopGroup implements EventLoopGroup {
    private final EventLoop[] children;
    private final AtomicInteger index = new AtomicInteger(0);

    public DefaultEventLoopGroup(int threads) {
        if (threads <= 0) threads = 1;
        this.children = new EventLoop[threads];
        for (int i = 0; i < this.children.length; i++) {
            this.children[i] = new DefaultEventLoop();
        }
    }

    public void execute(Runnable runnable) {
        next().execute(runnable);
    }

    public void schedule(Runnable runnable, long delay, TimeUnit unit) {
        next().schedule(runnable, delay, unit);
    }

    public void scheduleAtFixed(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
        next().scheduleAtFixed(runnable, initialDelay, period, unit);
    }

    private EventLoop next() {
        return children[index.incrementAndGet() % children.length];
    }
}
