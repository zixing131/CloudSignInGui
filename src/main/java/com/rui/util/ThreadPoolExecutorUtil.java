package com.rui.util;

import cn.hutool.core.lang.Console;
import cn.hutool.core.thread.ThreadUtil;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 所属项目:CloudSignInGui
 *
 * @author rui10038 邮箱：2450782689@qq.com
 * @version 1.0
 * //                .-~~~~~~~~~-._       _.-~~~~~~~~~-.
 * //            __.'              ~.   .~              `.__
 * //          .'//    JAVA无涯      \./     回头是岸     \\`.
 * //        .'//                     |                     \\`.
 * //      .'// .-~"""""""~~~~-._     |     _,-~~~~"""""""~-. \\`.
 * //    .'//.-"                 `-.  |  .-'                 "-.\\`.
 * //  .'//______.============-..   \ | /   ..-============.______\\`.
 * //.'______________________________\|/______________________________`.
 * @date 2020/2/26 -下午 7:45
 **/
public class ThreadPoolExecutorUtil {
    public ExecutorService threadPool = new ThreadPoolExecutor(0,
            Integer.MAX_VALUE,
            1L,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(1),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy());
    public AtomicInteger atomicInteger = new AtomicInteger();

    public void updateIncrement() {
        atomicInteger.incrementAndGet();
    }

    public void execute(Runnable runnable) {
        threadPool.execute(runnable);
    }

    public static void main(String[] args) {
        ThreadPoolExecutorUtil threadPoolExecutorUtil = new ThreadPoolExecutorUtil();
        for (int i = 0; i < 5000; i++) {
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    ThreadUtil.sleep(1000);
                   Console.log("测试");
                }
            };
            threadPoolExecutorUtil.threadPool.execute(task);
        }
        threadPoolExecutorUtil.threadPool.shutdown();
        try {
            threadPoolExecutorUtil.threadPool.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
