package com.lyl.study.cloud.wechat.core.facade.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.lyl.study.cloud.wechat.api.facade.WxMenuFacade;
import com.lyl.study.cloud.wechat.core.service.WxMenuService;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class WxMenuFacadeImpl implements WxMenuFacade {
    @Autowired
    private WxMenuService wxMenuService;

    @Override
    public String setMenu(String appId, String json) {
        return wxMenuService.setMenu(appId, json);
    }

    @Override
    public String menuGet(String appId) {
        return wxMenuService.menuGet(appId);
    }
}
