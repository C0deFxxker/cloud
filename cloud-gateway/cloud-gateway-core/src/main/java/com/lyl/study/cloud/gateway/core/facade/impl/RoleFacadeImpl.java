package com.lyl.study.cloud.gateway.core.facade.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.lyl.study.cloud.base.dto.PageInfo;
import com.lyl.study.cloud.base.exception.NoSuchEntityException;
import com.lyl.study.cloud.gateway.api.dto.request.RoleSaveForm;
import com.lyl.study.cloud.gateway.api.dto.request.RoleUpdateForm;
import com.lyl.study.cloud.gateway.api.dto.response.PermissionItem;
import com.lyl.study.cloud.gateway.api.dto.response.RoleDTO;
import com.lyl.study.cloud.gateway.api.facade.RoleFacade;
import com.lyl.study.cloud.gateway.core.entity.Organization;
import com.lyl.study.cloud.gateway.core.entity.Role;
import com.lyl.study.cloud.gateway.core.service.OrganizationService;
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
    private OrganizationService organizationService;

    @Override
    public long save(RoleSaveForm roleSaveForm) {
        // TODO 校对授权项是否存在
        return roleService.save(roleSaveForm);
    }

    @Override
    public void update(RoleUpdateForm roleUpdateForm) throws NoSuchEntityException {
        // TODO 校对授权项是否存在
        roleService.update(roleUpdateForm);
    }

    @Override
    public void deleteById(long id) {
        roleService.deleteById(id);
    }

    @Override
    public RoleDTO getById(long id) {
        Role role = roleService.selectById(id);
        if (role != null) {
            RoleDTO dto = new RoleDTO();
            BeanUtils.copyProperties(role, dto);
            // 获取组织名称
            Organization organization = organizationService.selectById(role.getOrganizationId());
            dto.setOrganizationName(organization.getName());
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
    public PageInfo<RoleDTO> list(Long organizationId, Integer pageIndex, Integer pageSize) {
        EntityWrapper<Role> wrapper = new EntityWrapper<>();
        if (organizationId != null) {
            wrapper.eq(Role.DEPARTMENT_ID, organizationId);
        }
        wrapper.orderBy(Role.ID, false);
        Page<Role> page = new Page<>(pageIndex, pageSize);
        page = roleService.selectPage(page, wrapper);

        List<Role> records = page.getRecords();
        // 批量获取组织名称
        Map<Long, String> organizationIdNameMap = batchGetOrganizationNames(records);
        // 转DTO
        List<RoleDTO> dtoList = records.stream()
                .map(entity -> {
                    RoleDTO dto = new RoleDTO();
                    BeanUtils.copyProperties(entity, dto);
                    String deptName = organizationIdNameMap.get(dto.getOrganizationId());
                    dto.setOrganizationName(deptName);
                    return dto;
                }).collect(Collectors.toList());

        return new PageInfo<>(pageIndex, pageSize, page.getTotal(), dtoList);
    }

    /**
     * 批量获取角色列表对应的组织名称
     *
     * @param records 角色列表
     * @return 组织ID与组织名称映射
     */
    private Map<Long, String> batchGetOrganizationNames(List<Role> records) {
        if (!records.isEmpty()) {
            Set<Long> organizationIdSet = records.stream().map(Role::getOrganizationId).collect(Collectors.toSet());
            Map<Long, String> organizationIdNameMap = new HashMap<>(organizationIdSet.size());
            List<Organization> organizations = organizationService.selectBatchIds(organizationIdSet);
            organizations.forEach(entity -> organizationIdNameMap.put(entity.getId(), entity.getName()));
            return organizationIdNameMap;
        } else {
            return Collections.emptyMap();
        }
    }
}
