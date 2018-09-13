package com.lyl.study.cloud.gateway.core.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.lyl.study.cloud.base.exception.NoSuchEntityException;
import com.lyl.study.cloud.base.idworker.Sequence;
import com.lyl.study.cloud.gateway.api.GatewayErrorCode;
import com.lyl.study.cloud.gateway.api.dto.request.RoleSaveForm;
import com.lyl.study.cloud.gateway.api.dto.request.RoleUpdateForm;
import com.lyl.study.cloud.gateway.api.dto.response.PermissionItem;
import com.lyl.study.cloud.gateway.core.entity.Role;
import com.lyl.study.cloud.gateway.core.mapper.PermissionMapper;
import com.lyl.study.cloud.gateway.core.mapper.RoleMapper;
import com.lyl.study.cloud.gateway.core.service.RoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author liyilin
 * @since 2018-09-07
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    @Autowired
    private Sequence sequence;
    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    @Transactional
    public long save(RoleSaveForm form) {
        Role record = new Role();
        BeanUtils.copyProperties(form, record);
        record.setId(sequence.nextId());
        baseMapper.insert(record);
        baseMapper.insertRolePermissions(record.getId(), form.getPermissions());
        return record.getId();
    }

    @Override
    public void update(RoleUpdateForm form) throws NoSuchEntityException {
        Long roleId = form.getId();
        Assert.notNull(roleId, "id cannot be null");
        Role record = baseMapper.selectById(roleId);
        if (record == null) {
            throw new NoSuchEntityException(GatewayErrorCode.NOT_FOUND, "找不到ID为" + roleId + "的角色");
        }

        BeanUtils.copyProperties(form, record);
        record.setCreateTime(null);
        record.setUpdateTime(null);
        baseMapper.updateById(record);

        // 处理角色授权项关联关系的修改
        List<PermissionItem> permissions = baseMapper.selectPermissionByRoleId(roleId);
        Set<Long> permIdSet = permissions.stream().map(PermissionItem::getId).collect(Collectors.toSet());
        HashSet<Long> formPermIdSet = new HashSet<>(form.getPermissions());
        if (!permIdSet.equals(formPermIdSet)) {
            // 校验授权项是否存在
//            if (!formPermIdSet.isEmpty()) {
//                List<Permission> records = permissionMapper.selectBatchIds(formPermIdSet);
//                if (records.size() != formPermIdSet.size()) {
//                    throw new NoSuchDependentedEntityException("找不到相关授权项");
//                }
//            }

            baseMapper.deleteRolePermissionsByRoleId(roleId);
            baseMapper.insertRolePermissions(roleId, formPermIdSet);
        }
    }

    @Override
    @Transactional
    public void deleteById(long id) throws NoSuchEntityException {
        // 删除角色记录
        int rows = baseMapper.deleteById(id);
        if (rows != 0) {
            // 删除角色授权关联关系
            baseMapper.deleteRolePermissionsByRoleId(id);
            // 删除用户角色关联关系
            baseMapper.deleteUserRolesByRoleId(id);
        } else {
            throw new NoSuchEntityException("找不到ID为" + id + "的角色");
        }
    }

    @Override
    public List<PermissionItem> getPermissionByRoleId(Long roleId) {
        return baseMapper.selectPermissionByRoleId(roleId);
    }
}
