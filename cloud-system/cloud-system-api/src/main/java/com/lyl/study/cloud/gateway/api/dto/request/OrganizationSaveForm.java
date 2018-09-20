package com.lyl.study.cloud.gateway.api.dto.request;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ToString
@Accessors(chain = true)
public class OrganizationSaveForm implements Serializable {
    /**
     * 上级组织ID
     */
    private Long parentId;
    /**
     * 组织名称
     */
    @NotBlank
    @Length(max = 50)
    private String name;
    /**
     * 描述
     */
    private String description;
    /**
     * 排序（默认为0）
     */
    private Integer sort = 0;
    /**
     * 创建者ID
     */
    private Long creatorId;
    /**
     * 拥有者ID
     */
    private Long ownerId;
    /**
     * 拥有者角色ID
     */
    private Long ownerRoleId;
}
