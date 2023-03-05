package com.geekbang.rpc.netty.client;

import com.geekbang.rpc.netty.protocol.Protocol;
import com.geekbang.rpc.netty.service.UserService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.FutureTask;

public class MyClientBootStrap1 {
    private NioEventLoopGroup eventExecutors;
    private ClientProxyHandler clientProxyHandler;

    public void start(String host, int port) {

        Protocol protocol = new Protocol();
        protocol.setClassName("com.geekbang.rpc.netty.service.UserService");
        protocol.setMethodName("say");

        Class[] paramsTypes = {String.class};
        Object[] parameters = {"郑天民"};
        protocol.setParamerTypes(paramsTypes);
        protocol.setParamValues(parameters);

        final ClientHandler clientHandler = new ClientHandler(protocol);
        try {
            Bootstrap bootstrap = new Bootstrap();
            eventExecutors = new NioEventLoopGroup();
            bootstrap.group(eventExecutors);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel channel) throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();
                    pipeline.addLast(new ObjectEncoder());
                    pipeline.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                    pipeline.addLast(clientHandler);
                }
            });

            bootstrap.connect(host, port).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(clientHandler.getResult());
    }

}
