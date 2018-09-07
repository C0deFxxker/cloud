package com.lyl.study.cloud.gateway.api.dto.request;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author liyilin
 */
@Data
@Accessors(chain = true)
public class RoleSaveForm implements Serializable {
    /**
     * 角色名称
     */
    @NotBlank
    @Length(max = 50)
    private String name;
    /**
     * 角色标识
     */
    @NotBlank
    @Length(max = 50)
    private String sign;
    /**
     * 所属部门ID
     */
    @NotNull
    private Long departmentId;
    /**
     * 是否管理员
     */
    private Boolean administrator;
    /**
     * 是否启用
     */
    @NotNull
    private Boolean enable;
    /**
     * 拥有的菜单权限
     */
    @NotNull
    private List<Long> permissions = Collections.emptyList();
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
