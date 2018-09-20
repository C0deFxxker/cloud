package com.lyl.study.cloud.wechat.core.service.impl;

import com.lyl.study.cloud.wechat.api.exception.WxApiRemoteException;
import com.lyl.study.cloud.wechat.core.service.MultiWxMpService;
import com.lyl.study.cloud.wechat.core.service.WxMenuService;
import me.chanjar.weixin.common.bean.menu.WxMenu;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpMenuService;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.menu.WxMpMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WxMenuServiceImpl implements WxMenuService {
    @Autowired
    private MultiWxMpService multiWxMpService;

    public String setMenu(String appId, String json) {
        try {
            WxMpService wxMpService = multiWxMpService.getByAppId(appId);
            WxMpMenuService menuService = wxMpService.getMenuService();
            return menuService.menuCreate(json);
        } catch (WxErrorException e) {
            throw new WxApiRemoteException(e.getMessage());
        }
    }

    public String setMenu(String appId, WxMenu menu) {
        try {
            WxMpService wxMpService = multiWxMpService.getByAppId(appId);
            WxMpMenuService menuService = wxMpService.getMenuService();
            return menuService.menuCreate(menu);
        } catch (WxErrorException e) {
            throw new WxApiRemoteException(e.getMessage());
        }
    }

    public WxMpMenu menuGet(String appId) {
        try {
            WxMpService wxMpService = multiWxMpService.getByAppId(appId);
            WxMpMenuService menuService = wxMpService.getMenuService();
            return menuService.menuGet();
        } catch (WxErrorException e) {
            throw new WxApiRemoteException(e.getMessage());
        }
    }
}
