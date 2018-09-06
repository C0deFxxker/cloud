package com.lyl.study.cloud.security.api.facade;

import com.lyl.study.cloud.base.dto.TreeNode;
import com.lyl.study.cloud.base.exception.NoSuchEntityException;
import com.lyl.study.cloud.security.api.dto.request.PermissionSaveForm;
import com.lyl.study.cloud.security.api.dto.request.PermissionUpdateForm;
import com.lyl.study.cloud.security.api.dto.response.PermissionDTO;

import java.util.List;

public interface PermissionFacade {
    /**
     * 新增菜单项
     *
     * @param permissionSaveForm 表单
     * @return 菜单项ID
     */
    long save(PermissionSaveForm permissionSaveForm);

    /**
     * 修改菜单项
     *
     * @param permissionUpdateForm 表单
     */
    void update(PermissionUpdateForm permissionUpdateForm) throws NoSuchEntityException;

    /**
     * 根据ID获取菜单项
     *
     * @param id 菜单项ID
     * @return 返回对应ID的菜单项信息；找不到菜单项时，返回null
     */
    PermissionDTO getById(long id);

    /**
     * 删除菜单项
     *
     * @param id    菜单项ID
     * @param force 是否强制删除：如果是，则连同子菜单项一并删除；否则，检查到该菜单项有子菜单项时，抛出异常
     * @return 删除记录数
     * @throws IllegalAccessError 指定不强制删除的情况下，检查到菜单项有子菜单时抛出此异常
     */
    int deleteById(long id, boolean force);

    /**
     * 展示整个菜单树
     *
     * @return 菜单树信息
     */
    List<TreeNode<PermissionDTO>> tree();

    /**
     * 获取角色授权的菜单项
     *
     * @param roleId 角色ID
     * @return 角色授权的菜单项
     * @throws NoSuchEntityException 找不到角色时，抛出此异常
     */
    List<PermissionDTO> getPermissionByRoleId(long roleId) throws NoSuchEntityException;
}
