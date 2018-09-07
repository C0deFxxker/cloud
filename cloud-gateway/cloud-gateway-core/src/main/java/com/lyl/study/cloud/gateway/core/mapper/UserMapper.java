package com.lyl.study.cloud.gateway.core.mapper;

import com.lyl.study.cloud.gateway.core.entity.Role;
import com.lyl.study.cloud.gateway.core.entity.User;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.lyl.study.cloud.security.api.dto.request.UserListConditions;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author liyilin
 * @since 2018-09-07
 */
public interface UserMapper extends BaseMapper<User> {
    /**
     * 查询用户带有的角色
     * @param userId 用户ID
     * @return 用户带有的角色
     */
    List<Role> selectRolesByUserId(Long userId);

    /**
     *
     * @param userId
     * @param roleIds
     * @return
     */
    int insertUserRoles(@Param("userId") Long userId, @Param("roleIds") Collection<Long> roleIds);

    int deleteUserRolesByUserId(Long userId);

    List<User> selectByConditions(UserListConditions conditions);

    int countByConditions(UserListConditions conditions);
}
