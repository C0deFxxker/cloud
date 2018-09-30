package com.lyl.study.cloud.wechat.core.service.impl;

import com.lyl.study.cloud.wechat.api.exception.InvalidAppIdException;
import com.lyl.study.cloud.wechat.api.exception.WxApiRemoteException;
import com.lyl.study.cloud.wechat.core.service.MultiWxService;
import com.lyl.study.cloud.wechat.core.service.WxMpExecution;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

import java.util.HashMap;
import java.util.Map;

public class MultiWxServiceImpl implements MultiWxService {
    private Map<String, WxMpService> wxMpServiceMap;
    private Map<String, WxMpMessageRouter> wxMpMessageRouterMap = new HashMap<>();

    public MultiWxServiceImpl(Map<String, WxMpService> wxMpServiceMap) {
        this.wxMpServiceMap = wxMpServiceMap;
    }

    public MultiWxServiceImpl(Map<String, WxMpService> wxMpServiceMap,
                              Map<String, WxMpMessageRouter> wxMpMessageRouterMap) {
        this.wxMpServiceMap = wxMpServiceMap;
        this.wxMpMessageRouterMap = wxMpMessageRouterMap;
    }

    public Map<String, WxMpService> getWxMpServiceMap() {
        return wxMpServiceMap;
    }

    public Map<String, WxMpMessageRouter> getWxMpMessageRouterMap() {
        return wxMpMessageRouterMap;
    }

    @Override
    public WxMpService getMpServiceByAppId(String appId) throws InvalidAppIdException {
        WxMpService wxMpService = wxMpServiceMap.get(appId);
        if (wxMpService == null) {
            throw new InvalidAppIdException("无效AppID: " + appId);
        }
        return wxMpService;
    }

    @Override
    public <T> T runByMpService(String appId, WxMpExecution<T> whatToDo) {
        WxMpService wxMpService = getMpServiceByAppId(appId);
        try {
            return whatToDo.run(wxMpService);
        } catch (WxErrorException e) {
            throw new WxApiRemoteException(e.getMessage());
        }
    }

    @Override
    public WxMpXmlOutMessage route(String appId, WxMpXmlMessage wxMessage) {
        WxMpMessageRouter router = wxMpMessageRouterMap.get(appId);
        if (router == null) {
            throw new InvalidAppIdException("无效AppID: " + appId);
        }
        return router.route(wxMessage);
    }
}