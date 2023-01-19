package com.geekbang.rpc.server;

import com.geekbang.rpc.service.impl.UserServiceImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ServerTest {

    public  static  void main(String[] args) {

        Map<String, Object> servicePool = new HashMap<String, Object>();
        servicePool.put("com.geekbang.rpc.service.UserService", new UserServiceImpl());

        RpcServer server = new RpcServer(servicePool, 4, 9001);

        try{
            server.service();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
