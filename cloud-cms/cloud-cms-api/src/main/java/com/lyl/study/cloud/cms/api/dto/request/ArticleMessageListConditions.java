package com.lyl.study.cloud.cms.api.dto.request;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Data
@ToString
public class ArticleMessageListConditions implements Serializable {
    /**
     * 主题
     */
    private String title;
    /**
     * 作者名称
     */
    private String author;
    /**
     * 是否启用（用于消息撤回）
     */
    private Boolean enable;
    /**
     * 启用时间（用于定时发布）
     */
    private Date enableTimeStart;
    /**
     * 启用时间（用于定时发布）
     */
    private Date enableTimeEnd;
    /**
     * 启用时间（用于定时发布）
     */
    private Date createTimeStart;
    /**
     * 启用时间（用于定时发布）
     */
    private Date createTimeEnd;
    /**
     * 发送状态
     */
    private Integer sendState;
    /**
     * 页码
     */
    private Integer pageIndex = 1;
    /**
     * 页面大小
     */
    private Integer pageSize = 10;
}
