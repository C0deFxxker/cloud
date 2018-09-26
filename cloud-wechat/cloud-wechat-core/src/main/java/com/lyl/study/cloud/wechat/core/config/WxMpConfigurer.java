package com.lyl.study.cloud.wechat.core.config;

import com.lyl.study.cloud.wechat.core.service.MultiWxMpService;
import com.lyl.study.cloud.wechat.core.service.impl.MultiWxMpServiceImpl;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class WxMpConfigurer {
    @Autowired
    private MultiWxMpProperties multiWxMpProperties;

    @Bean
    public MultiWxMpService multiWxMpService() {
        List<WxMpConfigStorage> wxMpConfigStorages = multiWxMpProperties.listWechatMpProperties();
        Map<String, WxMpService> wxMpServiceMap = new HashMap<>(wxMpConfigStorages.size());

        // 多公众号Service
        for (WxMpConfigStorage wxMpConfigStorage : wxMpConfigStorages) {
            WxMpService wxMpService = new me.chanjar.weixin.mp.api.impl.WxMpServiceImpl();
            wxMpService.setWxMpConfigStorage(wxMpConfigStorage);
            wxMpServiceMap.put(wxMpConfigStorage.getAppId(), wxMpService);
        }

        return new MultiWxMpServiceImpl(wxMpServiceMap);
    }

}
