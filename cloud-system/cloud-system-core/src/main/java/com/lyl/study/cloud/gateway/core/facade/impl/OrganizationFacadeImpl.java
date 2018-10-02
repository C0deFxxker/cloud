package com.lyl.study.cloud.gateway.core.facade.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.lyl.study.cloud.base.dto.TreeNode;
import com.lyl.study.cloud.base.exception.IllegalOperationException;
import com.lyl.study.cloud.base.exception.NoSuchDependentedEntityException;
import com.lyl.study.cloud.base.exception.NoSuchEntityException;
import com.lyl.study.cloud.base.idworker.Sequence;
import com.lyl.study.cloud.base.util.TreeNodeUtils;
import com.lyl.study.cloud.gateway.api.dto.request.OrganizationSaveForm;
import com.lyl.study.cloud.gateway.api.dto.request.OrganizationUpdateForm;
import com.lyl.study.cloud.gateway.api.dto.response.OrganizationDTO;
import com.lyl.study.cloud.gateway.api.facade.OrganizationFacade;
import com.lyl.study.cloud.gateway.core.entity.Organization;
import com.lyl.study.cloud.gateway.core.entity.Role;
import com.lyl.study.cloud.gateway.core.service.OrganizationService;
import com.lyl.study.cloud.gateway.core.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.lyl.study.cloud.gateway.api.SystemErrorCode.DEPARTMENT_DELETE_FAILED;
import static com.lyl.study.cloud.gateway.api.SystemErrorCode.NOT_FOUND;

/**
 * @author liyilin
 */
@Slf4j
@Service
public class OrganizationFacadeImpl implements OrganizationFacade {
    private final TreeNodeUtils.BuildTreeConfig<OrganizationDTO> config;

    @Autowired
    private RoleService roleService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private Sequence sequence;

    public OrganizationFacadeImpl() {
        config = new TreeNodeUtils.BuildTreeConfig<>();
        config.setIdGetter(OrganizationDTO::getId);
        config.setLabelGetter(OrganizationDTO::getName);
        config.setParentIdGetter(OrganizationDTO::getParentId);
    }

    @Override
    public long save(OrganizationSaveForm organizationSaveForm) throws NoSuchDependentedEntityException {
        log.info("新增组织：" + organizationSaveForm);

        // 判断父组织是否存在
        Long parentId = organizationSaveForm.getParentId();
        if (parentId != null && organizationService.selectById(parentId) == null) {
            throw new NoSuchDependentedEntityException(NOT_FOUND, "找不到ID为" + parentId + "的父组织");
        }

        Organization organization = new Organization();
        BeanUtils.copyProperties(organizationSaveForm, organization);
        organization.setId(sequence.nextId());
        organization.setEnable(true);

        organizationService.insert(organization);
        return organization.getId();
    }

    @Override
    public void update(OrganizationUpdateForm organizationUpdateForm) throws NoSuchEntityException {
        log.info("修改组织：" + organizationUpdateForm);

        Long id = organizationUpdateForm.getId();
        Assert.notNull(id, "组织ID不能为空");
        Organization record = organizationService.selectById(id);
        if (record == null) {
            throw new NoSuchEntityException(NOT_FOUND, "找不到ID为" + id + "的组织");
        }

        BeanUtils.copyProperties(organizationUpdateForm, record);
        record.setUpdateTime(null);
        organizationService.updateById(record);
    }

    @Override
    public void deleteById(long id) throws NoSuchEntityException, IllegalOperationException {
        log.info("删除组织: id={}", id);

        int numOfChild = organizationService.selectCount(new EntityWrapper<Organization>().eq(Organization.PARENT_ID, id));
        if (numOfChild > 0) {
            throw new IllegalOperationException(DEPARTMENT_DELETE_FAILED, "该组织下还有子组织，不能删除");
        }

        int numOfRole = roleService.selectCount(new EntityWrapper<Role>().eq(Role.DEPARTMENT_ID, id));
        if (numOfRole > 0) {
            throw new IllegalOperationException(DEPARTMENT_DELETE_FAILED, "该组织下还有角色，不能删除");
        }

        if (!organizationService.deleteById(id)) {
            throw new NoSuchEntityException("找不到ID为" + id + "的组织信息");
        }
    }

    @Override
    public OrganizationDTO getById(long id) {
        OrganizationDTO dto = null;
        Organization organization = organizationService.selectById(id);

        if (organization != null) {
            dto = new OrganizationDTO();
            BeanUtils.copyProperties(organization, dto);

            if (organization.getParentId() != null) {
                Organization parent = organizationService.selectById(organization.getParentId());
                dto.setParentName(parent.getName());
            }
        }

        return dto;
    }

    @Override
    public List<TreeNode<OrganizationDTO>> listTree(Long id) throws NoSuchEntityException {
        Organization record = organizationService.selectById(id);
        if (record == null) {
            throw new NoSuchEntityException("找不到ID为" + id + "的组织信息");
        }

        // TODO 待优化
        List<Organization> allOrganization = organizationService.selectList(
                new EntityWrapper<Organization>().orderBy(Organization.SORT, true)
        );

        // 转成DTO
        List<OrganizationDTO> dtoList = allOrganization.stream().map(entity -> {
            OrganizationDTO dto = new OrganizationDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }).collect(Collectors.toList());

        List<TreeNode<OrganizationDTO>> treeNodes = TreeNodeUtils.buildTree(dtoList, id, config);
        wireParentName(treeNodes, allOrganization);

        return treeNodes;
    }

    /**
     * 批量设置ParentName属性（无DB连接）
     *
     * @param treeNodes       组织树
     * @param allOrganization 所有组织
     */
    private void wireParentName(List<TreeNode<OrganizationDTO>> treeNodes, List<Organization> allOrganization) {
        for (TreeNode<OrganizationDTO> root : treeNodes) {
            // 根结点parentName赋值
            Long parentId = (Long) root.getParentId();
            if (parentId != null) {
                Optional<Organization> parentOptional = allOrganization.stream()
                        .filter(entity -> entity.getId().equals(parentId))
                        .findFirst();
                parentOptional.ifPresent(parent -> root.getDetail().setParentName(parent.getName()));
            }

            // 非根节点parentName赋值
            TreeNodeUtils.bfsWalker(root, (node, parent, deep) -> {
                if (parent != null) {
                    node.getDetail().setParentName(parent.getLabel());
                }
            });
        }
    }
}
