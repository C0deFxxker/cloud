package com.lyl.study.cloud.wechat.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
@SpringBootApplication
public class CoreApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext
                = SpringApplication.run(CoreApplication.class, args);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("启动shutdownHook对程序进行关闭...");
            applicationContext.close();
        }));
    }
}
