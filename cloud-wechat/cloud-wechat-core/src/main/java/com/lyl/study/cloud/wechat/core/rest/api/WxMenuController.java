package com.lyl.study.cloud.wechat.core.rest.api;

import com.lyl.study.cloud.wechat.core.service.WxMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/{appId}/menu")
public class WxMenuController {
    @Autowired
    private WxMenuService wxMenuService;

    public String setMenu(String appId, String json) {
        return wxMenuService.setMenu(appId, json);
    }

    public String menuGet(String appId) {
        return wxMenuService.menuGet(appId);
    }
}
