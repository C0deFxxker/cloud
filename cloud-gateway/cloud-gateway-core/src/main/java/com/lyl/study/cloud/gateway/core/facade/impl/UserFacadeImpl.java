package com.lyl.study.cloud.gateway.core.facade.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.lyl.study.cloud.base.dto.PageInfo;
import com.lyl.study.cloud.base.exception.InvalidArgumentException;
import com.lyl.study.cloud.base.exception.NoSuchEntityException;
import com.lyl.study.cloud.gateway.api.GatewayErrorCode;
import com.lyl.study.cloud.gateway.api.dto.request.UserListConditions;
import com.lyl.study.cloud.gateway.api.dto.request.UserSaveForm;
import com.lyl.study.cloud.gateway.api.dto.request.UserUpdateForm;
import com.lyl.study.cloud.gateway.api.dto.response.RoleDTO;
import com.lyl.study.cloud.gateway.api.dto.response.UserDTO;
import com.lyl.study.cloud.gateway.api.dto.response.UserDetailDTO;
import com.lyl.study.cloud.gateway.api.facade.UserFacade;
import com.lyl.study.cloud.gateway.core.entity.Role;
import com.lyl.study.cloud.gateway.core.entity.User;
import com.lyl.study.cloud.gateway.core.service.RoleService;
import com.lyl.study.cloud.gateway.core.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.lyl.study.cloud.base.CommonErrorCode.BAD_REQUEST;

@Service
public class UserFacadeImpl implements UserFacade {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;

    @Override
    public UserDetailDTO getById(long id) {
        User user = userService.selectById(id);
        return user != null ? getUserDetail(user) : null;
    }

    @Override
    public UserDetailDTO getByUsername(String username) {
        User user = userService.selectOne(new EntityWrapper<User>().eq(User.USERNAME, username));
        return user != null ? getUserDetail(user) : null;
    }

    @Override
    public List<RoleDTO> getRolesByUserId(long userId, boolean onlyEnable) throws NoSuchEntityException {
        User user = userService.selectById(userId);
        if (user == null) {
            throw new NoSuchEntityException(GatewayErrorCode.NOT_FOUND, "找不到ID为" + userId + "的用户信息");
        }

        return userService.getRolesByUserId(userId, onlyEnable);
    }

    @Override
    public PageInfo<UserDTO> list(UserListConditions conditions) {
        PageInfo<User> pageInfo = userService.listByConditions(conditions);
        List<UserDTO> dtoList = pageInfo.getRecords().stream().map(entity -> {
            UserDTO dto = new UserDTO();
            BeanUtils.copyProperties(entity, dto);
            // 列表不能展示密码
            dto.setPassword(null);
            return dto;
        }).collect(Collectors.toList());
        PageInfo<UserDTO> dtoPage = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        dtoPage.setRecords(dtoList);
        return dtoPage;
    }

    @Override
    public long save(UserSaveForm userSaveForm) throws IllegalArgumentException {
        User record = userService.selectOne(new EntityWrapper<User>().eq(User.USERNAME, userSaveForm.getUsername()));
        if (record != null) {
            throw new InvalidArgumentException(BAD_REQUEST, "用户名已存在");
        }

        List<Long> roles = userSaveForm.getRoles();
        List<Long> notExistsRoles = getNotExistsRoles(roles);
        if (!notExistsRoles.isEmpty()) {
            throw new InvalidArgumentException(BAD_REQUEST, "用户角色不存在: " + notExistsRoles.toString());
        }

        return userService.save(userSaveForm);
    }

    @Override
    public void update(UserUpdateForm userUpdateForm) {
        List<Long> roles = userUpdateForm.getRoles();
        List<Long> notExistsRoles = getNotExistsRoles(roles);
        if (!notExistsRoles.isEmpty()) {
            throw new InvalidArgumentException(BAD_REQUEST, "用户角色不存在: " + notExistsRoles.toString());
        }

        userService.update(userUpdateForm);
    }

    @Override
    public void deleteById(long id) throws NoSuchEntityException {
        userService.deleteById(id);
    }

    @Override
    public void changePassword(String username, String password) throws NoSuchEntityException {
        userService.changePassword(username, password);
    }

    private UserDetailDTO getUserDetail(User user) {
        Assert.notNull(user, "user cannot be null");
        List<RoleDTO> roles = userService.getRolesByUserId(user.getId(), true);
        UserDetailDTO userDetail = new UserDetailDTO();
        BeanUtils.copyProperties(user, userDetail);
        userDetail.setRoles(roles);
        return userDetail;
    }

    private List<Long> getNotExistsRoles(List<Long> roles) {
        if (roles != null && !roles.isEmpty()) {
            List<Role> records = roleService.selectList(new EntityWrapper<Role>().in(Role.ID, roles));
            List<Long> recordIds = records.stream().map(Role::getId).collect(Collectors.toList());
            return roles.stream().filter(each -> !recordIds.contains(each))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>(0);
    }
}
