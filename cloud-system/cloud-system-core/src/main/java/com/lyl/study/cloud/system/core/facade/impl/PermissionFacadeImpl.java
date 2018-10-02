package com.lyl.study.cloud.system.core.facade.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.lyl.study.cloud.base.dto.TreeNode;
import com.lyl.study.cloud.base.exception.IllegalOperationException;
import com.lyl.study.cloud.base.exception.InvalidArgumentException;
import com.lyl.study.cloud.base.exception.NoSuchDependentedEntityException;
import com.lyl.study.cloud.base.exception.NoSuchEntityException;
import com.lyl.study.cloud.base.idworker.Sequence;
import com.lyl.study.cloud.base.util.TreeNodeUtils;
import com.lyl.study.cloud.system.api.dto.request.PermissionSaveForm;
import com.lyl.study.cloud.system.api.dto.request.PermissionUpdateForm;
import com.lyl.study.cloud.system.api.dto.response.PermissionDTO;
import com.lyl.study.cloud.system.api.facade.PermissionFacade;
import com.lyl.study.cloud.system.core.entity.Permission;
import com.lyl.study.cloud.system.core.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

import static com.lyl.study.cloud.system.api.SystemErrorCode.BAD_REQUEST;
import static com.lyl.study.cloud.system.api.SystemErrorCode.NOT_FOUND;

@Slf4j
@Service
public class PermissionFacadeImpl implements PermissionFacade {
    private static final int PERMISSION_TYPE_CATALOG = 0;
    private static final int PERMISSION_TYPE_PAGE = 1;
    private static final int PERMISSION_TYPE_REQUEST = 2;

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
    public long save(PermissionSaveForm permissionSaveForm) throws NoSuchDependentedEntityException, InvalidArgumentException {
        log.info("新增授权项：" + permissionSaveForm);
        Integer parentType = null;

        // 判断父授权项是否存在
        Long parentId = permissionSaveForm.getParentId();
        if (parentId != null) {
            Permission parent = permissionService.selectById(parentId);
            if (parent == null) {
                throw new NoSuchDependentedEntityException(NOT_FOUND, "找不到ID为" + parentId + "的父授权项");
            }
            parentType = parent.getType();
        }

        if (!checkPermissionType(permissionSaveForm.getType(), parentType)) {
            throw new InvalidArgumentException(BAD_REQUEST, "非法权限类型");
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
     * @param currentType 当前授权项权限类型
     * @param parentType  父级授权项权限类型
     * @return
     */
    private boolean checkPermissionType(int currentType, Integer parentType) {
        if (currentType == PERMISSION_TYPE_CATALOG) {
            // 目录的父级可以为空或是一个目录
            return parentType == null || parentType == PERMISSION_TYPE_CATALOG;
        } else if (currentType == PERMISSION_TYPE_PAGE) {
            // 页面的父级可以为空或是一个目录
            return parentType == null || parentType == PERMISSION_TYPE_CATALOG;
        } else if (currentType == PERMISSION_TYPE_REQUEST) {
            // 请求权限的父级只能是目录或页面
            return parentType != null && parentType != PERMISSION_TYPE_REQUEST;
        } else {
            return false;
        }
    }

    @Override
    public void update(PermissionUpdateForm permissionUpdateForm) throws NoSuchEntityException {
        log.info("修改授权项：" + permissionUpdateForm);

        Long id = permissionUpdateForm.getId();
        Assert.notNull(id, "授权项ID不能为空");
        Permission record = permissionService.selectById(permissionUpdateForm.getId());
        if (record == null) {
            throw new NoSuchEntityException(NOT_FOUND, "找不到ID为" + id + "的授权项");
        }
        BeanUtils.copyProperties(permissionUpdateForm, record);
        record.setUpdateTime(null);
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
    public int deleteById(long id, boolean force) throws NoSuchEntityException, IllegalOperationException {
        Permission record = permissionService.selectById(id);
        if (record == null) {
            throw new NoSuchEntityException(NOT_FOUND, "找不到ID为" + id + "的授权项");
        }

        return permissionService.deleteById(id, force);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<TreeNode<PermissionDTO>> tree() {
        List<Permission> allPermission = permissionService.selectList(
                new EntityWrapper<Permission>().orderBy(Permission.SORT, true)
        );
        List<PermissionDTO> dtoList = allPermission.stream().map(entity -> {
            PermissionDTO dto = new PermissionDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }).collect(Collectors.toList());
        return TreeNodeUtils.buildTree(dtoList, null, config);
    }
}
