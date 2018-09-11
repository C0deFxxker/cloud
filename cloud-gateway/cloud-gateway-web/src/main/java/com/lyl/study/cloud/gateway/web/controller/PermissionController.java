package com.lyl.study.cloud.gateway.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.lyl.study.cloud.base.dto.Result;
import com.lyl.study.cloud.base.dto.TreeNode;
import com.lyl.study.cloud.base.exception.NoSuchEntityException;
import com.lyl.study.cloud.gateway.api.ErrorCode;
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
        try {
            UserDetailDTO user = CurrentSessionHolder.getCurrentUser();
            RoleDTO currentRole = CurrentSessionHolder.getCurrentRole();
            permissionSaveForm.setCreatorId(user.getId());
            permissionSaveForm.setOwnerId(user.getId());
            permissionSaveForm.setOwnerRoleId(currentRole.getId());

            return new Result<>(ErrorCode.OK, "新增成功", permissionFacade.save(permissionSaveForm));
        } catch (NoSuchEntityException e) {
            return new Result<>(ErrorCode.NOT_FOUND, "找不到ID为" + permissionSaveForm.getParentId() + "的授权项", null);
        } catch (IllegalArgumentException e) {
            return new Result<>(ErrorCode.BAD_REQUEST, "非法授权项类型", null);
        }
    }

    /**
     * 修改授权项
     *
     * @param permissionUpdateForm 表单
     */
    @PutMapping
    public Result update(@RequestBody @Validated PermissionUpdateForm permissionUpdateForm) {
        try {
            permissionFacade.update(permissionUpdateForm);
            return new Result<>(ErrorCode.OK, "修改成功", null);
        } catch (NoSuchEntityException e) {
            return new Result<>(ErrorCode.NOT_FOUND, "找不到ID为" + permissionUpdateForm.getId() + "的授权项", null);
        }
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
            return new Result<>(ErrorCode.OK, "查询成功", dto);
        } else {
            return new Result<>(ErrorCode.NOT_FOUND, "找不到ID为" + id + "的授权项", null);
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
        try {
            return new Result<>(ErrorCode.OK, "删除成功", permissionFacade.deleteById(id, force));
        } catch (NoSuchEntityException e) {
            return new Result<>(ErrorCode.NOT_FOUND, e.getMessage(), null);
        } catch (IllegalAccessError e) {
            return new Result<>(ErrorCode.BAD_REQUEST, e.getMessage(), null);
        }
    }

    /**
     * 展示整个菜单树
     *
     * @return 菜单树信息
     */
    public Result<List<TreeNode<PermissionDTO>>> tree() {
        return new Result<>(ErrorCode.OK, "查询成功", permissionFacade.tree());
    }
}
