package com.geekbang.rpc.netty.service;

import com.geekbang.rpc.netty.service.UserService;

public class UserServiceImpl implements UserService {

    public String say(String name) {
        System.out.println("服务端触发调用");
        return "你好：" + name;
    }
}
