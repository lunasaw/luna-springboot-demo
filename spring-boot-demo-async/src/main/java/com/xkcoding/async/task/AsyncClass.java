package com.xkcoding.async.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 将一个类声明为异步类，那么这个类对外暴露的方法全部成为异步方法。
 * 与异步方法的区别是这里的注解是加到类上，异步方法的注解是加到方法上。仅此而已
 * 
 * @DESC
 * @author guchuang
 *
 */
@Async
@Service
@Slf4j
public class AsyncClass {
    public AsyncClass() {
        log.info("-------------------------init AsyncClass--------------------");
    }

    volatile int index = 0;

    public void foo() {
        log.info("asyncclass foo, index:" + index);
    }

    public void foo(int i) {
        this.index = i;
        log.info("asyncclass foo, index:" + i);
    }

    public void bar(int i) {
        this.index = i;
        log.info("asyncclass bar, index:" + i);
    }
}
