package com.lyl.study.cloud.cms.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(UploadProperties.class)
public class CmsWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(CmsWebApplication.class, args);
    }
}
