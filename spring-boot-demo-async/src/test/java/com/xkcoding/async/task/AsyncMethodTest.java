package com.xkcoding.async.task;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import com.xkcoding.async.SpringBootDemoAsyncApplicationTests;
import lombok.extern.slf4j.Slf4j;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


@Slf4j
public class AsyncMethodTest extends SpringBootDemoAsyncApplicationTests {

    @Autowired
    AsyncMethod asyncMethod;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void afterClass() throws Exception {
        Thread.sleep(3000);
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test1() throws InterruptedException {
        asyncMethod.foo1();
        log.info("just wait");
        Thread.sleep(2000);
    }
    @Test
    public void test2() {
        for (int i = 0; i < 10; i++) {
            asyncMethod.foo2(i);
        }
    }
    @Test
    public void test3() {
        for (int i = 0; i < 10; i++) {
            asyncMethod.foo3(i, "gc-thread-"+i);
        }
    }

    @Test
    public void testE() {
        try {
            Future<String> result = asyncMethod.futureE();
            //这里调用get才会获得异常
            log.info(result.get());
        } catch(Exception e) {
            //e.printStackTrace();
            log.info("this is excepted Exception:" + e.getMessage());
        }
        // 直接抛出异常
        asyncMethod.fooE();
        log.info("end call e");
        //log.sleep(1000);
    }
    
    @Test
    public void testFuture() throws InterruptedException, ExecutionException {
        log.info("\n-----------------start-----------------------");
        Future<String> result1 = asyncMethod.futureTask1();
        CompletableFuture<String> result2 = asyncMethod.futureTask2();
        // 获取返回值
        log.info("result1:" + result1.get());
        log.info("result2:" + result2.get());
    }
    
    @Test
    public void testReject() throws InterruptedException {
        log.info("\n-----------------start testReject-----------------------");
        log.info("start add task");
        //当超过线程词最大容量的时候，会抛出TaskRejectedException
        try {
            for (int i = 0; i < 10; i++) {
                asyncMethod.asyncSleep(i, 1);
            }
        } catch(RejectedExecutionException e) {
            log.info("excepted exception:" + e.getMessage());
        }
        log.info("finished add task");
        Thread.sleep(100 * 1000);
    }
    
    @Test
    public void testRejectWithDeal() throws InterruptedException {
        log.info("\n-----------------start testRejectWithDeal-----------------------");
        log.info("start add task");
        //当超过线程词最大容量的时候，会抛出TaskRejectedException
        try {
            for (int i = 0; i < 10; i++) {
                asyncMethod.asyncSleep3(i, 1);
            }
        } catch(RejectedExecutionException e) {
            log.info("excepted exception:" + e.getMessage());
        }
        log.info("finished add task");
        Thread.sleep(100 * 1000);
    }
}
