package com.lyl.study.cloud.admin.web.cms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CmsWebApplication {
    private static final Logger logger = LoggerFactory.getLogger(CmsWebApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(CmsWebApplication.class, args);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("启动shutdownHook对程序进行关闭...");
            // do something before app close
        }));
    }
}
