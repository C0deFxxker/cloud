package com.lyl.study.cloud.wechat.core.service.impl;

import com.lyl.study.cloud.wechat.api.dto.WxJsapiSign;
import com.lyl.study.cloud.wechat.api.dto.WxUser;
import com.lyl.study.cloud.wechat.api.exception.WxApiRemoteException;
import com.lyl.study.cloud.wechat.core.service.MultiWxMpService;
import com.lyl.study.cloud.wechat.core.service.WxOAuth2Service;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
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
        WxMpService service = multiWxMpService.getByAppId(appId);
        try {
            WxMpOAuth2AccessToken token = service.oauth2getAccessToken(code);
            return token.getOpenId();
        } catch (WxErrorException e) {
            throw new WxApiRemoteException(e.getMessage());
        }
    }

    @Override
    public WxUser oauth2getUserInfo(String appId, String openId) {
        WxMpService service = multiWxMpService.getByAppId(appId);
        try {
            WxMpOAuth2AccessToken token = new WxMpOAuth2AccessToken();
            token.setOpenId(openId);
            token.setAccessToken(service.getAccessToken());
            WxMpUser wxMpUser = service.oauth2getUserInfo(token, null);
            WxUser user = new WxUser();
            BeanUtils.copyProperties(wxMpUser, user);
            return user;
        } catch (WxErrorException e) {
            throw new WxApiRemoteException(e.getMessage());
        }
    }

    @Override
    public WxJsapiSign createJsapiSignature(String appId, String url) {
        WxMpService service = multiWxMpService.getByAppId(appId);
        try {
            WxJsapiSignature signature = service.createJsapiSignature(url);
            WxJsapiSign wxJsapiSign = new WxJsapiSign();
            BeanUtils.copyProperties(signature, wxJsapiSign);
            return wxJsapiSign;
        } catch (WxErrorException e) {
            throw new WxApiRemoteException(e.getMessage());
        }
    }
}
