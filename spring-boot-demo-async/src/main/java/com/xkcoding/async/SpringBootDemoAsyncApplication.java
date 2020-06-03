package com.xkcoding.async;

import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * <p>
 * 启动器
 * </p>
 *
 *异步方法示例，关键点有三步：
 *  1.启动类增加注解 @EnableAsync
 *  2.当前类声明为服务 @Service
 *  3.方法上面添加注解 @Async
 *限制：
 *   默认类内的方法调用不会被aop拦截，也就是说同一个类内的方法调用，@Async不生效
 *解决办法：
 *  如果要使同一个类中的方法之间调用也被拦截，需要使用spring容器中的实例对象，而不是使用默认的this，因为通过bean实例的调用才会被spring的aop拦截
 */
@EnableAsync
@SpringBootApplication
@Slf4j
public class SpringBootDemoAsyncApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootDemoAsyncApplication.class, args);
    }

}

