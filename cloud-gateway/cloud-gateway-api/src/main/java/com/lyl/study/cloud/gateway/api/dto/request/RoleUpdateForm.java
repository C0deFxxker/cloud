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
public class RoleUpdateForm implements Serializable {
    /**
     * 主键
     */
    @NotNull
    private Long id;
    /**
     * 角色名称
     */
    @NotBlank
    @Length(max = 50)
    private String name;
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
     * 拥有者ID
     */
    private Long ownerId;
    /**
     * 拥有者角色ID
     */
    private Long ownerRoleId;
}
