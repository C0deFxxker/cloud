package com.lyl.study.cloud.gateway.core.service;

import com.lyl.study.cloud.base.dto.PageInfo;
import com.lyl.study.cloud.gateway.core.entity.Role;
import com.lyl.study.cloud.gateway.core.entity.User;
import com.baomidou.mybatisplus.service.IService;
import com.lyl.study.cloud.security.api.dto.request.UserListConditions;
import com.lyl.study.cloud.security.api.dto.request.UserSaveForm;
import com.lyl.study.cloud.security.api.dto.request.UserUpdateForm;

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
    PageInfo<User> listByConditions(UserListConditions conditions);

    List<Role> getRolesByUserId(Long userId);

    long save(UserSaveForm userSaveForm);

    void update(UserUpdateForm userUpdateForm);

    int deleteById(long id);
}
