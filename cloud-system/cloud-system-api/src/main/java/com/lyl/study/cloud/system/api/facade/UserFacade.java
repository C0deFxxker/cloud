package com.lyl.study.cloud.system.api.facade;

import com.lyl.study.cloud.base.dto.PageInfo;
import com.lyl.study.cloud.base.exception.NoSuchEntityException;
import com.lyl.study.cloud.system.api.dto.request.UserListConditions;
import com.lyl.study.cloud.system.api.dto.request.UserSaveForm;
import com.lyl.study.cloud.system.api.dto.request.UserUpdateForm;
import com.lyl.study.cloud.system.api.dto.response.RoleDTO;
import com.lyl.study.cloud.system.api.dto.response.UserDTO;
import com.lyl.study.cloud.system.api.dto.response.UserDetailDTO;

import java.util.List;

public interface UserFacade {
    /**
     * 根据ID获取用户信息
     *
     * @param id 用户ID
     * @return 返回对应ID的用户信息；找不到用户时，返回null
     */
    UserDetailDTO getById(long id);

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 返回对应用户名的用户信息；找不到用户时，返回null
     */
    UserDetailDTO getByUsername(String username);

    /**
     * 获取用户的角色列表
     *
     * @param userId     用户ID
     * @param onlyEnable 只筛选启用的角色
     * @return 返回对应ID用户的角色列表
     * @throws NoSuchEntityException 找不到用户时抛出次异常
     */
    List<RoleDTO> getRolesByUserId(long userId, boolean onlyEnable) throws NoSuchEntityException;

    /**
     * 查询用户列表
     *
     * @param conditions 查询表单
     * @return 用户列表
     */
    PageInfo<UserDTO> list(UserListConditions conditions);

    /**
     * 新增用户信息
     *
     * @return 新用户ID
     * @throws IllegalArgumentException 用户名已存在时，抛出此异常
     */
    long save(UserSaveForm userSaveForm) throws IllegalArgumentException;

    /**
     * 修改用户信息
     */
    void update(UserUpdateForm userUpdateForm);

    /**
     * 根据ID删除用户信息
     *
     * @param id 用户ID
     * @throws NoSuchEntityException 找不到用户
     */
    void deleteById(long id) throws NoSuchEntityException;

    /**
     * 修改用户密码
     *
     * @param username 用户名
     * @param password 密码
     * @throws NoSuchEntityException 找不到用户名对应的用户信息
     */
    void changePassword(String username, String password) throws NoSuchEntityException;
}