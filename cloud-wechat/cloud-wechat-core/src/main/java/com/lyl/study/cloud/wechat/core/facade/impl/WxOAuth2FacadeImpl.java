package com.lyl.study.cloud.wechat.core.facade.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.lyl.study.cloud.wechat.api.dto.WxJsapiSign;
import com.lyl.study.cloud.wechat.api.dto.WxUser;
import com.lyl.study.cloud.wechat.api.facade.WxOAuth2Facade;
import com.lyl.study.cloud.wechat.core.service.WxOAuth2Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class WxOAuth2FacadeImpl implements WxOAuth2Facade {
    @Autowired
    private WxOAuth2Service wxOAuth2Service;

    @Override
    public String oauth2getOpenId(String appId, String code) {
        return wxOAuth2Service.oauth2getOpenId(appId, code);
    }

    @Override
    public WxUser oauth2getUserInfo(String appId, String openId) {
        return wxOAuth2Service.oauth2getUserInfo(appId, openId);
    }

    @Override
    public WxJsapiSign createJsapiSignature(String appId, String url) {
        return wxOAuth2Service.createJsapiSignature(appId, url);
    }
}
