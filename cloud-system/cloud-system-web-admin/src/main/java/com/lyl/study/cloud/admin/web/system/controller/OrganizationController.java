package com.lyl.study.cloud.admin.web.system.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.lyl.study.cloud.base.dto.Result;
import com.lyl.study.cloud.base.dto.TreeNode;
import com.lyl.study.cloud.base.exception.NoSuchDependentedEntityException;
import com.lyl.study.cloud.base.exception.NoSuchEntityException;
import com.lyl.study.cloud.system.api.dto.request.OrganizationSaveForm;
import com.lyl.study.cloud.system.api.dto.request.OrganizationUpdateForm;
import com.lyl.study.cloud.system.api.dto.response.OrganizationDTO;
import com.lyl.study.cloud.system.api.dto.response.RoleDTO;
import com.lyl.study.cloud.system.api.dto.response.UserDetailDTO;
import com.lyl.study.cloud.system.api.facade.OrganizationFacade;
import com.lyl.study.cloud.admin.security.CurrentSessionHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.lyl.study.cloud.system.api.SystemErrorCode.NOT_FOUND;
import static com.lyl.study.cloud.system.api.SystemErrorCode.OK;

/**
 * @author liyilin
 */
@RestController
@RequestMapping("/system/organization")
public class OrganizationController {
    @Reference
    private OrganizationFacade organizationFacade;

    /**
     * 新增组织
     *
     * @param organizationSaveForm 表单
     * @return 新增的组织信息
     * @throws NoSuchDependentedEntityException 找不到对应parentId的父节点组织时抛出此异常
     */
    @PreAuthorize("hasAuthority('system:organization:save')")
    @PostMapping
    public Result<Long> save(@RequestBody @Validated OrganizationSaveForm organizationSaveForm) {
        UserDetailDTO user = CurrentSessionHolder.getCurrentUser();
        RoleDTO currentRole = CurrentSessionHolder.getCurrentRole();
        organizationSaveForm.setCreatorId(user.getId());
        organizationSaveForm.setOwnerId(user.getId());
        organizationSaveForm.setOwnerRoleId(currentRole.getId());

        return new Result<>(OK, "新增成功", organizationFacade.save(organizationSaveForm));
    }

    /**
     * 修改组织
     *
     * @param organizationUpdateForm 表单
     * @throws NoSuchEntityException 待修改对象不存在时，抛出此异常
     */
    @PreAuthorize("hasAuthority('system:organization:update')")
    @PutMapping
    public Result update(@RequestBody @Validated OrganizationUpdateForm organizationUpdateForm) {
        organizationFacade.update(organizationUpdateForm);
        return new Result<>(OK, "修改成功", null);
    }

    /**
     * 根据ID删除组织
     *
     * @param id 组织ID
     * @return 删除记录数
     */
    @PreAuthorize("hasAuthority('system:organization:delete')")
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable("id") Long id) throws IllegalAccessError {
        organizationFacade.deleteById(id);
        return new Result<>(OK, "删除成功", null);
    }

    /**
     * 根据ID获取组织信息
     *
     * @param id 组织ID
     * @return 若查询成功，返回包含有对应ID组织信息的Result对象；否则，返回描述错误信息的Result对象
     */
    @PreAuthorize("hasAuthority('system:organization:read')")
    @GetMapping("/{id}")
    public Result<OrganizationDTO> getById(@PathVariable("id") Long id) {
        OrganizationDTO dto = organizationFacade.getById(id);
        if (dto != null) {
            return new Result<>(OK, "查询成功", dto);
        } else {
            return new Result<>(NOT_FOUND, "找不到ID为" + id + "的组织", null);
        }
    }


    /**
     * 列出组织树状结构
     *
     * @param id 根节点ID。若为null，则返回所有树
     * @return 若查询成功，则返回含有组织树列表的Result对象；
     * 若指定的根节点ID对应的组织不存在，则返回的Result对象数据字段中仅含有一个空列表。
     */
    @PreAuthorize("hasAuthority('system:organization:read')")
    @GetMapping("/tree")
    public Result<List<TreeNode<OrganizationDTO>>> listTree(Long id) {
        return new Result<>(OK, "查询成功", organizationFacade.listTree(id));
    }
}
