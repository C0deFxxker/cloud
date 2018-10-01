//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package me.chanjar.weixin.mp.api.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.error.WxError;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.StandardSessionManager;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.common.util.DataUtils;
import me.chanjar.weixin.common.util.RandomUtils;
import me.chanjar.weixin.common.util.crypto.SHA1;
import me.chanjar.weixin.common.util.http.RequestExecutor;
import me.chanjar.weixin.common.util.http.RequestHttp;
import me.chanjar.weixin.common.util.http.SimpleGetRequestExecutor;
import me.chanjar.weixin.common.util.http.SimplePostRequestExecutor;
import me.chanjar.weixin.common.util.http.URIUtil;
import me.chanjar.weixin.mp.api.WxMpAiOpenService;
import me.chanjar.weixin.mp.api.WxMpCardService;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpDataCubeService;
import me.chanjar.weixin.mp.api.WxMpDeviceService;
import me.chanjar.weixin.mp.api.WxMpKefuService;
import me.chanjar.weixin.mp.api.WxMpMassMessageService;
import me.chanjar.weixin.mp.api.WxMpMaterialService;
import me.chanjar.weixin.mp.api.WxMpMemberCardService;
import me.chanjar.weixin.mp.api.WxMpMenuService;
import me.chanjar.weixin.mp.api.WxMpQrcodeService;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.WxMpShakeService;
import me.chanjar.weixin.mp.api.WxMpStoreService;
import me.chanjar.weixin.mp.api.WxMpSubscribeMsgService;
import me.chanjar.weixin.mp.api.WxMpTemplateMsgService;
import me.chanjar.weixin.mp.api.WxMpUserBlacklistService;
import me.chanjar.weixin.mp.api.WxMpUserService;
import me.chanjar.weixin.mp.api.WxMpUserTagService;
import me.chanjar.weixin.mp.api.WxMpWifiService;
import me.chanjar.weixin.mp.bean.WxMpSemanticQuery;
import me.chanjar.weixin.mp.bean.result.WxMpCurrentAutoReplyInfo;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpSemanticQueryResult;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseWxMpServiceImpl<H, P> implements WxMpService, RequestHttp<H, P> {
    private static final JsonParser JSON_PARSER = new JsonParser();
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    protected WxSessionManager sessionManager = new StandardSessionManager();
    protected WxMpConfigStorage wxMpConfigStorage;
    private WxMpKefuService kefuService = new WxMpKefuServiceImpl(this);
    private WxMpMaterialService materialService = new WxMpMaterialServiceImpl(this);
    private WxMpMenuService menuService = new WxMpMenuServiceImpl(this);
    private WxMpUserService userService = new WxMpUserServiceImpl(this);
    private WxMpUserTagService tagService = new WxMpUserTagServiceImpl(this);
    private WxMpQrcodeService qrCodeService = new WxMpQrcodeServiceImpl(this);
    private WxMpCardService cardService = new WxMpCardServiceImpl(this);
    private WxMpStoreService storeService = new WxMpStoreServiceImpl(this);
    private WxMpDataCubeService dataCubeService = new WxMpDataCubeServiceImpl(this);
    private WxMpUserBlacklistService blackListService = new WxMpUserBlacklistServiceImpl(this);
    private WxMpTemplateMsgService templateMsgService = new WxMpTemplateMsgServiceImpl(this);
    private WxMpSubscribeMsgService subscribeMsgService = new WxMpSubscribeMsgServiceImpl(this);
    private WxMpDeviceService deviceService = new WxMpDeviceServiceImpl(this);
    private WxMpShakeService shakeService = new WxMpShakeServiceImpl(this);
    private WxMpMemberCardService memberCardService = new WxMpMemberCardServiceImpl(this);
    private WxMpMassMessageService massMessageService = new WxMpMassMessageServiceImpl(this);
    private WxMpAiOpenService aiOpenService = new WxMpAiOpenServiceImpl(this);
    private WxMpWifiService wifiService = new WxMpWifiServiceImpl(this);
    private int retrySleepMillis = 1000;
    private int maxRetryTimes = 5;

    public BaseWxMpServiceImpl() {
    }

    public boolean checkSignature(String timestamp, String nonce, String signature) {
        try {
            // 此处修改了原库的bug
            List<String> list = Arrays.asList(this.getWxMpConfigStorage().getToken(), timestamp, nonce);
            list.sort(String::compareTo);
            return SHA1.gen(list.toArray(new String[list.size()])).equals(signature);
        } catch (Exception var5) {
            this.log.error("Checking signature failed, and the reason is :" + var5.getMessage());
            return false;
        }
    }

    public String getJsapiTicket() throws WxErrorException {
        return this.getJsapiTicket(false);
    }

    public String getJsapiTicket(boolean forceRefresh) throws WxErrorException {
        Lock lock = this.getWxMpConfigStorage().getJsapiTicketLock();

        try {
            lock.lock();
            if (forceRefresh) {
                this.getWxMpConfigStorage().expireJsapiTicket();
            }

            if (this.getWxMpConfigStorage().isJsapiTicketExpired()) {
                String responseContent = (String)this.execute(SimpleGetRequestExecutor.create(this), "https://api.weixin.qq.com/cgi-bin/ticket/getticket?type=jsapi", null);
                JsonElement tmpJsonElement = JSON_PARSER.parse(responseContent);
                JsonObject tmpJsonObject = tmpJsonElement.getAsJsonObject();
                String jsapiTicket = tmpJsonObject.get("ticket").getAsString();
                int expiresInSeconds = tmpJsonObject.get("expires_in").getAsInt();
                this.getWxMpConfigStorage().updateJsapiTicket(jsapiTicket, expiresInSeconds);
            }
        } finally {
            lock.unlock();
        }

        return this.getWxMpConfigStorage().getJsapiTicket();
    }

    public WxJsapiSignature createJsapiSignature(String url) throws WxErrorException {
        long timestamp = System.currentTimeMillis() / 1000L;
        String randomStr = RandomUtils.getRandomStr();
        String jsapiTicket = this.getJsapiTicket(false);
        String signature = SHA1.genWithAmple(new String[]{"jsapi_ticket=" + jsapiTicket, "noncestr=" + randomStr, "timestamp=" + timestamp, "url=" + url});
        WxJsapiSignature jsapiSignature = new WxJsapiSignature();
        jsapiSignature.setAppId(this.getWxMpConfigStorage().getAppId());
        jsapiSignature.setTimestamp(timestamp);
        jsapiSignature.setNonceStr(randomStr);
        jsapiSignature.setUrl(url);
        jsapiSignature.setSignature(signature);
        return jsapiSignature;
    }

    public String getAccessToken() throws WxErrorException {
        return this.getAccessToken(false);
    }

    public String shortUrl(String longUrl) throws WxErrorException {
        JsonObject o = new JsonObject();
        o.addProperty("action", "long2short");
        o.addProperty("long_url", longUrl);
        String responseContent = this.post("https://api.weixin.qq.com/cgi-bin/shorturl", o.toString());
        JsonElement tmpJsonElement = JSON_PARSER.parse(responseContent);
        return tmpJsonElement.getAsJsonObject().get("short_url").getAsString();
    }

    public WxMpSemanticQueryResult semanticQuery(WxMpSemanticQuery semanticQuery) throws WxErrorException {
        String responseContent = this.post("https://api.weixin.qq.com/semantic/semproxy/search", semanticQuery.toJson());
        return WxMpSemanticQueryResult.fromJson(responseContent);
    }

    public String oauth2buildAuthorizationUrl(String redirectURI, String scope, String state) {
        return String.format("https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s&connect_redirect=1#wechat_redirect", this.getWxMpConfigStorage().getAppId(), URIUtil.encodeURIComponent(redirectURI), scope, StringUtils.trimToEmpty(state));
    }

    public String buildQrConnectUrl(String redirectURI, String scope, String state) {
        return String.format("https://open.weixin.qq.com/connect/qrconnect?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect", this.getWxMpConfigStorage().getAppId(), URIUtil.encodeURIComponent(redirectURI), scope, StringUtils.trimToEmpty(state));
    }

    private WxMpOAuth2AccessToken getOAuth2AccessToken(String url) throws WxErrorException {
        try {
            RequestExecutor<String, String> executor = SimpleGetRequestExecutor.create(this);
            String responseText = (String)executor.execute(url, null);
            return WxMpOAuth2AccessToken.fromJson(responseText);
        } catch (IOException var4) {
            throw new RuntimeException(var4);
        }
    }

    public WxMpOAuth2AccessToken oauth2getAccessToken(String code) throws WxErrorException {
        String url = String.format("https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code", this.getWxMpConfigStorage().getAppId(), this.getWxMpConfigStorage().getSecret(), code);
        return this.getOAuth2AccessToken(url);
    }

    public WxMpOAuth2AccessToken oauth2refreshAccessToken(String refreshToken) throws WxErrorException {
        String url = String.format("https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=%s&grant_type=refresh_token&refresh_token=%s", this.getWxMpConfigStorage().getAppId(), refreshToken);
        return this.getOAuth2AccessToken(url);
    }

    public WxMpUser oauth2getUserInfo(WxMpOAuth2AccessToken token, String lang) throws WxErrorException {
        if (lang == null) {
            lang = "zh_CN";
        }

        String url = String.format("https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=%s", token.getAccessToken(), token.getOpenId(), lang);

        try {
            RequestExecutor<String, String> executor = SimpleGetRequestExecutor.create(this);
            String responseText = (String)executor.execute(url, null);
            return WxMpUser.fromJson(responseText);
        } catch (IOException var6) {
            throw new RuntimeException(var6);
        }
    }

    public boolean oauth2validateAccessToken(WxMpOAuth2AccessToken token) {
        String url = String.format("https://api.weixin.qq.com/sns/auth?access_token=%s&openid=%s", token.getAccessToken(), token.getOpenId());

        try {
            SimpleGetRequestExecutor.create(this).execute(url, null);
            return true;
        } catch (IOException var4) {
            throw new RuntimeException(var4);
        } catch (WxErrorException var5) {
            return false;
        }
    }

    public String[] getCallbackIP() throws WxErrorException {
        String responseContent = this.get("https://api.weixin.qq.com/cgi-bin/getcallbackip", (String)null);
        JsonElement tmpJsonElement = JSON_PARSER.parse(responseContent);
        JsonArray ipList = tmpJsonElement.getAsJsonObject().get("ip_list").getAsJsonArray();
        String[] ipArray = new String[ipList.size()];

        for(int i = 0; i < ipList.size(); ++i) {
            ipArray[i] = ipList.get(i).getAsString();
        }

        return ipArray;
    }

    public WxMpCurrentAutoReplyInfo getCurrentAutoReplyInfo() throws WxErrorException {
        return WxMpCurrentAutoReplyInfo.fromJson(this.get("https://api.weixin.qq.com/cgi-bin/get_current_autoreply_info", (String)null));
    }

    public void clearQuota(String appid) throws WxErrorException {
        JsonObject o = new JsonObject();
        o.addProperty("appid", appid);
        this.post("https://api.weixin.qq.com/cgi-bin/clear_quota", o.toString());
    }

    public String get(String url, String queryParam) throws WxErrorException {
        return (String)this.execute(SimpleGetRequestExecutor.create(this), url, queryParam);
    }

    public String post(String url, String postData) throws WxErrorException {
        return (String)this.execute(SimplePostRequestExecutor.create(this), url, postData);
    }

    public <T, E> T execute(RequestExecutor<T, E> executor, String uri, E data) throws WxErrorException {
        int retryTimes = 0;

        while(true) {
            try {
                return this.executeInternal(executor, uri, data);
            } catch (WxErrorException var10) {
                if (retryTimes + 1 > this.maxRetryTimes) {
                    this.log.warn("重试达到最大次数【{}】", this.maxRetryTimes);
                    throw new RuntimeException("微信服务端异常，超出重试次数");
                }

                WxError error = var10.getError();
                if (error.getErrorCode() != -1) {
                    throw var10;
                }

                int sleepMillis = this.retrySleepMillis * (1 << retryTimes);

                try {
                    this.log.warn("微信系统繁忙，{} ms 后重试(第{}次)", sleepMillis, retryTimes + 1);
                    Thread.sleep((long)sleepMillis);
                } catch (InterruptedException var9) {
                    throw new RuntimeException(var9);
                }

                if (retryTimes++ >= this.maxRetryTimes) {
                    this.log.warn("重试达到最大次数【{}】", this.maxRetryTimes);
                    throw new RuntimeException("微信服务端异常，超出重试次数");
                }
            }
        }
    }

    public <T, E> T executeInternal(RequestExecutor<T, E> executor, String uri, E data) throws WxErrorException {
        E dataForLog = DataUtils.handleDataWithSecret(data);
        if (uri.contains("access_token=")) {
            throw new IllegalArgumentException("uri参数中不允许有access_token: " + uri);
        } else {
            String accessToken = this.getAccessToken(false);
            String uriWithAccessToken = uri + (uri.contains("?") ? "&" : "?") + "access_token=" + accessToken;

            try {
                T result = executor.execute(uriWithAccessToken, data);
                this.log.debug("\n【请求地址】: {}\n【请求参数】：{}\n【响应数据】：{}", new Object[]{uriWithAccessToken, dataForLog, result});
                return result;
            } catch (WxErrorException var9) {
                WxError error = var9.getError();
                if (error.getErrorCode() == 42001 || error.getErrorCode() == 40001 || error.getErrorCode() == 40014) {
                    this.getWxMpConfigStorage().expireAccessToken();
                    if (this.getWxMpConfigStorage().autoRefreshToken()) {
                        return this.execute(executor, uri, data);
                    }
                }

                if (error.getErrorCode() != 0) {
                    this.log.error("\n【请求地址】: {}\n【请求参数】：{}\n【错误信息】：{}", new Object[]{uriWithAccessToken, dataForLog, error});
                    throw new WxErrorException(error, var9);
                } else {
                    return null;
                }
            } catch (IOException var10) {
                this.log.error("\n【请求地址】: {}\n【请求参数】：{}\n【异常信息】：{}", new Object[]{uriWithAccessToken, dataForLog, var10.getMessage()});
                throw new WxErrorException(WxError.builder().errorMsg(var10.getMessage()).build(), var10);
            }
        }
    }

    public WxMpConfigStorage getWxMpConfigStorage() {
        return this.wxMpConfigStorage;
    }

    public void setWxMpConfigStorage(WxMpConfigStorage wxConfigProvider) {
        this.wxMpConfigStorage = wxConfigProvider;
        this.initHttp();
    }

    public void setRetrySleepMillis(int retrySleepMillis) {
        this.retrySleepMillis = retrySleepMillis;
    }

    public void setMaxRetryTimes(int maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
    }

    public WxMpKefuService getKefuService() {
        return this.kefuService;
    }

    public WxMpMaterialService getMaterialService() {
        return this.materialService;
    }

    public WxMpMenuService getMenuService() {
        return this.menuService;
    }

    public WxMpUserService getUserService() {
        return this.userService;
    }

    public WxMpUserTagService getUserTagService() {
        return this.tagService;
    }

    public WxMpQrcodeService getQrcodeService() {
        return this.qrCodeService;
    }

    public WxMpCardService getCardService() {
        return this.cardService;
    }

    public WxMpDataCubeService getDataCubeService() {
        return this.dataCubeService;
    }

    public WxMpUserBlacklistService getBlackListService() {
        return this.blackListService;
    }

    public WxMpStoreService getStoreService() {
        return this.storeService;
    }

    public WxMpTemplateMsgService getTemplateMsgService() {
        return this.templateMsgService;
    }

    public WxMpSubscribeMsgService getSubscribeMsgService() {
        return this.subscribeMsgService;
    }

    public WxMpDeviceService getDeviceService() {
        return this.deviceService;
    }

    public WxMpShakeService getShakeService() {
        return this.shakeService;
    }

    public WxMpMemberCardService getMemberCardService() {
        return this.memberCardService;
    }

    public RequestHttp getRequestHttp() {
        return this;
    }

    public WxMpMassMessageService getMassMessageService() {
        return this.massMessageService;
    }

    public void setKefuService(WxMpKefuService kefuService) {
        this.kefuService = kefuService;
    }

    public void setMaterialService(WxMpMaterialService materialService) {
        this.materialService = materialService;
    }

    public void setMenuService(WxMpMenuService menuService) {
        this.menuService = menuService;
    }

    public void setUserService(WxMpUserService userService) {
        this.userService = userService;
    }

    public void setTagService(WxMpUserTagService tagService) {
        this.tagService = tagService;
    }

    public void setQrCodeService(WxMpQrcodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    public void setCardService(WxMpCardService cardService) {
        this.cardService = cardService;
    }

    public void setStoreService(WxMpStoreService storeService) {
        this.storeService = storeService;
    }

    public void setDataCubeService(WxMpDataCubeService dataCubeService) {
        this.dataCubeService = dataCubeService;
    }

    public void setBlackListService(WxMpUserBlacklistService blackListService) {
        this.blackListService = blackListService;
    }

    public void setTemplateMsgService(WxMpTemplateMsgService templateMsgService) {
        this.templateMsgService = templateMsgService;
    }

    public void setDeviceService(WxMpDeviceService deviceService) {
        this.deviceService = deviceService;
    }

    public void setShakeService(WxMpShakeService shakeService) {
        this.shakeService = shakeService;
    }

    public void setMemberCardService(WxMpMemberCardService memberCardService) {
        this.memberCardService = memberCardService;
    }

    public void setMassMessageService(WxMpMassMessageService massMessageService) {
        this.massMessageService = massMessageService;
    }

    public WxMpAiOpenService getAiOpenService() {
        return this.aiOpenService;
    }

    public void setAiOpenService(WxMpAiOpenService aiOpenService) {
        this.aiOpenService = aiOpenService;
    }

    public WxMpWifiService getWifiService() {
        return this.wifiService;
    }
}
