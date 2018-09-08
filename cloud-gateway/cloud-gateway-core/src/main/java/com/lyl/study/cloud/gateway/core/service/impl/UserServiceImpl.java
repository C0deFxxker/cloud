package com.lyl.study.cloud.gateway.core.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.lyl.study.cloud.base.dto.PageInfo;
import com.lyl.study.cloud.base.idworker.Sequence;
import com.lyl.study.cloud.base.util.CryptoUtils;
import com.lyl.study.cloud.gateway.core.entity.Role;
import com.lyl.study.cloud.gateway.core.entity.User;
import com.lyl.study.cloud.gateway.core.mapper.UserMapper;
import com.lyl.study.cloud.gateway.core.service.UserService;
import com.lyl.study.cloud.gateway.api.dto.request.UserListConditions;
import com.lyl.study.cloud.gateway.api.dto.request.UserSaveForm;
import com.lyl.study.cloud.gateway.api.dto.request.UserUpdateForm;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author liyilin
 * @since 2018-09-07
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private Sequence sequence;

    @Override
    public List<Role> getRolesByUserId(Long userId) {
        return baseMapper.selectRolesByUserId(userId);
    }

    @Override
    @Transactional
    public long save(UserSaveForm userSaveForm) {
        User record = baseMapper.selectOne(new User().setUsername(userSaveForm.getUsername()));
        if (record != null) {
            throw new IllegalArgumentException("用户名已存在");
        }

        User user = new User();
        BeanUtils.copyProperties(userSaveForm, user);
        String encryptedPassword = CryptoUtils.hmacSha1(userSaveForm.getUsername(), userSaveForm.getPassword());
        user.setPassword(encryptedPassword);
        user.setId(sequence.nextId());
        baseMapper.insert(user);
        baseMapper.insertUserRoles(user.getId(), new HashSet<>(userSaveForm.getRoles()));
        return user.getId();
    }

    @Override
    @Transactional
    public void update(UserUpdateForm userUpdateForm) {
        Long userId = userUpdateForm.getId();
        Assert.notNull(userUpdateForm.getId(), "id cannot be null");

        User user = baseMapper.selectById(userId);
        BeanUtils.copyProperties(userUpdateForm, user);
        baseMapper.updateById(user);


        // 修改用户角色关联关系
        List<Role> roles = baseMapper.selectRolesByUserId(userId);
        Set<Long> recordRoleSet = roles.stream().map(Role::getId).collect(Collectors.toSet());
        Set<Long> formRoleSet = new HashSet<>(userUpdateForm.getRoles());
        if (!recordRoleSet.equals(formRoleSet)) {
            baseMapper.deleteUserRolesByUserId(userId);
            baseMapper.insertUserRoles(userId, formRoleSet);
        }
    }

    @Override
    @Transactional
    public int deleteById(long id) {
        int rows = baseMapper.deleteById(id);
        baseMapper.deleteUserRolesByUserId(id);
        return rows;
    }

    @Override
    public PageInfo<User> listByConditions(UserListConditions conditions) {
        Assert.notNull(conditions.getPageIndex(), "pageIndex cannot be null");
        Assert.notNull(conditions.getPageSize(), "pageSize cannot be null");

        PageInfo<User> pageInfo = new PageInfo<>(conditions.getPageIndex(), conditions.getPageSize());
        int offset = (conditions.getPageIndex() - 1) * conditions.getPageIndex();
        int rows = baseMapper.countByConditions(conditions);
        pageInfo.setTotal(rows);

        if (rows >= offset) {
            conditions.setOffset(offset);
            List<User> list = baseMapper.selectByConditions(conditions);
            pageInfo.setRecords(list);
        }

        return pageInfo;
    }
}
