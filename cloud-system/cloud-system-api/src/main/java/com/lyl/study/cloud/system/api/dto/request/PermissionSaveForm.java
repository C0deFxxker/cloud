package com.lyl.study.cloud.system.api.dto.request;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author liyilin
 */
@Data
@Accessors(chain = true)
public class PermissionSaveForm implements Serializable {
    /**
     * 父菜单ID，不允许修改父节点
     */
    private Long parentId;
    /**
     * 菜单名称
     */
    @NotBlank
    @Length(max = 50)
    private String label;
    /**
     * 权限： URL（菜单类型） 或 请求方法标识（按钮/请求类型）
     */
    @Length(max = 255)
    private String sign;
    /**
     * 类型   0：目录   1：菜单   2：按钮/请求
     */
    @NotNull
    @Min(value = 0, message = "菜单类型只能是 0：目录, 1：菜单, 2：按钮/请求")
    @Max(value = 2, message = "菜单类型只能是 0：目录, 1：菜单, 2：按钮/请求")
    private Integer type;
    /**
     * 菜单图标
     */
    @Length(max = 255)
    private String icon;
    /**
     * 是否启用
     */
    @NotNull
    private Boolean enable;
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
