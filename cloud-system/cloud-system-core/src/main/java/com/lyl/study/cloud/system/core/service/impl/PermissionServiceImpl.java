package com.lyl.study.cloud.system.core.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.lyl.study.cloud.base.dto.TreeNode;
import com.lyl.study.cloud.base.exception.IllegalOperationException;
import com.lyl.study.cloud.base.util.TreeNodeUtils;
import com.lyl.study.cloud.system.core.entity.Permission;
import com.lyl.study.cloud.system.core.mapper.PermissionMapper;
import com.lyl.study.cloud.system.core.service.PermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.lyl.study.cloud.base.CommonErrorCode.BAD_REQUEST;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author liyilin
 * @since 2018-09-07
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {
    private final TreeNodeUtils.BuildTreeConfig<Permission> config;

    public PermissionServiceImpl() {
        config = new TreeNodeUtils.BuildTreeConfig<>();
        config.setIdGetter(Permission::getId);
        config.setLabelGetter(Permission::getLabel);
        config.setParentIdGetter(Permission::getParentId);
    }

    @Override
    @Transactional
    public int deleteById(long id, boolean force) throws IllegalOperationException {
        // 非强制删除模式
        if (!force) {
            int numOfChild = baseMapper.selectCount(new EntityWrapper<Permission>().eq(Permission.PARENT_ID, id));
            if (numOfChild > 0) {
                throw new IllegalOperationException(BAD_REQUEST, "该授权项下还有子授权项");
            }
            int rows = baseMapper.deleteById(id);
            baseMapper.deleteRolePermissionsByPermissionId(id);
            return rows;
        }
        //强制删除模式
        else {
            // TODO 待优化
            List<Permission> allPermission = baseMapper.selectList(new EntityWrapper<>());
            List<TreeNode<Permission>> trees = TreeNodeUtils.buildTree(allPermission, id, config);

            final List<Long> deletedSet = new ArrayList<>();
            TreeNode<Permission> root = trees.get(0);
            TreeNodeUtils.bfsWalker(root, ((node, parent, deep) -> {
                deletedSet.add((Long) node.getId());
                baseMapper.deleteRolePermissionsByPermissionId((Long) node.getId());
            }));
            return baseMapper.deleteBatchIds(deletedSet);
        }
    }
}
