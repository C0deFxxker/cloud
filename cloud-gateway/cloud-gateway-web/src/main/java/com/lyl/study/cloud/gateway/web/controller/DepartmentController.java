package com.lyl.study.cloud.gateway.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.lyl.study.cloud.base.dto.Result;
import com.lyl.study.cloud.base.dto.TreeNode;
import com.lyl.study.cloud.base.exception.NoSuchDependentedEntityException;
import com.lyl.study.cloud.base.exception.NoSuchEntityException;
import com.lyl.study.cloud.gateway.api.ErrorCode;
import com.lyl.study.cloud.gateway.api.dto.request.DepartmentSaveForm;
import com.lyl.study.cloud.gateway.api.dto.request.DepartmentUpdateForm;
import com.lyl.study.cloud.gateway.api.dto.response.DepartmentDTO;
import com.lyl.study.cloud.gateway.api.dto.response.RoleDTO;
import com.lyl.study.cloud.gateway.api.dto.response.UserDetailDTO;
import com.lyl.study.cloud.gateway.api.facade.DepartmentFacade;
import com.lyl.study.cloud.gateway.security.CurrentSessionHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liyilin
 */
@RestController
@RequestMapping("/department")
public class DepartmentController {
    @Reference
    private DepartmentFacade departmentFacade;

    /**
     * 新增部门
     *
     * @param departmentSaveForm 表单
     * @return 新增的部门信息
     * @throws NoSuchDependentedEntityException 找不到对应parentId的父节点部门时抛出此异常
     */
    @PostMapping
    public Result<Long> save(@RequestBody @Validated DepartmentSaveForm departmentSaveForm) {
        try {
            UserDetailDTO user = CurrentSessionHolder.getCurrentUser();
            RoleDTO currentRole = CurrentSessionHolder.getCurrentRole();
            departmentSaveForm.setCreatorId(user.getId());
            departmentSaveForm.setOwnerId(user.getId());
            departmentSaveForm.setOwnerRoleId(currentRole.getId());

            return new Result<>(ErrorCode.OK, "新增成功", departmentFacade.save(departmentSaveForm));
        } catch (NoSuchDependentedEntityException e) {
            return new Result<>(ErrorCode.NOT_FOUND, e.getMessage(), null);
        }
    }

    /**
     * 修改部门
     *
     * @param departmentUpdateForm 表单
     * @throws NoSuchEntityException 待修改对象不存在时，抛出此异常
     */
    @PutMapping
    public Result update(@RequestBody @Validated DepartmentUpdateForm departmentUpdateForm) {
        try {
            departmentFacade.update(departmentUpdateForm);
            return new Result<>(ErrorCode.OK, "修改成功", null);
        } catch (NoSuchEntityException e) {
            return new Result<>(ErrorCode.NOT_FOUND, e.getMessage(), null);
        }
    }

    /**
     * 根据ID删除部门
     *
     * @param id 部门ID
     * @return 删除记录数
     */
    @DeleteMapping("/{id}")
    public Result<Integer> deleteById(@PathVariable("id") Long id) throws IllegalAccessError {
        try {
            int rows = departmentFacade.deleteById(id);
            if (rows > 0) {
                return new Result<>(ErrorCode.OK, "删除成功", rows);
            } else {
                return new Result<>(ErrorCode.NOT_FOUND, "找不到ID为" + id + "的部门", null);
            }
        } catch (IllegalAccessError e) {
            return new Result<>(ErrorCode.DEPARTMENT_DELETE_FAILED, e.getMessage(), null);
        }
    }

    /**
     * 根据ID获取部门信息
     *
     * @param id 部门ID
     * @return 若查询成功，返回包含有对应ID部门信息的Result对象；否则，返回描述错误信息的Result对象
     */
    @GetMapping("/{id}")
    public Result<DepartmentDTO> getById(@PathVariable("id") Long id) {
        DepartmentDTO dto = departmentFacade.getById(id);
        if (dto != null) {
            return new Result<>(ErrorCode.OK, "查询成功", dto);
        } else {
            return new Result<>(ErrorCode.NOT_FOUND, "找不到ID为" + id + "的部门", null);
        }
    }


    /**
     * 列出部门树状结构
     *
     * @param id 根节点ID。若为null，则返回所有树
     * @return 若查询成功，则返回含有部门树列表的Result对象；
     * 若指定的根节点ID对应的部门不存在，则返回的Result对象数据字段中仅含有一个空列表。
     */
    @GetMapping("/tree")
    public Result<List<TreeNode<DepartmentDTO>>> listTree(Long id) {
        try {
            return new Result<>(ErrorCode.OK, "查询成功", departmentFacade.listTree(id));
        } catch (IllegalArgumentException e) {
            return new Result<>(ErrorCode.BAD_REQUEST, "找不到ID为" + id + "的部门", null);
        }
    }
}
