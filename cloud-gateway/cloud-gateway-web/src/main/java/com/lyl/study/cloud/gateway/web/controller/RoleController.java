package com.lyl.study.cloud.gateway.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.lyl.study.cloud.base.dto.PageInfo;
import com.lyl.study.cloud.base.dto.Result;
import com.lyl.study.cloud.base.exception.NoSuchDependentedEntityException;
import com.lyl.study.cloud.gateway.api.ErrorCode;
import com.lyl.study.cloud.gateway.api.dto.request.RoleSaveForm;
import com.lyl.study.cloud.gateway.api.dto.request.RoleUpdateForm;
import com.lyl.study.cloud.gateway.api.dto.response.RoleDTO;
import com.lyl.study.cloud.gateway.api.facade.RoleFacade;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping
    public Result<Long> save(@RequestBody @Validated RoleSaveForm roleSaveForm) {
        try {
            return new Result<>(ErrorCode.OK, "新增成功", roleFacade.save(roleSaveForm));
        } catch (NoSuchDependentedEntityException e) {
            return new Result<>(ErrorCode.NOT_FOUND, e.getMessage(), null);
        }
    }

    /**
     * 修改角色信息
     *
     * @param roleUpdateForm 表单
     */
    @PutMapping
    public Result update(RoleUpdateForm roleUpdateForm) {
        try {
            roleFacade.update(roleUpdateForm);
            return new Result<>(ErrorCode.OK, "修改成功", null);
        } catch (NoSuchDependentedEntityException e) {
            return new Result<>(ErrorCode.NOT_FOUND, e.getMessage(), null);
        }
    }

    /**
     * 删除角色（级联删除角色授权项关联关系）
     *
     * @param id 角色ID
     */
    @DeleteMapping("/{id}")
    public Result<Integer> deleteById(@PathVariable("id") Long id) {
        int rows = roleFacade.deleteById(id);
        if (rows > 0) {
            return new Result<>(ErrorCode.OK, "删除成功", rows);
        } else {
            return new Result<>(ErrorCode.NOT_FOUND, "找不到ID为" + id + "的角色信息", rows);
        }
    }

    /**
     * 根据ID获取角色信息
     *
     * @param id 角色ID
     * @return 返回对应ID的角色信息；找不到角色时，返回null
     */
    @GetMapping("/{id}")
    public Result<RoleDTO> getById(@PathVariable("id") Long id) {
        RoleDTO dto = roleFacade.getById(id);
        if (dto != null) {
            return new Result<>(ErrorCode.OK, "查询成功", dto);
        } else {
            return new Result<>(ErrorCode.NOT_FOUND, "找不到ID为" + id + "的角色信息", null);
        }
    }

    /**
     * 角色列表查询（这里不会展示每个角色的授权项）
     *
     * @param departmentId 筛选部门（可选）
     * @param pageIndex    页码
     * @param pageSize     页面大小
     * @return 角色信息列表
     */
    @GetMapping("/list")
    public Result<PageInfo<RoleDTO>> list(Long departmentId,
                                          @RequestParam(name = "pageIndex", defaultValue = "1") Integer pageIndex,
                                          @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        return new Result<>(ErrorCode.OK, "查询成功", roleFacade.list(departmentId, pageIndex, pageSize));
    }
}
