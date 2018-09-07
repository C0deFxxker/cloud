package com.lyl.study.cloud.security.api.facade;

import com.lyl.study.cloud.base.dto.PageInfo;
import com.lyl.study.cloud.security.api.dto.request.RoleSaveForm;
import com.lyl.study.cloud.security.api.dto.request.RoleUpdateForm;
import com.lyl.study.cloud.security.api.dto.response.RoleDTO;

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
     */
    void update(RoleUpdateForm roleUpdateForm);

    /**
     * 删除角色（级联删除角色授权项关联关系）
     *
     * @param id 角色ID
     */
    int deleteById(long id);

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
     * @param departmentId 筛选部门（可选）
     * @param pageIndex    页码
     * @param pageSize     页面大小
     * @return 角色信息列表
     */
    PageInfo<RoleDTO> list(Long departmentId, Integer pageIndex, Integer pageSize);
}
