package com.lyl.study.cloud.wechat.core.service.impl;

import com.lyl.study.cloud.base.util.JsonUtils;
import com.lyl.study.cloud.wechat.core.service.MultiWxMpService;
import com.lyl.study.cloud.wechat.core.service.WxMenuService;
import me.chanjar.weixin.mp.api.WxMpMenuService;
import me.chanjar.weixin.mp.bean.menu.WxMpMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WxMenuServiceImpl implements WxMenuService {
    @Autowired
    private MultiWxMpService multiWxMpService;

    public String setMenu(String appId, String json) {
        return multiWxMpService.run(appId, wxMpService -> {
            WxMpMenuService menuService = wxMpService.getMenuService();
            return menuService.menuCreate(json);
        });
    }

    public String menuGet(String appId) {
        return multiWxMpService.run(appId, wxMpService -> {
            WxMpMenuService menuService = wxMpService.getMenuService();
            WxMpMenu result = menuService.menuGet();
            return JsonUtils.toJson(result);
        });
    }
}
