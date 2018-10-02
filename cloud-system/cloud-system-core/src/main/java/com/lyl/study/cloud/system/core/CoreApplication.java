package com.lyl.study.cloud.system.core;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@MapperScan("com.lyl.study.cloud.system.core.mapper")
public class CoreApplication {
    private static final Logger logger = LoggerFactory.getLogger(CoreApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(CoreApplication.class, args);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("启动shutdownHook对程序进行关闭...");
            applicationContext.close();
        }));

        // 保持非Web项目在加载完Spring上下文后自动关闭，需要添加下面这些代码，让它保持运作
        while (applicationContext.isActive()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignored) {
            }
        }
    }
}
