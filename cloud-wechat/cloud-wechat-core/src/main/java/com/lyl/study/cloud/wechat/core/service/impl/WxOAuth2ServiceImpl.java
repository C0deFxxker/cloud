package com.lyl.study.cloud.wechat.core.service.impl;

import com.lyl.study.cloud.wechat.api.dto.response.WxJsapiSign;
import com.lyl.study.cloud.wechat.api.dto.response.WxUserDTO;
import com.lyl.study.cloud.wechat.core.service.MultiWxMpService;
import com.lyl.study.cloud.wechat.core.service.WxOAuth2Service;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WxOAuth2ServiceImpl implements WxOAuth2Service {
    @Autowired
    private MultiWxMpService multiWxMpService;

    @Override
    public String oauth2getOpenId(String appId, String code) {
        return multiWxMpService.run(appId, wxMpService -> wxMpService.oauth2getAccessToken(code).getOpenId());
    }

    @Override
    public WxUserDTO oauth2getUserInfo(String appId, String openId) {
        return multiWxMpService.run(appId, wxMpService -> {
            WxMpOAuth2AccessToken token = new WxMpOAuth2AccessToken();
            token.setOpenId(openId);
            token.setAccessToken(wxMpService.getAccessToken());
            WxMpUser wxMpUser = wxMpService.oauth2getUserInfo(token, null);
            WxUserDTO user = new WxUserDTO();
            BeanUtils.copyProperties(wxMpUser, user);
            return user;
        });
    }

    @Override
    public WxJsapiSign createJsapiSignature(String appId, String url) {
        return multiWxMpService.run(appId, wxMpService -> {
            WxJsapiSignature signature = wxMpService.createJsapiSignature(url);
            WxJsapiSign wxJsapiSign = new WxJsapiSign();
            BeanUtils.copyProperties(signature, wxJsapiSign);
            return wxJsapiSign;
        });
    }
}
