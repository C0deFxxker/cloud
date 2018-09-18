package com.lyl.study.cloud.admin.web.controller;

import com.lyl.study.cloud.base.dto.Result;
import org.springframework.web.bind.annotation.RestController;

/**
 * 消息中心控制器
 */
@RestController
public class MessageController {
    public Result sendP2pSms() {
        return null;
    }

    public Result sendBatchSms() {
        return null;
    }

    public Result sendP2pWebMessage() {
        return null;
    }

    public Result sendBatchWebMessage() {
        return null;
    }

    /**
     * 获取消息详情
     *
     * @param id
     * @return
     */
    public Result getMessageDetail(String id) {
        return null;
    }

    /**
     * 获取消息状态列表
     *
     * @return
     */
    public Result getMessageStateList() {
        return null;
    }
}
