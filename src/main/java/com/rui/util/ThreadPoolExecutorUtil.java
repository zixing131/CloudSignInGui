package com.rui.util;

import cn.hutool.core.lang.Console;

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
    public  ExecutorService threadPool=new ThreadPoolExecutor(0,
            Integer.MAX_VALUE,
            1L,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(1),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy());
    public  AtomicInteger atomicInteger=new AtomicInteger();

    public void execute(Runnable runnable,String name){
        atomicInteger.incrementAndGet();
        Console.log(atomicInteger.get()+"\t"+name);
        threadPool.execute(runnable);
    }
    public void execute(Runnable runnable){
        threadPool.execute(runnable);
    }
}
