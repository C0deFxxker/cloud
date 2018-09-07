package com.lyl.study.cloud.gateway.core.mapper;

import com.lyl.study.cloud.gateway.core.entity.Role;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.lyl.study.cloud.security.api.dto.response.RoleDTO;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author liyilin
 * @since 2018-09-07
 */
public interface RoleMapper extends BaseMapper<Role> {
    /**
     * 获取指定角色的授权项
     *
     * @param roleId 角色ID
     * @return 指定角色的授权项
     */
    List<RoleDTO.PermissionItem> selectPermissionByRoleId(Long roleId);

    /**
     * 为角色添加授权项
     *
     * @param roleId      角色ID
     * @param permissions 授权项ID集
     * @return 成功添加行数
     */
    int insertRolePermissions(Long roleId, Collection<Long> permissions);

    /**
     * 删除角色的全部授权项
     *
     * @param roleId 角色ID
     * @return 成功删除行数
     */
    int deleteRolePermissionsByRoleId(Long roleId);
}
