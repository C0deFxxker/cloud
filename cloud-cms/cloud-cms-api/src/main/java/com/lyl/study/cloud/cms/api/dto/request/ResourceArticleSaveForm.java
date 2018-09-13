package com.lyl.study.cloud.cms.api.dto.request;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author liyilin
 */
@Data
@ToString
@Accessors(chain = true)
public class ResourceArticleSaveForm implements Serializable {
    /**
     * 主题
     */
    @NotBlank
    @Length(max = 100)
    private String title;
    /**
     * 作者名称
     */
    @NotBlank
    @Length(max = 32)
    private String author;
    /**
     * 文本摘要
     */
    @NotBlank
    @Length(max = 255)
    private String abstractText;
    /**
     * 文章内容
     */
    @NotNull
    @Length(max = 16777215)
    private String content;
    /**
     * 封面图链接url
     */
    @NotBlank
    @Length(max = 255)
    private String surfaceUrl;
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
}
