package com.lyl.study.cloud.gateway.core.service;

import com.baomidou.mybatisplus.service.IService;
import com.lyl.study.cloud.base.exception.NoSuchDependentedEntityException;
import com.lyl.study.cloud.base.exception.NoSuchEntityException;
import com.lyl.study.cloud.gateway.api.dto.request.RoleSaveForm;
import com.lyl.study.cloud.gateway.api.dto.request.RoleUpdateForm;
import com.lyl.study.cloud.gateway.api.dto.response.PermissionItem;
import com.lyl.study.cloud.gateway.core.entity.Role;

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
     * @throws NoSuchDependentedEntityException 找不到菜单项
     */
    long save(RoleSaveForm roleSaveForm) throws NoSuchDependentedEntityException;

    /**
     * 修改角色信息
     *
     * @param roleUpdateForm 表单
     * @throws NoSuchEntityException 找不到角色
     */
    void update(RoleUpdateForm roleUpdateForm) throws NoSuchEntityException;

    /**
     * 删除角色（级联删除角色授权项关联关系）
     *
     * @param id 角色ID
     */
    void deleteById(long id) throws NoSuchEntityException;

    /**
     * 获取指定角色的授权项
     *
     * @param roleId 角色ID
     * @return 指定角色的授权项
     */
    List<PermissionItem> getPermissionByRoleId(Long roleId);
}
