package com.lyl.study.cloud.gateway.core.facade.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.lyl.study.cloud.base.dto.PageInfo;
import com.lyl.study.cloud.base.exception.NoSuchEntityException;
import com.lyl.study.cloud.gateway.core.entity.Role;
import com.lyl.study.cloud.gateway.core.entity.User;
import com.lyl.study.cloud.gateway.core.service.UserService;
import com.lyl.study.cloud.gateway.api.dto.request.UserListConditions;
import com.lyl.study.cloud.gateway.api.dto.request.UserSaveForm;
import com.lyl.study.cloud.gateway.api.dto.request.UserUpdateForm;
import com.lyl.study.cloud.gateway.api.dto.response.RoleDTO;
import com.lyl.study.cloud.gateway.api.dto.response.UserDTO;
import com.lyl.study.cloud.gateway.api.dto.response.UserDetailDTO;
import com.lyl.study.cloud.gateway.api.facade.UserFacade;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserFacadeImpl implements UserFacade {
    @Autowired
    private UserService userService;

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
    public List<RoleDTO> getRolesByUserId(long userId) throws NoSuchEntityException {
        User user = userService.selectById(userId);
        if (user == null) {
            throw new NoSuchEntityException("找不到ID为" + userId + "的用户信息");
        }

        List<Role> roleList = userService.getRolesByUserId(userId);
        return roleList.stream().map(entity -> {
            RoleDTO dto = new RoleDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public PageInfo<UserDTO> list(UserListConditions conditions) {
        PageInfo<User> pageInfo = userService.listByConditions(conditions);
        List<UserDTO> dtoList = pageInfo.getRecords().stream().map(entity -> {
            UserDTO dto = new UserDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }).collect(Collectors.toList());
        PageInfo<UserDTO> dtoPage = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        dtoPage.setRecords(dtoList);
        return dtoPage;
    }

    @Override
    public long save(UserSaveForm userSaveForm) {
        return userService.save(userSaveForm);
    }

    @Override
    public void update(UserUpdateForm userUpdateForm) {
        userService.update(userUpdateForm);
    }

    @Override
    public int deleteById(long id) {
        return userService.deleteById(id);
    }

    private UserDetailDTO getUserDetail(User user) {
        Assert.notNull(user, "user cannot be null");
        List<Role> roles = userService.getRolesByUserId(user.getId());
        List<RoleDTO> dtos = roles.stream().map(entity -> {
            RoleDTO dto = new RoleDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }).collect(Collectors.toList());
        UserDetailDTO userDetail = new UserDetailDTO();
        BeanUtils.copyProperties(user, userDetail);
        userDetail.setRoles(dtos);
        return userDetail;
    }
}
