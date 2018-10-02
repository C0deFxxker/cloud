package com.lyl.study.cloud.system.api.facade;

import com.lyl.study.cloud.base.dto.PageInfo;
import com.lyl.study.cloud.base.exception.NoSuchEntityException;
import com.lyl.study.cloud.system.api.dto.request.RoleSaveForm;
import com.lyl.study.cloud.system.api.dto.request.RoleUpdateForm;
import com.lyl.study.cloud.system.api.dto.response.RoleDTO;

public interface RoleFacade {
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
     * 根据ID获取角色信息
     *
     * @param roleId 角色ID
     * @return 返回对应ID的角色信息；找不到角色时，返回null
     */
    RoleDTO getById(long roleId);

    /**
     * 角色列表查询（这里不会展示每个角色的授权项）
     *
     * @param organizationId 筛选组织（可选）
     * @param pageIndex      页码
     * @param pageSize       页面大小
     * @return 角色信息列表
     */
    PageInfo<RoleDTO> list(Long organizationId, Integer pageIndex, Integer pageSize);
}
