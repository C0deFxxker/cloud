package com.lyl.study.cloud.gateway.core.service.impl;

import com.lyl.study.cloud.gateway.core.entity.User;
import com.lyl.study.cloud.gateway.core.mapper.UserMapper;
import com.lyl.study.cloud.gateway.core.service.UserService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author liyilin
 * @since 2018-09-07
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
