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

public class MyClientBootStrap2 {
    private NioEventLoopGroup eventExecutors;
    private ClientProxyHandler clientProxyHandler;

    public void start(String host, int port) {
        try {
            clientProxyHandler = new ClientProxyHandler();
            Bootstrap bootstrap = new Bootstrap();
            eventExecutors = new NioEventLoopGroup();
            bootstrap.group(eventExecutors);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel channel) throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();
                    pipeline.addLast(new ObjectEncoder());
                    pipeline.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                    pipeline.addLast(clientProxyHandler);
                }
            });

            bootstrap.connect(host, port).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public <T> T proxy(final Class<T> target) {
        return (T) Proxy.newProxyInstance(target.getClassLoader(), new Class[]{target}, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Protocol protocol = new Protocol();
                protocol.setClassName(target.getName());
                protocol.setMethodName(method.getName());
                protocol.setParamerTypes(method.getParameterTypes());
                protocol.setParamValues(args);

                System.out.println(protocol);
                clientProxyHandler.setProtocol(protocol);
                FutureTask<Object> futureTask = new FutureTask<Object>(clientProxyHandler);
                new Thread(futureTask).start();
                return futureTask.get();
            }
        });
    }
}
