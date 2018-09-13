package com.lyl.study.cloud.cms.api.dto.response;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 图文消息（已经发送出去的）
 * </p>
 *
 * @author liyilin
 * @since 2018-09-13
 */
@Data
@ToString
@Accessors(chain = true)
public class ArticleMessageDTO implements Serializable {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 主题
     */
    private String title;
    /**
     * 作者名称
     */
    private String author;
    /**
     * 文本摘要
     */
    private String abstractText;
    /**
     * 文章内容
     */
    private String content;
    /**
     * 封面图链接url
     */
    private String surfaceUrl;
    /**
     * 是否启用（用于消息撤回）
     */
    private Boolean enable;
    /**
     * 启用时间（用于定时发布）
     */
    private Date enableTime;
    /**
     * 创建者id
     */
    private Long creatorId;
    /**
     * 拥有者id，默认为创建者id
     */
    private Long ownerId;
    /**
     * 拥有者角色id
     */
    private Long ownerRoleId;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date updateTime;

}
