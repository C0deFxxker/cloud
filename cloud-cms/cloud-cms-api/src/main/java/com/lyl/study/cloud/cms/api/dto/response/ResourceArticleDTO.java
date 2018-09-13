package com.lyl.study.cloud.cms.api.dto.response;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 图文资源
 * </p>
 *
 * @author liyilin
 * @since 2018-09-13
 */
@Data
@ToString
@Accessors(chain = true)
public class ResourceArticleDTO implements Serializable {
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
     * 逻辑删除字段
     */
    private Boolean deleted;
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
