package com.lyl.study.cloud.wechat.core.rest.api;

import com.lyl.study.cloud.base.dto.Result;
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
import org.springframework.web.bind.annotation.*;

import static com.lyl.study.cloud.wechat.api.ErrorCode.OK;

@RestController
@RequestMapping("/oauth")
public class OAuthController {
    private WxOAuth2Service wxOAuth2Service;

    /**
     * 获取OpenID
     */
    @GetMapping("/openId")
    public Result<String> oauth2getOpenId(@PathVariable("appId") String appId,
                                          @RequestParam("code") String code) {
        return new Result<>(OK, "查询成功", wxOAuth2Service.oauth2getOpenId(appId, code));
    }

    /**
     * 获取微信用户信息
     */
    public Result<WxUser> oauth2getUserInfo(@PathVariable("appId") String appId,
                                            @RequestParam("openId") String openId) {
        return new Result<>(OK, "查询成功", wxOAuth2Service.oauth2getUserInfo(appId, openId));
    }

    /**
     * 创建Jsapi签名
     */
    public Result<WxJsapiSign> createJsapiSignature(@PathVariable("appId") String appId,
                                                    @RequestParam("url") String url) {
        return new Result<>(OK, "查询成功", wxOAuth2Service.createJsapiSignature(appId, url));
    }
}