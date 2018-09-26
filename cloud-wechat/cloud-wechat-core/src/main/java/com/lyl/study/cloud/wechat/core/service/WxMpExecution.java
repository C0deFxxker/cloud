package com.lyl.study.cloud.wechat.core.service;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;

@FunctionalInterface
public interface WxMpExecution<T> {
    T run(WxMpService wxMpService) throws WxErrorException;
}
