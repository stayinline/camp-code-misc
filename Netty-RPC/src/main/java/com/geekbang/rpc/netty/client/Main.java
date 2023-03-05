package com.geekbang.rpc.netty.client;


import com.geekbang.rpc.netty.service.UserService;

public class Main {
    public static void main(String[] args) {
//        MyClientBootStrap1 myClientBootStrap1 = new MyClientBootStrap1();
//        myClientBootStrap1.start("127.0.0.1", 8888);


        MyClientBootStrap2 myClientBootStrap2 = new MyClientBootStrap2();
        myClientBootStrap2.start("127.0.0.1",8888);
        UserService proxy = myClientBootStrap2.proxy(UserService.class);
        System.out.println(proxy.say("郑天民"));
    }
}
