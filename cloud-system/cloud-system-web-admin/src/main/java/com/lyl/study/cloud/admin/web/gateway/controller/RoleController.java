package com.lyl.study.cloud.admin.web.gateway.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.lyl.study.cloud.base.dto.PageInfo;
import com.lyl.study.cloud.base.dto.Result;
import com.lyl.study.cloud.gateway.api.dto.request.RoleSaveForm;
import com.lyl.study.cloud.gateway.api.dto.request.RoleUpdateForm;
import com.lyl.study.cloud.gateway.api.dto.response.RoleDTO;
import com.lyl.study.cloud.gateway.api.dto.response.UserDetailDTO;
import com.lyl.study.cloud.gateway.api.facade.RoleFacade;
import com.lyl.study.cloud.gateway.security.CurrentSessionHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.lyl.study.cloud.gateway.api.GatewayErrorCode.NOT_FOUND;
import static com.lyl.study.cloud.gateway.api.GatewayErrorCode.OK;

@RestController
@RequestMapping("/role")
public class RoleController {
    @Reference
    private RoleFacade roleFacade;

    /**
     * 新建角色信息
     *
     * @param roleSaveForm 表单
     * @return 角色ID
     */
    @PreAuthorize("hasAuthority('system:role:save')")
    @PostMapping
    public Result<Long> save(@RequestBody @Validated RoleSaveForm roleSaveForm) {
        UserDetailDTO user = CurrentSessionHolder.getCurrentUser();
        RoleDTO currentRole = CurrentSessionHolder.getCurrentRole();
        roleSaveForm.setCreatorId(user.getId());
        roleSaveForm.setOwnerId(user.getId());
        roleSaveForm.setOwnerRoleId(currentRole.getId());

        return new Result<>(OK, "新增成功", roleFacade.save(roleSaveForm));
    }

    /**
     * 修改角色信息
     *
     * @param roleUpdateForm 表单
     */
    @PreAuthorize("hasAuthority('system:role:update')")
    @PutMapping
    public Result update(@RequestBody @Validated RoleUpdateForm roleUpdateForm) {
        roleFacade.update(roleUpdateForm);
        return new Result<>(OK, "修改成功", null);
    }

    /**
     * 删除角色（级联删除角色授权项关联关系）
     *
     * @param id 角色ID
     */
    @PreAuthorize("hasAuthority('system:role:delete')")
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable("id") Long id) {
        roleFacade.deleteById(id);
        return new Result<>(OK, "删除成功", null);
    }

    /**
     * 根据ID获取角色信息
     *
     * @param id 角色ID
     * @return 返回对应ID的角色信息；找不到角色时，返回null
     */
    @PreAuthorize("hasAuthority('system:role:read')")
    @GetMapping("/{id}")
    public Result<RoleDTO> getById(@PathVariable("id") Long id) {
        RoleDTO dto = roleFacade.getById(id);
        if (dto != null) {
            return new Result<>(OK, "查询成功", dto);
        } else {
            return new Result<>(NOT_FOUND, "找不到ID为" + id + "的角色信息", null);
        }
    }

    /**
     * 角色列表查询（这里不会展示每个角色的授权项）
     *
     * @param organizationId 筛选组织（可选）
     * @param pageIndex      页码
     * @param pageSize       页面大小
     * @return 角色信息列表
     */
    @PreAuthorize("hasAuthority('system:role:read')")
    @GetMapping("/list")
    public Result<PageInfo<RoleDTO>> list(Long organizationId,
                                          @RequestParam(name = "pageIndex", defaultValue = "1") Integer pageIndex,
                                          @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        return new Result<>(OK, "查询成功", roleFacade.list(organizationId, pageIndex, pageSize));
    }
}
