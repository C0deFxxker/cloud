package com.lyl.study.cloud.gateway.core.service;

import com.lyl.study.cloud.gateway.core.entity.Role;
import com.baomidou.mybatisplus.service.IService;
import com.lyl.study.cloud.security.api.dto.request.RoleSaveForm;
import com.lyl.study.cloud.security.api.dto.request.RoleUpdateForm;
import com.lyl.study.cloud.security.api.dto.response.RoleDTO;

import java.util.List;

/**
 * @author liyilin
 * @since 2018-09-07
 */
public interface RoleService extends IService<Role> {
    /**
     * 新建角色信息
     *
     * @param roleSaveForm 表单
     * @return 角色ID
     */
    long save(RoleSaveForm roleSaveForm);

    /**
     * 修改角色信息
     *
     * @param roleUpdateForm 表单
     */
    void update(RoleUpdateForm roleUpdateForm);

    /**
     * 删除角色（级联删除角色授权项关联关系）
     *
     * @param id 角色ID
     */
    int deleteById(long id);

    /**
     * 获取指定角色的授权项
     *
     * @param roleId 角色ID
     * @return 指定角色的授权项
     */
    List<RoleDTO.PermissionItem> getPermissionByRoleId(Long roleId);
}
