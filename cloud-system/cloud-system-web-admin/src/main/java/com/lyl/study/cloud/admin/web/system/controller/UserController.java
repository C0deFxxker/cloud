package com.lyl.study.cloud.admin.web.system.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.lyl.study.cloud.base.dto.PageInfo;
import com.lyl.study.cloud.base.dto.Result;
import com.lyl.study.cloud.gateway.api.dto.request.*;
import com.lyl.study.cloud.gateway.api.dto.response.RoleDTO;
import com.lyl.study.cloud.gateway.api.dto.response.UserDTO;
import com.lyl.study.cloud.gateway.api.dto.response.UserDetailDTO;
import com.lyl.study.cloud.gateway.api.facade.UserFacade;
import com.lyl.study.cloud.admin.security.CurrentSessionHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.lyl.study.cloud.base.CommonErrorCode.NOT_FOUND;
import static com.lyl.study.cloud.base.CommonErrorCode.OK;

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
    @PreAuthorize("hasAuthority('system:user:read')")
    @GetMapping("/list")
    public Result<PageInfo<UserDTO>> list(@ModelAttribute UserListConditions conditions) {
        if (conditions.getPageIndex() == null) {
            conditions.setPageIndex(1);
        }
        if (conditions.getPageSize() == null) {
            conditions.setPageSize(10);
        }
        return new Result<>(OK, "查询成功", userFacade.list(conditions));
    }

    /**
     * 根据ID获取用户信息
     *
     * @param id 用户ID
     * @return 返回对应ID的用户信息
     */
    @PreAuthorize("hasAuthority('system:user:read')")
    @GetMapping("/{id}")
    public Result<UserDetailDTO> getById(@PathVariable("id") long id) {
        UserDetailDTO dto = userFacade.getById(id);
        if (dto != null) {
            dto.setPassword(null);
            return new Result<>(OK, "查询成功", dto);
        } else {
            return new Result<>(NOT_FOUND, "找不到ID为" + id + "的用户信息", null);
        }
    }

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 返回对应用户名的用户信息
     */
    @PreAuthorize("hasAuthority('system:user:read')")
    @GetMapping("/getByUsername")
    public Result<UserDetailDTO> getByUsername(@RequestParam String username) {
        UserDetailDTO dto = userFacade.getByUsername(username);
        if (dto != null) {
            dto.setPassword(null);
            return new Result<>(OK, "查询成功", dto);
        } else {
            return new Result<>(NOT_FOUND, "找不到用户名为" + username + "的用户信息", null);
        }
    }

    /**
     * 获取用户的角色列表
     *
     * @param userId     用户ID
     * @param onlyEnable 只筛选启用的角色
     * @return 返回对应ID用户的角色列表
     */
    @PreAuthorize("hasAuthority('system:user:read')")
    @GetMapping("/roles/{userId}")
    public Result<List<RoleDTO>> getRolesByUserId(@PathVariable("userId") Long userId,
                                                  @RequestParam(value = "enable", defaultValue = "false") boolean onlyEnable) {
        return new Result<>(OK, "查询成功", userFacade.getRolesByUserId(userId, onlyEnable));
    }

    /**
     * 新增用户信息
     *
     * @return 新用户ID
     */
    @PreAuthorize("hasAuthority('system:user:save')")
    @PostMapping
    public Result<Long> save(@RequestBody @Validated UserSaveForm userSaveForm) {
        UserDetailDTO user = CurrentSessionHolder.getCurrentUser();
        RoleDTO currentRole = CurrentSessionHolder.getCurrentRole();
        userSaveForm.setCreatorId(user.getId());
        userSaveForm.setOwnerId(user.getId());
        userSaveForm.setOwnerRoleId(currentRole.getId());

        return new Result<>(OK, "新增成功", userFacade.save(userSaveForm));
    }

    /**
     * 修改用户信息
     */
    @PreAuthorize("hasAuthority('system:user:update')")
    @PutMapping
    public Result update(@RequestBody @Validated UserUpdateForm userUpdateForm) {
        userFacade.update(userUpdateForm);
        return new Result<>(OK, "修改成功", null);
    }

    /**
     * 根据ID删除用户信息
     *
     * @param id 用户ID
     */
    @PreAuthorize("hasAuthority('system:user:delete')")
    @DeleteMapping("/{id}")
    public Result<Integer> deleteById(@PathVariable("id") Long id) {
        userFacade.deleteById(id);
        return new Result<>(OK, "删除成功", null);
    }

    /**
     * 修改用户密码（包括其它用户）
     *
     * @param form 表单
     * @return 响应结果
     */
    @PreAuthorize("hasAuthority('system:user:update')")
    @PostMapping("/changePassword")
    public Result changePassword(@RequestBody @Validated UserChangePasswordForm form) {
        String username = form.getUsername();
        String password = form.getPassword();
        userFacade.changePassword(username, password);
        return new Result<>(OK, "重置密码成功", null);
    }

    /**
     * 修改个人密码
     *
     * @param form 表单
     * @return 响应结果
     */
    @PostMapping("/changeOwnerPassword")
    public Result changeOwnerPassword(@RequestBody @Validated UserChangeMyPasswordForm form) {
        String username = CurrentSessionHolder.getCurrentUser().getUsername();
        String password = form.getPassword();
        userFacade.changePassword(username, password);
        return new Result<>(OK, "重置密码成功", null);
    }
}
