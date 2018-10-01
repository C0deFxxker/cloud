package com.lyl.study.cloud.wechat.core.config;

import com.lyl.study.cloud.wechat.core.service.MultiWxService;
import com.lyl.study.cloud.wechat.core.service.impl.MultiWxServiceImpl;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.chanjar.weixin.common.api.WxConsts.*;

@Slf4j
@Configuration
@EnableConfigurationProperties(MultiWxMpProperties.class)
public class WxMpConfigurer {
    @Autowired
    private MultiWxMpProperties multiWxMpProperties;

    @Bean
    public MultiWxService multiWxMpService() {
        List<WxMpConfigStorage> wxMpConfigStorages = multiWxMpProperties.listWechatMpProperties();
        Map<String, WxMpService> wxMpServiceMap = new HashMap<>(wxMpConfigStorages.size());
        Map<String, WxMpMessageRouter> wxMpMessageRouterMap = new HashMap<>();

        for (WxMpConfigStorage wxMpConfigStorage : wxMpConfigStorages) {
            // 初始化公众号服务
            WxMpService wxMpService = new me.chanjar.weixin.mp.api.impl.WxMpServiceImpl();
            wxMpService.setWxMpConfigStorage(wxMpConfigStorage);
            wxMpServiceMap.put(wxMpConfigStorage.getAppId(), wxMpService);

            // 事件处理器
            final WxMpMessageRouter router = newRouter(wxMpService);
            wxMpMessageRouterMap.put(wxMpConfigStorage.getAppId(), router);
        }

        return new MultiWxServiceImpl(wxMpServiceMap, wxMpMessageRouterMap);
    }

    private WxMpMessageRouter newRouter(WxMpService wxMpService) {
        final WxMpMessageRouter newRouter = new WxMpMessageRouter(wxMpService);

        // 记录所有事件的日志 （异步执行）
//        newRouter.rule().handler((wxMessage, context, mpService, sessionManager) -> {
//            log.info(wxMessage.toString());
//            return WxMpXmlOutMessage.TEXT().build();
//        }).end();

        // 接收客服会话管理事件
//        newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
//                .event(WxMpEventConstants.CustomerService.KF_CREATE_SESSION)
//                .handler(this.kfSessionHandler).end();
//        newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
//                .event(WxMpEventConstants.CustomerService.KF_CLOSE_SESSION)
//                .handler(this.kfSessionHandler)
//                .end();
//        newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
//                .event(WxMpEventConstants.CustomerService.KF_SWITCH_SESSION)
//                .handler(this.kfSessionHandler).end();

        // 门店审核事件
//        newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
//                .event(WxMpEventConstants.POI_CHECK_NOTIFY)
//                .handler(this.storeCheckNotifyHandler).end();

        // 自定义菜单事件
        newRouter.rule().msgType(XmlMsgType.EVENT)
                .event(MenuButtonType.CLICK)
                .handler((wxMessage, context, mpService, sessionManager) -> {
                    log.info("点击菜单拉取消息事件: OpenID={}, MenuId={}, EventKey={}",
                            wxMessage.getFromUser(), wxMessage.getMenuId(), wxMessage.getEventKey());
                    return null;
                }).end();

        // 点击菜单连接事件
        newRouter.rule().msgType(XmlMsgType.EVENT)
                .event(MenuButtonType.VIEW)
                .handler((wxMessage, context, mpService, sessionManager) -> {
                    log.info("点击菜单跳转链接事件: OpenID={}, MenuId={}, EventKey={}",
                            wxMessage.getFromUser(), wxMessage.getMenuId(), wxMessage.getEventKey());
                    return null;
                }).end();
        ;

        // 关注事件
        newRouter.rule().msgType(XmlMsgType.EVENT)
                .event(EventType.SUBSCRIBE)
                .handler((wxMessage, context, mpService, sessionManager) -> {
                    log.info("关注事件: OpenID={}", wxMessage.getFromUser());
                    return null;
                }).end();

        // 取消关注事件
        newRouter.rule().msgType(XmlMsgType.EVENT)
                .event(EventType.UNSUBSCRIBE)
                .handler((wxMessage, context, mpService, sessionManager) -> {
                    log.info("取消关注事件: OpenID={}", wxMessage.getFromUser());
                    return null;
                }).end();

        // 上报地理位置事件
        newRouter.rule().msgType(XmlMsgType.EVENT)
                .event(EventType.LOCATION)
                .handler((wxMessage, context, mpService, sessionManager) -> {
                    log.info("上报地理位置事件: OpenID={}, Latitude={}, Longitude={}, Precision={}",
                            wxMessage.getFromUser(), wxMessage.getLatitude(), wxMessage.getLongitude(), wxMessage.getPrecision());
                    return null;
                }).end();

        // 接收地理位置消息
//        newRouter.rule().async(false).msgType(XmlMsgType.LOCATION)
//                .handler(this.locationHandler).end();

        // 扫码事件
        newRouter.rule().msgType(XmlMsgType.EVENT)
                .event(EventType.SCAN)
                .handler((wxMessage, context, mpService, sessionManager) -> {
                    log.info("扫码事件: OpenID={}, scene_id={}, ticket={}",
                            wxMessage.getFromUser(), wxMessage.getEventKey(), wxMessage.getTicket());
                    return null;
                }).end();

        // 自动回复
        newRouter.rule().async(false).msgType(XmlMsgType.TEXT)
                .handler((wxMessage, context, mpService, sessionManager) -> {
                    String msg = wxMessage.getContent();
                    WxMpXmlOutMessage outMessage;
                    if (msg.equals("你好")) {
                        outMessage = WxMpXmlOutMessage.TEXT().content("叫爸爸").build();
                    } else if (msg.equals("爸爸")) {
                        outMessage = WxMpXmlOutMessage.TEXT().content("儿子乖～").build();
                    } else if (msg.equals("我想看个图片")) {
                        outMessage = WxMpXmlOutMessage.IMAGE().mediaId("SeyMl98FOl4NN4CMBFX_Ej2I_INwh__yMMYMoPnTSfo").build();
                    } else {
                        outMessage = WxMpXmlOutMessage.TEXT().content("不知道你在说什么").build();
                    }
                    outMessage.setFromUserName(wxMessage.getToUser());
                    outMessage.setToUserName(wxMessage.getFromUser());
                    return outMessage;
                }).end();

        return newRouter;
    }
}
