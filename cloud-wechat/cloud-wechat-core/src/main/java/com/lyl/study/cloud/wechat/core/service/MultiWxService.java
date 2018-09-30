package com.lyl.study.cloud.wechat.core.service;

import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

public interface MultiWxService {
    WxMpService getMpServiceByAppId(String appId);

    <T> T runByMpService(String appId, WxMpExecution<T> whatToDo);

    WxMpXmlOutMessage route(String appId, WxMpXmlMessage wxMessage);
}