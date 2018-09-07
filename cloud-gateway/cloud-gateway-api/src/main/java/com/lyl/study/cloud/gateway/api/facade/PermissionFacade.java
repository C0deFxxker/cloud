package com.lyl.study.cloud.gateway.api.facade;

import com.lyl.study.cloud.base.dto.TreeNode;
import com.lyl.study.cloud.base.exception.NoSuchEntityException;
import com.lyl.study.cloud.gateway.api.dto.request.PermissionSaveForm;
import com.lyl.study.cloud.gateway.api.dto.request.PermissionUpdateForm;
import com.lyl.study.cloud.gateway.api.dto.response.PermissionDTO;

import java.util.List;

public interface PermissionFacade {
    /**
     * 新增授权项
     *
     * @param permissionSaveForm 表单
     * @return 授权项ID
     */
    long save(PermissionSaveForm permissionSaveForm);

    /**
     * 修改授权项
     *
     * @param permissionUpdateForm 表单
     */
    void update(PermissionUpdateForm permissionUpdateForm) throws NoSuchEntityException;

    /**
     * 根据ID获取授权项
     *
     * @param id 授权项ID
     * @return 返回对应ID的授权项信息；找不到授权项时，返回null
     */
    PermissionDTO getById(long id);

    /**
     * 删除授权项（级联删除角色授权项关联关系）
     *
     * @param id    授权项ID
     * @param force 是否强制删除：如果是，则连同子授权项一并删除；否则，检查到该授权项有子授权项时，抛出异常
     * @return 删除记录数
     * @throws IllegalAccessError 指定不强制删除的情况下，检查到授权项有子菜单时抛出此异常
     */
    int deleteById(long id, boolean force);

    /**
     * 展示整个菜单树
     *
     * @return 菜单树信息
     */
    List<TreeNode<PermissionDTO>> tree();

    /**
     * 获取角色授权的授权项
     *
     * @param roleId 角色ID
     * @return 角色授权的授权项
     * @throws NoSuchEntityException 找不到角色时，抛出此异常
     */
//    List<PermissionDTO> getPermissionByRoleId(long roleId) throws NoSuchEntityException;
}
