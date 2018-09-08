package com.lyl.study.cloud.gateway.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.lyl.study.cloud.base.dto.PageInfo;
import com.lyl.study.cloud.base.dto.Result;
import com.lyl.study.cloud.gateway.api.ErrorCode;
import com.lyl.study.cloud.gateway.api.dto.request.UserListConditions;
import com.lyl.study.cloud.gateway.api.dto.request.UserSaveForm;
import com.lyl.study.cloud.gateway.api.dto.request.UserUpdateForm;
import com.lyl.study.cloud.gateway.api.dto.response.RoleDTO;
import com.lyl.study.cloud.gateway.api.dto.response.UserDTO;
import com.lyl.study.cloud.gateway.api.dto.response.UserDetailDTO;
import com.lyl.study.cloud.gateway.api.facade.UserFacade;
import org.springframework.web.bind.annotation.*;

import java.rmi.NoSuchObjectException;
import java.util.List;

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
     * @return 返回对应ID的用户信息；找不到用户时，返回null
     */
    @GetMapping("/{id}")
    public Result<UserDetailDTO> getById(@PathVariable("id") long id) {
        return new Result<>(ErrorCode.OK, "查询成功", userFacade.getById(id));
    }

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 返回对应用户名的用户信息；找不到用户时，返回null
     */
    @GetMapping("/getByUsername")
    public Result<UserDetailDTO> getByUsername(@RequestParam String username) {
        return new Result<>(ErrorCode.OK, "查询成功", userFacade.getByUsername(username));
    }

    /**
     * 获取用户的角色列表
     *
     * @param userId 用户ID
     * @return 返回对应ID用户的角色列表
     * @throws NoSuchObjectException 找不到用户时抛出次异常
     */
    @GetMapping("/roles/{userId}")
    public Result<List<RoleDTO>> getRolesByUserId(@PathVariable("userId") Long userId) throws NoSuchObjectException {
        return new Result<>(ErrorCode.OK, "查询成功", userFacade.getRolesByUserId(userId));
    }

    /**
     * 新增用户信息
     *
     * @return 新用户ID
     */
    @PostMapping
    public Result<Long> save(@RequestBody UserSaveForm userSaveForm) {
        return new Result<>(ErrorCode.OK, "新增成功", userFacade.save(userSaveForm));
    }

    /**
     * 修改用户信息
     */
    @PutMapping
    public Result update(@RequestBody UserUpdateForm userUpdateForm) {
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
        return new Result<>(ErrorCode.OK, "删除成功", userFacade.deleteById(id));
    }
}
