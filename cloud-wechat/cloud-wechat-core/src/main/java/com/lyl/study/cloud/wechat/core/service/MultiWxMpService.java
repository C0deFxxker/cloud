package com.lyl.study.cloud.wechat.core.service;

import me.chanjar.weixin.mp.api.WxMpService;

public interface MultiWxMpService {
    WxMpService getByAppId(String appId);

    <T> T run(String appId, WxMpExecution<T> whatToDo);
}