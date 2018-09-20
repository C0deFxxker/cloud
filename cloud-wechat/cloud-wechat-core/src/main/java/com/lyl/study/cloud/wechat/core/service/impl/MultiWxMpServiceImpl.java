package com.lyl.study.cloud.wechat.core.service.impl;

import com.lyl.study.cloud.wechat.api.exception.InvalidAppIdException;
import com.lyl.study.cloud.wechat.core.service.MultiWxMpService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class MultiWxMpServiceImpl implements MultiWxMpService {
    private Map<String, WxMpService> wxMpServiceMap;

    public MultiWxMpServiceImpl(Map<String, WxMpService> wxMpServiceMap) {
        this.wxMpServiceMap = wxMpServiceMap;
    }

    @Override
    public WxMpService getByAppId(String appId) throws InvalidAppIdException {
        WxMpService wxMpService = wxMpServiceMap.get(appId);
        if (wxMpService == null) {
            throw new InvalidAppIdException("无效AppID: " + appId);
        }
        return wxMpService;
    }
}