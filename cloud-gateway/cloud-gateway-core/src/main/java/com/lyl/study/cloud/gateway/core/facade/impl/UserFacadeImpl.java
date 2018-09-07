package com.lyl.study.cloud.gateway.core.facade.impl;

import com.lyl.study.cloud.gateway.core.service.UserService;
import com.lyl.study.cloud.security.api.dto.response.RoleDTO;
import com.lyl.study.cloud.security.api.dto.response.UserDTO;
import com.lyl.study.cloud.security.api.dto.response.UserDetailDTO;
import com.lyl.study.cloud.security.api.facade.UserFacade;
import org.springframework.beans.factory.annotation.Autowired;

import java.rmi.NoSuchObjectException;
import java.util.List;

public class UserFacadeImpl implements UserFacade {
    @Autowired
    private UserService userService;

    @Override
    public UserDetailDTO getById(long id) {
        return null;
    }

    @Override
    public UserDetailDTO getByUsername(String username) {
        return null;
    }

    @Override
    public List<RoleDTO> getRolesByUserId(long userId) throws NoSuchObjectException {
        return null;
    }

    @Override
    public List<UserDTO> list(int pageIndex, int pageSize) {
        return null;
    }

    @Override
    public long save() {
        return 0;
    }

    @Override
    public void update() {

    }

    @Override
    public void deleteById(long id) {

    }
}
