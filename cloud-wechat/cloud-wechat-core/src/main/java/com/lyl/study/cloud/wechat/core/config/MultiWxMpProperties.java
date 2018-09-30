package com.lyl.study.cloud.wechat.core.config;

import com.lyl.study.cloud.base.util.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 多微信公众号配置类
 *
 * @author lyl
 */
@Slf4j
@ConfigurationProperties(prefix = "cloud.wechat.mp")
public class MultiWxMpProperties implements InitializingBean {

    private List<WxMpInMemoryConfigStorage> configs = new ArrayList<>();

    public List<WxMpInMemoryConfigStorage> getConfigs() {
        return configs;
    }

    public MultiWxMpProperties setConfigs(List<WxMpInMemoryConfigStorage> configs) {
        this.configs = configs;
        return this;
    }

    private Map<String, WxMpConfigStorage> wxMpConfigStorageMap = new HashMap<>();

    public WxMpConfigStorage getByAppId(String appId) {
        return wxMpConfigStorageMap.get(appId);
    }

    public List<WxMpConfigStorage> listWechatMpProperties() {
        return new ArrayList<>(wxMpConfigStorageMap.values());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (wxMpConfigStorageMap.isEmpty() && !configs.isEmpty()) {
            int idx = 0;
            for (WxMpInMemoryConfigStorage each : configs) {
                WxMpConfigStorage wxMpConfigStorage = BeanUtils.transform(each, new WxMpInMemoryConfigStorage());
                wxMpConfigStorageMap.put(wxMpConfigStorage.getAppId(), wxMpConfigStorage);
                log.info("配置公众号：[appId={}, secret={}, token={}, aesKey={}]",
                        wxMpConfigStorage.getAppId(), wxMpConfigStorage.getSecret(),
                        wxMpConfigStorage.getToken(), wxMpConfigStorage.getAesKey());
                idx++;
            }
        }
    }
}
