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
        newRouter.rule().handler((wxMessage, context, mpService, sessionManager) -> {
            log.info(wxMessage.getContent());
            return WxMpXmlOutMessage.TEXT().content(wxMessage.getContent()).build();
        }).end();

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
//        newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
//                .event(MenuButtonType.CLICK).handler(this.menuHandler).end();

        // 点击菜单连接事件
//        newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
//                .event(MenuButtonType.VIEW).handler(this.nullHandler).end();

        // 关注事件
//        newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
//                .event(EventType.SUBSCRIBE).handler(this.subscribeHandler)
//                .end();

        // 取消关注事件
//        newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
//                .event(EventType.UNSUBSCRIBE)
//                .handler(this.unsubscribeHandler).end();

        // 上报地理位置事件
//        newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
//                .event(EventType.LOCATION).handler(this.locationHandler)
//                .end();

        // 接收地理位置消息
//        newRouter.rule().async(false).msgType(XmlMsgType.LOCATION)
//                .handler(this.locationHandler).end();

        // 扫码事件
//        newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
//                .event(EventType.SCAN).handler(this.nullHandler).end();

        // 默认
//        newRouter.rule().async(false).handler(this.msgHandler).end();

        return newRouter;
    }
}
