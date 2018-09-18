package com.lyl.study.cloud.admin.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.lyl.study.cloud.base.dto.Result;
import com.lyl.study.cloud.base.dto.TreeNode;
import com.lyl.study.cloud.gateway.api.dto.request.PermissionSaveForm;
import com.lyl.study.cloud.gateway.api.dto.request.PermissionUpdateForm;
import com.lyl.study.cloud.gateway.api.dto.response.PermissionDTO;
import com.lyl.study.cloud.gateway.api.dto.response.RoleDTO;
import com.lyl.study.cloud.gateway.api.dto.response.UserDetailDTO;
import com.lyl.study.cloud.gateway.api.facade.PermissionFacade;
import com.lyl.study.cloud.gateway.security.CurrentSessionHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.lyl.study.cloud.gateway.api.GatewayErrorCode.NOT_FOUND;
import static com.lyl.study.cloud.gateway.api.GatewayErrorCode.OK;

/**
 * @author liyilin
 */
@RestController
@RequestMapping("/permission")
public class PermissionController {
    @Reference
    private PermissionFacade permissionFacade;

    /**
     * 新增授权项
     *
     * @param permissionSaveForm 表单
     * @return 授权项ID
     */
    @PostMapping
    public Result<Long> save(@RequestBody @Validated PermissionSaveForm permissionSaveForm) {
        UserDetailDTO user = CurrentSessionHolder.getCurrentUser();
        RoleDTO currentRole = CurrentSessionHolder.getCurrentRole();
        permissionSaveForm.setCreatorId(user.getId());
        permissionSaveForm.setOwnerId(user.getId());
        permissionSaveForm.setOwnerRoleId(currentRole.getId());

        return new Result<>(OK, "新增成功", permissionFacade.save(permissionSaveForm));
    }

    /**
     * 修改授权项
     *
     * @param permissionUpdateForm 表单
     */
    @PutMapping
    public Result update(@RequestBody @Validated PermissionUpdateForm permissionUpdateForm) {
        permissionFacade.update(permissionUpdateForm);
        return new Result<>(OK, "修改成功", null);
    }

    /**
     * 根据ID获取授权项
     *
     * @param id 授权项ID
     * @return 返回对应ID的授权项信息；找不到授权项时，返回null
     */
    @GetMapping("/{id}")
    public Result<PermissionDTO> getById(@PathVariable("id") Long id) {
        PermissionDTO dto = permissionFacade.getById(id);
        if (dto != null) {
            return new Result<>(OK, "查询成功", dto);
        } else {
            return new Result<>(NOT_FOUND, "找不到ID为" + id + "的授权项", null);
        }
    }

    /**
     * 删除授权项（级联删除角色授权项关联关系）
     *
     * @param id    授权项ID
     * @param force 是否强制删除：如果是，则连同子授权项一并删除；否则，检查到该授权项有子授权项时，抛出异常
     * @return 删除记录数
     */
    @DeleteMapping("/{id}")
    public Result<Integer> deleteById(@PathVariable("id") Long id,
                                      @RequestParam(name = "force", defaultValue = "false") Boolean force) {
        return new Result<>(OK, "删除成功", permissionFacade.deleteById(id, force));
    }

    /**
     * 展示整个菜单树
     *
     * @return 菜单树信息
     */
    @GetMapping("/tree")
    public Result<List<TreeNode<PermissionDTO>>> tree() {
        return new Result<>(OK, "查询成功", permissionFacade.tree());
    }
}
