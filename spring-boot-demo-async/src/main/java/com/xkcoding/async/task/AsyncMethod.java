package com.xkcoding.async.task;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

/**
 * 异步方法示例，关键点有三步：
 * 1.启动类增加注解 @EnableAsync
 * 2.当前类声明为服务 @Service
 * 3.方法上面添加注解 @Async
 * 限制：
 * 默认类内的方法调用不会被aop拦截，也就是说同一个类内的方法调用，@Async不生效
 * 解决办法：
 * 如果要使同一个类中的方法之间调用也被拦截，需要使用spring容器中的实例对象，而不是使用默认的this，因为通过bean实例的调用才会被spring的aop拦截
 * 本例使用方法： AsyncMethod asyncMethod = context.getBean(AsyncMethod.class); 然后使用这个引用调用本地的方法即可达到被拦截的目的
 * 备注：
 * 这种方法只能拦截protected，default，public方法，private方法无法拦截。这个是spring aop的一个机制。
 * 
 * 默认情况下异步方法的调用使用的是SimpleAsyncTaskExecutor来执行异步方法调用，实际是每个方法都会起一个新的线程。
 * 大致运行过程：（以asyncMethod.bar1();为例）
 * 1.调用bar1()方法被aop拦截
 * 2.使用cglib获取要执行的方法和入参、当前实例（后续用于反射调用方法）。这些是运行一个方法的必要条件，可以封装成独立的方法来运行
 * 3.启动新的线程，调用上面封装的实际要调用的方法
 * 4.返回方法调用的结果
 * 前提是启动的时候被spring提前处理，将方法进行封装，加载流程：
 * AsyncAnnotationBeanPostProcessor ->
 * 如果要修改@Async异步方法底层调用：
 * 可以实现AsyncConfigurer接口，或者提供TaskExecutor实例（然后在@Async中指定这个实例），详见本例代码
 * 
 * 异步方法返回类型只能有两种：void和java.util.concurrent.Future
 * 当返回类型为void的时候，方法调用过程产生的异常不会抛到调用者层面，可以通过注册AsyncUncaughtExceptionHandler来捕获此类异常
 * 当返回类型为Future的时候，方法调用过程差生的异常会抛到调用者层面
 * 
 * @DESC
 * @author guchuang
 *
 */
@Service
@Slf4j
public class AsyncMethod {
    // @Autowired
    AsyncMethod           asyncMethod;

    @Autowired
    WebApplicationContext context;

    /*@PostConstruct
    public void init() {
        this.asyncMethod = context.getBean(AsyncMethod.class);
    }*/

    @Async
    public void bar() {
        log.info("sleep bar");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Async
    private void bar1() {
        log.info("private bar");
    }

    @Async
    public void bar2() {
        log.info("public bar");
    }

    @Async
    protected void bar3() {
        log.info("protected bar");
    }

    @Async
    void bar4() {
        log.info("default bar");
    }

    @Async
    public void foo1() {
        log.info("foo1");
        this.bar1();
        this.bar2();
        asyncMethod = context.getBean(AsyncMethod.class);
        asyncMethod.bar(); // 异步
        asyncMethod.bar1(); // 同步
        asyncMethod.bar2(); // 异步
        asyncMethod.bar3(); // 异步
        asyncMethod.bar4(); // 异步
    }

    /**
     * 指定这个异步方法使用的底层执行器TaskExecutor
     * 
     * @param index
     */
    @Async("async1")
    public void foo2(int index) {
        log.info("foo2 with index:" + index);
    }

    @Async
    public void foo3(int index, String threadName) {
        // 设置线程名
        Thread.currentThread().setName(threadName);
        log.info("foo3 with index:" + index);
    }

    @Async
    public void fooE() {
        throw new RuntimeException("无返回值异步方法抛出异常");
    }

    @Async
    public Future<String> futureE() {
        throw new RuntimeException("有返回值异步方法抛出异常");
    }

    /**
     * 带返回值的异步调用
     * 
     * @return
     */
    @Async
    public Future<String> futureTask1() throws InterruptedException {
        log.info("start run future task1");
        Thread.sleep(1000);
        return new AsyncResult<String>("future task1");
    }

    @Async
    public CompletableFuture<String> futureTask2() {
        log.info("Running task  thread: " + Thread.currentThread().getName());

        CompletableFuture<String> future = new CompletableFuture<String>() {
            @Override
            public String get() throws InterruptedException, ExecutionException {
                return " task result";
            }
        };
        return future;
    }

    /**
     * 指定使用的TaskExecutor，这个bean在config中已经配置
     * 
     * @param index
     * @param time
     */
    @Async("async2")
    public void asyncSleep(int index, int time) {
        try {
            Thread.sleep(time * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("task:" + index + " end");
    }

    @Async("async3")
    public void asyncSleep3(int index, int time) {
        try {
            Thread.sleep(time * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("task:" + index + " end");
    }
}
