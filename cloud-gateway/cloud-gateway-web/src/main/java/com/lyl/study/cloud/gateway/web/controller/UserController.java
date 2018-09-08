package com.lyl.study.cloud.gateway.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.lyl.study.cloud.base.dto.PageInfo;
import com.lyl.study.cloud.base.dto.Result;
import com.lyl.study.cloud.base.exception.NoSuchEntityException;
import com.lyl.study.cloud.gateway.api.ErrorCode;
import com.lyl.study.cloud.gateway.api.dto.request.UserListConditions;
import com.lyl.study.cloud.gateway.api.dto.request.UserSaveForm;
import com.lyl.study.cloud.gateway.api.dto.request.UserUpdateForm;
import com.lyl.study.cloud.gateway.api.dto.response.RoleDTO;
import com.lyl.study.cloud.gateway.api.dto.response.UserDTO;
import com.lyl.study.cloud.gateway.api.dto.response.UserDetailDTO;
import com.lyl.study.cloud.gateway.api.facade.UserFacade;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liyilin
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Reference
    private UserFacade userFacade;

    /**
     * 查询用户列表
     *
     * @param conditions 查询表单
     * @return 用户列表
     */
    @GetMapping("/list")
    public Result<PageInfo<UserDTO>> list(@ModelAttribute UserListConditions conditions) {
        return new Result<>(ErrorCode.OK, "查询成功", userFacade.list(conditions));
    }

    /**
     * 根据ID获取用户信息
     *
     * @param id 用户ID
     * @return 返回对应ID的用户信息
     */
    @GetMapping("/{id}")
    public Result<UserDetailDTO> getById(@PathVariable("id") long id) {
        UserDetailDTO dto = userFacade.getById(id);
        if (dto != null) {
            return new Result<>(ErrorCode.OK, "查询成功", null);
        } else {
            return new Result<>(ErrorCode.NOT_FOUND, "找不到ID为" + id + "的用户信息", null);
        }
    }

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 返回对应用户名的用户信息
     */
    @GetMapping("/getByUsername")
    public Result<UserDetailDTO> getByUsername(@RequestParam String username) {
        UserDetailDTO dto = userFacade.getByUsername(username);
        if (dto != null) {
            return new Result<>(ErrorCode.OK, "查询成功", dto);
        } else {
            return new Result<>(ErrorCode.NOT_FOUND, "找不到用户名为" + username + "的用户信息", null);
        }
    }

    /**
     * 获取用户的角色列表
     *
     * @param userId 用户ID
     * @return 返回对应ID用户的角色列表
     */
    @GetMapping("/roles/{userId}")
    public Result<List<RoleDTO>> getRolesByUserId(@PathVariable("userId") Long userId) {
        try {
            return new Result<>(ErrorCode.OK, "查询成功", userFacade.getRolesByUserId(userId));
        } catch (NoSuchEntityException e) {
            return new Result<>(ErrorCode.NOT_FOUND, e.getMessage(), null);
        }
    }

    /**
     * 新增用户信息
     *
     * @return 新用户ID
     */
    @PostMapping
    public Result<Long> save(@RequestBody @Validated UserSaveForm userSaveForm) {
        try {
            return new Result<>(ErrorCode.OK, "新增成功", userFacade.save(userSaveForm));
        } catch (IllegalArgumentException e) {
            return new Result<>(ErrorCode.BAD_REQUEST, "用户名已存在", null);
        }
    }

    /**
     * 修改用户信息
     */
    @PutMapping
    public Result update(@RequestBody @Validated UserUpdateForm userUpdateForm) {
        userFacade.update(userUpdateForm);
        return new Result<>(ErrorCode.OK, "修改成功", null);
    }

    /**
     * 根据ID删除用户信息
     *
     * @param id 用户ID
     */
    @DeleteMapping("/{id}")
    public Result<Integer> deleteById(@PathVariable("id") Long id) {
        int row = userFacade.deleteById(id);
        if (row > 0) {
            return new Result<>(ErrorCode.OK, "删除成功", row);
        } else {
            return new Result<>(ErrorCode.NOT_FOUND, "找不到ID为" + id + "的用户信息", row);
        }
    }
}
