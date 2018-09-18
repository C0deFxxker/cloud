package com.lyl.study.cloud.admin.web.cms.config;

import com.lyl.study.cloud.admin.web.cms.UploadProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(UploadProperties.class)
public class CmsWebConfigurer {
}
