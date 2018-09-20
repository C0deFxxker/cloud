package com.lyl.study.cloud.gateway.core.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.lyl.study.cloud.gateway.api.dto.request.UserListConditions;
import com.lyl.study.cloud.gateway.api.dto.response.RoleDTO;
import com.lyl.study.cloud.gateway.core.entity.User;
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
     *
     * @param userId 用户ID
     * @param onlyEnable 只筛选正在启用的角色
     * @return 用户带有的角色
     */
    List<RoleDTO> selectRolesByUserId(@Param("userId") Long userId, @Param("onlyEnable") boolean onlyEnable);

    /**
     * 添加用户角色关联关系
     *
     * @param userId  用户ID
     * @param roleIds 角色ID集
     * @return 插入记录数
     */
    int insertUserRoles(@Param("userId") Long userId, @Param("roleIds") Collection<Long> roleIds);

    /**
     * 删除用户角色关联关系
     *
     * @param userId 用户ID
     * @return 删除记录数
     */
    int deleteUserRolesByUserId(Long userId);

    /**
     * 条件筛选用户列表
     *
     * @param conditions 筛选条件
     * @return 用户列表
     */
    List<User> selectByConditions(UserListConditions conditions);

    /**
     * 计算符合筛选条件的用户数量
     *
     * @param conditions 筛选条件
     * @return 符合筛选条件的用户数量
     */
    int countByConditions(UserListConditions conditions);
}
