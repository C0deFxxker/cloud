package com.lyl.study.cloud.gateway.core.facade.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.lyl.study.cloud.base.dto.TreeNode;
import com.lyl.study.cloud.base.exception.NoSuchDependentedEntityException;
import com.lyl.study.cloud.base.exception.NoSuchEntityException;
import com.lyl.study.cloud.base.idworker.Sequence;
import com.lyl.study.cloud.base.util.TreeNodeUtils;
import com.lyl.study.cloud.gateway.api.dto.request.DepartmentSaveForm;
import com.lyl.study.cloud.gateway.api.dto.request.DepartmentUpdateForm;
import com.lyl.study.cloud.gateway.api.dto.response.DepartmentDTO;
import com.lyl.study.cloud.gateway.api.facade.DepartmentFacade;
import com.lyl.study.cloud.gateway.core.entity.Department;
import com.lyl.study.cloud.gateway.core.entity.Role;
import com.lyl.study.cloud.gateway.core.service.DepartmentService;
import com.lyl.study.cloud.gateway.core.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author liyilin
 */
@Service
public class DepartmentFacadeImpl implements DepartmentFacade {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TreeNodeUtils.BuildTreeConfig<DepartmentDTO> config;

    @Autowired
    private RoleService roleService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private Sequence sequence;

    public DepartmentFacadeImpl() {
        config = new TreeNodeUtils.BuildTreeConfig<>();
        config.setIdGetter(DepartmentDTO::getId);
        config.setLabelGetter(DepartmentDTO::getName);
        config.setParentIdGetter(DepartmentDTO::getParentId);
    }

    @Override
    public long save(DepartmentSaveForm departmentSaveForm) throws NoSuchDependentedEntityException {
        logger.info("新增部门：" + departmentSaveForm);

        // 判断父部门是否存在
        Long parentId = departmentSaveForm.getParentId();
        if (parentId != null && departmentService.selectById(parentId) == null) {
            throw new NoSuchDependentedEntityException("找不到ID为" + parentId + "的父部门");
        }

        Department department = new Department();
        BeanUtils.copyProperties(departmentSaveForm, department);
        department.setId(sequence.nextId());
        department.setEnable(true);

        departmentService.insert(department);
        return department.getId();
    }

    @Override
    public void update(DepartmentUpdateForm departmentUpdateForm) throws NoSuchEntityException {
        logger.info("修改部门：" + departmentUpdateForm);

        Long id = departmentUpdateForm.getId();
        Assert.notNull(id, "部门ID不能为空");
        Department record = departmentService.selectById(id);
        if (record == null) {
            throw new NoSuchEntityException("找不到ID为" + id + "的部门");
        }

        BeanUtils.copyProperties(departmentUpdateForm, record);
        departmentService.updateById(record);
    }

    @Override
    public int deleteById(long id) {
        logger.info("删除部门: id={}", id);

        int numOfChild = departmentService.selectCount(new EntityWrapper<Department>().eq(Department.PARENT_ID, id));
        if (numOfChild > 0) {
            throw new IllegalAccessError("该部门下还有子部门，不能删除");
        }

        int numOfRole = roleService.selectCount(new EntityWrapper<Role>().eq(Role.DEPARTMENT_ID, id));
        if (numOfRole > 0) {
            throw new IllegalAccessError("该部门下还有角色，不能删除");
        }

        return departmentService.deleteById(id) ? 1 : 0;
    }

    @Override
    public DepartmentDTO getById(long id) {
        DepartmentDTO dto = null;
        Department department = departmentService.selectById(id);

        if (department != null) {
            dto = new DepartmentDTO();
            BeanUtils.copyProperties(department, dto);

            if (department.getParentId() != null) {
                Department parent = departmentService.selectById(department.getParentId());
                dto.setParentName(parent.getName());
            }
        }

        return dto;
    }

    @Override
    public List<TreeNode<DepartmentDTO>> listTree(Long id) throws IllegalArgumentException {
        // TODO 待优化
        List<Department> allDepartment = departmentService.selectList(new EntityWrapper<>());

        // 转成DTO
        List<DepartmentDTO> dtoList = allDepartment.stream().map(entity -> {
            DepartmentDTO dto = new DepartmentDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }).collect(Collectors.toList());

        List<TreeNode<DepartmentDTO>> treeNodes = TreeNodeUtils.buildTree(dtoList, id, config);
        wireParentName(treeNodes, allDepartment);

        return treeNodes;
    }

    /**
     * 批量设置ParentName属性（无DB连接）
     *
     * @param treeNodes     部门树
     * @param allDepartment 所有部门
     */
    private void wireParentName(List<TreeNode<DepartmentDTO>> treeNodes, List<Department> allDepartment) {
        for (TreeNode<DepartmentDTO> root : treeNodes) {
            // 根结点parentName赋值
            Long parentId = (Long) root.getParentId();
            if (parentId != null) {
                Optional<Department> parentOptional = allDepartment.stream()
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
