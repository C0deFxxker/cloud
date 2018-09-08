package com.lyl.study.cloud.gateway.core.facade.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.lyl.study.cloud.base.dto.TreeNode;
import com.lyl.study.cloud.base.exception.NoSuchDependentedEntityException;
import com.lyl.study.cloud.base.exception.NoSuchEntityException;
import com.lyl.study.cloud.base.idworker.Sequence;
import com.lyl.study.cloud.base.util.TreeNodeUtils;
import com.lyl.study.cloud.gateway.core.entity.Permission;
import com.lyl.study.cloud.gateway.core.service.PermissionService;
import com.lyl.study.cloud.gateway.api.dto.request.PermissionSaveForm;
import com.lyl.study.cloud.gateway.api.dto.request.PermissionUpdateForm;
import com.lyl.study.cloud.gateway.api.dto.response.PermissionDTO;
import com.lyl.study.cloud.gateway.api.facade.PermissionFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionFacadeImpl implements PermissionFacade {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TreeNodeUtils.BuildTreeConfig<PermissionDTO> config;

    public PermissionFacadeImpl() {
        config = new TreeNodeUtils.BuildTreeConfig<>();
        config.setIdGetter(PermissionDTO::getId);
        config.setLabelGetter(PermissionDTO::getLabel);
        config.setParentIdGetter(PermissionDTO::getParentId);
    }

    @Autowired
    private PermissionService permissionService;
    @Autowired
    private Sequence sequence;

    @Override
    public long save(PermissionSaveForm permissionSaveForm) throws NoSuchDependentedEntityException, IllegalArgumentException {
        logger.info("新增授权项：" + permissionSaveForm);
        Integer parentType = null;

        // 判断父菜单是否存在
        Long parentId = permissionSaveForm.getParentId();
        if (parentId != null) {
            Permission parent = permissionService.selectById(parentId);
            if (parent == null) {
                throw new NoSuchDependentedEntityException("找不到ID为" + parentId + "的父授权项");
            }
            parentType = parent.getType();
        }

        if (!checkPermissionType(permissionSaveForm.getType(), parentType)) {
            throw new IllegalArgumentException("非法权限类型");
        }

        Permission record = new Permission();
        BeanUtils.copyProperties(permissionSaveForm, record);
        record.setId(sequence.nextId());
        permissionService.insert(record);
        return record.getId();
    }

    /**
     * 校验权限类型是否合法
     *
     * @param currentType 当前菜单权限类型
     * @param parentType  父级菜单权限类型
     * @return
     */
    private boolean checkPermissionType(int currentType, Integer parentType) {
        if (currentType == 0) {
            // 目录的父级可以为空或是一个目录
            return parentType == null || parentType == 0;
        } else if (currentType == 1) {
            // 页面的父级可以为空或是一个目录
            return parentType == null || parentType == 0;
        } else if (currentType == 2) {
            // 请求权限的父级只能是目录或页面
            return parentType != null && parentType != 2;
        } else {
            throw new IllegalArgumentException("Permission type no support.");
        }
    }

    @Override
    public void update(PermissionUpdateForm permissionUpdateForm) throws NoSuchEntityException {
        logger.info("修改授权项：" + permissionUpdateForm);

        Long id = permissionUpdateForm.getId();
        Assert.notNull(id, "菜单ID不能为空");
        Permission record = permissionService.selectById(permissionUpdateForm.getId());
        BeanUtils.copyProperties(permissionUpdateForm, record);

        permissionService.updateById(record);
    }

    @Override
    public PermissionDTO getById(long id) {
        Permission permission = permissionService.selectById(id);
        PermissionDTO dto = null;
        if (permission != null) {
            dto = new PermissionDTO();
            BeanUtils.copyProperties(permission, dto);
        }
        return dto;
    }

    @Override
    public int deleteById(long id, boolean force) throws NoSuchEntityException, IllegalAccessError {
        Permission record = permissionService.selectById(id);
        if (record == null) {
            throw new NoSuchEntityException("找不到ID为" + id + "的授权项");
        }

        return permissionService.deleteById(id, force);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<TreeNode<PermissionDTO>> tree() {
        List<Permission> allPermission = permissionService.selectList(new EntityWrapper<>());
        List<PermissionDTO> dtoList = allPermission.stream().map(entity -> {
            PermissionDTO dto = new PermissionDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }).collect(Collectors.toList());
        return TreeNodeUtils.buildTree(dtoList, null, config);
    }
}
