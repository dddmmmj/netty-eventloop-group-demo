package com.example.eventloop;

import java.util.concurrent.TimeUnit;

public interface EventLoopGroup {
    void execute(Runnable runnable);

    void schedule(Runnable runnable, long delay, TimeUnit unit);

    void scheduleAtFixed(Runnable runnable, long initialDelay, long period, TimeUnit unit);
}
