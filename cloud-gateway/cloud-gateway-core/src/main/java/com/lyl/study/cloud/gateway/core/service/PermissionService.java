package com.lyl.study.cloud.gateway.core.service;

import com.lyl.study.cloud.gateway.core.entity.Permission;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author liyilin
 * @since 2018-09-07
 */
public interface PermissionService extends IService<Permission> {
    /**
     * 删除授权项（级联删除角色授权项关联关系）
     *
     * @param id    授权项ID
     * @param force 是否强制删除：如果是，则连同子授权项一并删除；否则，检查到该授权项有子授权项时，抛出异常
     * @return 删除记录数
     * @throws IllegalAccessError 指定不强制删除的情况下，检查到授权项有子菜单时抛出此异常
     */
    int deleteById(long id, boolean force) throws IllegalAccessError;
}
