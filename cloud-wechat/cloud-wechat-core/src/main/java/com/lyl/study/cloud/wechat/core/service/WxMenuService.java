package com.lyl.study.cloud.wechat.core.service;

import me.chanjar.weixin.common.bean.menu.WxMenu;
import me.chanjar.weixin.mp.bean.menu.WxMpMenu;

public interface WxMenuService {
    String setMenu(String appId, String json);

    String setMenu(String appId, WxMenu menu);

    WxMpMenu menuGet(String appId);
}
