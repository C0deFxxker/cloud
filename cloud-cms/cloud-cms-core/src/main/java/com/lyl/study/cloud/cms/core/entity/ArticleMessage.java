package com.lyl.study.cloud.cms.core.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 图文消息（已经发送出去的）
 * </p>
 *
 * @author liyilin
 * @since 2018-09-13
 */
@Data
@Accessors(chain = true)
@TableName("cms_article_message")
public class ArticleMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.INPUT)
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


    public static final String ID = "id";

    public static final String TITLE = "title";

    public static final String AUTHOR = "author";

    public static final String ABSTRACT_TEXT = "abstract_text";

    public static final String CONTENT = "content";

    public static final String SURFACE_URL = "surface_url";

    public static final String ENABLE = "enable";

    public static final String ENABLE_TIME = "enable_time";

    public static final String CREATOR_ID = "creator_id";

    public static final String OWNER_ID = "owner_id";

    public static final String OWNER_ROLE_ID = "owner_role_id";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

}
