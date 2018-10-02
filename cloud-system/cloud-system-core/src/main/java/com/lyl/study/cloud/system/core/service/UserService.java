package com.lyl.study.cloud.system.core.service;

import com.baomidou.mybatisplus.service.IService;
import com.lyl.study.cloud.base.dto.PageInfo;
import com.lyl.study.cloud.base.exception.NoSuchEntityException;
import com.lyl.study.cloud.system.api.dto.request.UserListConditions;
import com.lyl.study.cloud.system.api.dto.request.UserSaveForm;
import com.lyl.study.cloud.system.api.dto.request.UserUpdateForm;
import com.lyl.study.cloud.system.api.dto.response.RoleDTO;
import com.lyl.study.cloud.system.core.entity.User;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author liyilin
 * @since 2018-09-07
 */
public interface UserService extends IService<User> {
    /**
     * 条件筛选用户分页列表
     *
     * @param conditions 筛选条件
     * @return 用户分页列表
     */
    PageInfo<User> listByConditions(UserListConditions conditions);

    /**
     * 查询用户带有的角色
     *
     * @param userId     用户ID
     * @param onlyEnable 只筛选启用的角色
     * @return 用户带有的角色
     */
    List<RoleDTO> getRolesByUserId(Long userId, boolean onlyEnable);

    /**
     * 新增用户信息
     *
     * @param userSaveForm 表单
     * @return 新增用户ID
     */
    long save(UserSaveForm userSaveForm);

    /**
     * 修改用户信息
     *
     * @param userUpdateForm 表单
     */
    void update(UserUpdateForm userUpdateForm);

    /**
     * 删除用户信息
     *
     * @param id 用户ID
     * @return 删除记录数
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
