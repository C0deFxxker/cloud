package com.lyl.study.cloud.system.core.mapper;

import com.lyl.study.cloud.system.core.entity.Permission;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author liyilin
 * @since 2018-09-07
 */
public interface PermissionMapper extends BaseMapper<Permission> {
    /**
     * 根据授权项ID删除角色授权关联关系
     *
     * @param permissionId 授权项ID
     * @return 成功删除行数
     */
    int deleteRolePermissionsByPermissionId(Long permissionId);
}
