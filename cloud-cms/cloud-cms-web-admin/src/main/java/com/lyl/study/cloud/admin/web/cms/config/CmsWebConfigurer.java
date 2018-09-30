package com.lyl.study.cloud.admin.web.cms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(UploadProperties.class)
public class CmsWebConfigurer {
}
