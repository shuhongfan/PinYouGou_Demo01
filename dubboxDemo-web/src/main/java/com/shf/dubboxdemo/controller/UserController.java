package com.shf.dubboxdemo.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shf.dubboxdemo.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 用户控制器类
 */

@Controller
@RequestMapping("/user")
public class UserController {
    @Reference
    private UserService userService;

    @ResponseBody
    @RequestMapping("/showname")
    public String showName(){
        return userService.getName();
    }
}
