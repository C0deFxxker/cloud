package com.lyl.study.cloud.admin.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * 整个Admin应用都是通过import不同模块的jar来实现
 */
@SpringBootApplication
public class WebApplication {
    private static final Logger logger = LoggerFactory.getLogger(WebApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("启动shutdownHook对程序进行关闭...");
            // do something before app close
        }));
    }
}
