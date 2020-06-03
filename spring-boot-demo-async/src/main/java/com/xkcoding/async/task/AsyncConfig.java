package com.xkcoding.async.task;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;

/**
 * @Async异步方法线程池配置，默认不使用线程池，使用SimpleAsyncTaskExecutor（一个线程执行器，每个任务都会新建线程去执行）
 * 这里实现了接口AsyncConfigurer，并覆写了其内的方法，这样@Async默认的运行机制发生变化（使用了线程池，设置了线程运行过程的异常处理函数）
 * 备注：
 * 这里只是展示写法，要达到这个目的，可以不实现这个接口，具体见下面的方法
 * @DESC
 * @author guchuang
 *
 */
@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig implements AsyncConfigurer {

    private static ExecutorService threadPool               = new ThreadPoolExecutor(5, 5,
        60L, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<>(3), new MyThreadFactory("common1"));

    private static ExecutorService threadPoolWithRejectDeal = new ThreadPoolExecutor(5, 5,
        60L, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<>(3), new MyThreadFactory("common2"), new RejectedPolicy());

    /**
     * 这个实例声明的TaskExecutor会成为@Async方法运行的默认线程执行器
     * 
     * @Bean 使这个实例完全被spring接管
     */
    @Bean
    @Override
    public TaskExecutor getAsyncExecutor() {
        return new ConcurrentTaskExecutor(Executors.newFixedThreadPool(5, new MyThreadFactory("async")));
    }

    /**
     * 定义@Async方法默认的异常处理机制（只对void型异步返回方法有效，Future返回值类型的异常会抛给调用者）
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (e, method, objects) -> log.error("Method:" + method + ", exception:" + e.getMessage());
    }

    /**
     * 如果不覆写AsyncConfigurer的话，这个方法暴露bean会被当做@Async的默认线程池。
     * 注意必须是这个方法名（也就是bean name， 或者显示指定bean name @Qualifier("taskExecutor")），返回类型可以是Executor或者TaskExecutor
     * 如果没有配置的Executor，则默认使用SimpleAsyncTaskExecutor
     * 备注： 这种方式声明的bean，方法名就是bean name
     * 
     * @return
     */
    @Bean
    public Executor taskExecutor() {
        return new ConcurrentTaskExecutor(Executors.newFixedThreadPool(5, new MyThreadFactory("async0")));
    }

    /**
     * 定义其它的TaskExecutor，声明@Async方法的时候可以指定TaskExecutor，达到切换底层的目的
     * 
     * @return
     */
    @Bean
    public TaskExecutor async1() {
        // 线程数,线程名
        return new ConcurrentTaskExecutor(Executors.newFixedThreadPool(2, new MyThreadFactory("async1")));
    }

    /**
     * 没有设置拒绝策略
     * 
     * @return
     */
    @Bean
    @Qualifier("async2")
    public TaskExecutor myAsyncExecutor2() {
        return new ConcurrentTaskExecutor(threadPool);
    }

    @Bean
    @Qualifier("async3")
    public TaskExecutor myAsyncExecutor3() {
        return new ConcurrentTaskExecutor(threadPoolWithRejectDeal);
    }

}
