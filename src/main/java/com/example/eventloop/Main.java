package com.example.eventloop;

public class Main {
    public static void main(String[] args) {
        //1、eventloop 可以指定哪个线程执行哪个任务 JDK线程池不行
        //2、JDK线程池不支持定时任务
        //3、JDK线程池不支持任务优先级,JDK线程池支持伸缩
        DefaultEventLoopGroup defaultEventLoopGroup = new DefaultEventLoopGroup(3);
        for (int i = 0; i < 10; i++) {
            defaultEventLoopGroup.execute(()->{
                System.out.println(Thread.currentThread().getName());
            });
        }
    }
}
