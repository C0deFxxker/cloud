package com.lyl.study.cloud.gateway.core.facade.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.lyl.study.cloud.base.dto.PageInfo;
import com.lyl.study.cloud.gateway.api.dto.request.RoleSaveForm;
import com.lyl.study.cloud.gateway.api.dto.request.RoleUpdateForm;
import com.lyl.study.cloud.gateway.api.dto.response.PermissionItem;
import com.lyl.study.cloud.gateway.api.dto.response.RoleDTO;
import com.lyl.study.cloud.gateway.api.facade.RoleFacade;
import com.lyl.study.cloud.gateway.core.entity.Department;
import com.lyl.study.cloud.gateway.core.entity.Role;
import com.lyl.study.cloud.gateway.core.service.DepartmentService;
import com.lyl.study.cloud.gateway.core.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleFacadeImpl implements RoleFacade {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RoleService roleService;
    @Autowired
    private DepartmentService departmentService;

    @Override
    public long save(RoleSaveForm roleSaveForm) {
        // TODO 校对授权项是否存在
        return roleService.save(roleSaveForm);
    }

    @Override
    public void update(RoleUpdateForm roleUpdateForm) {
        // TODO 校对授权项是否存在
        roleService.update(roleUpdateForm);
    }

    @Override
    public int deleteById(long id) {
        return roleService.deleteById(id);
    }

    @Override
    public RoleDTO getById(long id) {
        Role role = roleService.selectById(id);
        if (role != null) {
            RoleDTO dto = new RoleDTO();
            BeanUtils.copyProperties(role, dto);
            // 获取部门名称
            Department department = departmentService.selectById(role.getDepartmentId());
            dto.setDepartmentName(department.getName());
            // 获取角色关联的授权项
            List<PermissionItem> permissions = roleService.getPermissionByRoleId(id);
            dto.setPermissions(permissions);
            return dto;
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public PageInfo<RoleDTO> list(Long departmentId, Integer pageIndex, Integer pageSize) {
        EntityWrapper<Role> wrapper = new EntityWrapper<>();
        if (departmentId != null) {
            wrapper.eq(Role.DEPARTMENT_ID, departmentId);
        }
        Page<Role> page = new Page<>(pageIndex, pageSize);
        page = roleService.selectPage(page, wrapper);

        List<Role> records = page.getRecords();
        // 批量获取部门名称
        Map<Long, String> departmentIdNameMap = batchGetDepartmentNames(records);
        // 转DTO
        List<RoleDTO> dtoList = records.stream()
                .map(entity -> {
                    RoleDTO dto = new RoleDTO();
                    BeanUtils.copyProperties(entity, dto);
                    String deptName = departmentIdNameMap.get(dto.getDepartmentId());
                    dto.setDepartmentName(deptName);
                    return dto;
                }).collect(Collectors.toList());

        return new PageInfo<>(pageIndex, pageSize, page.getTotal(), dtoList);
    }

    /**
     * 批量获取角色列表对应的部门名称
     *
     * @param records 角色列表
     * @return 部门ID与部门名称映射
     */
    private Map<Long, String> batchGetDepartmentNames(List<Role> records) {
        if (!records.isEmpty()) {
            Set<Long> departmentIdSet = records.stream().map(Role::getDepartmentId).collect(Collectors.toSet());
            Map<Long, String> departmentIdNameMap = new HashMap<>(departmentIdSet.size());
            List<Department> departments = departmentService.selectBatchIds(departmentIdSet);
            departments.forEach(entity -> departmentIdNameMap.put(entity.getId(), entity.getName()));
            return departmentIdNameMap;
        } else {
            return Collections.emptyMap();
        }
    }
}
