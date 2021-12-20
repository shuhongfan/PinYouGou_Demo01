package com.shf.dubboxdemo.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.shf.dubboxdemo.UserService;

/**
 * 业务实现
 */
@Service(interfaceClass = UserService.class)
public class UserServiceImpl implements UserService {

    @Override
    public String getName() {
        return "shuhongfan";
    }
}
