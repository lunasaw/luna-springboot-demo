package com.xkcoding.async.task;

import com.xkcoding.async.MyLog;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池满之后的处理策略类
 * @DESC 
 * @author guchuang
 *
 */
@Slf4j
public class RejectedPolicy implements RejectedExecutionHandler {
    public RejectedPolicy() { }

    /**
     * 向线程池中添加线程被拒绝时会调用这个方法。一般拒绝是因为线程池满了
     *
     * @param r 被拒绝的任务
     * @param e 拒绝这个任务的线程池
     */
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        log.info("one thread is rejected, i will deal it");
        if (!e.isShutdown()) {
            r.run();
        }
    }
}
