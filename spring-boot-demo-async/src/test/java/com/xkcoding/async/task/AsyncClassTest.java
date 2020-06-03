package com.xkcoding.async.task;

import com.xkcoding.async.SpringBootDemoAsyncApplication;
import com.xkcoding.async.SpringBootDemoAsyncApplicationTests;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;



@Slf4j
public class AsyncClassTest extends SpringBootDemoAsyncApplicationTests {

    @Autowired
    AsyncClass asyncClass;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test() throws InterruptedException {
        asyncClass.foo();
        asyncClass.foo(10);
        Thread.sleep(100);
        asyncClass.foo();
    }

}
