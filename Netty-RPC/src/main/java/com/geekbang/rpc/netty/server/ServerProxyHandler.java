package com.geekbang.rpc.netty.server;

import com.geekbang.rpc.netty.protocol.Protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;

public class ServerProxyHandler extends SimpleChannelInboundHandler<Object> {

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object obj) throws Exception {
        Protocol protocol=(Protocol)obj;

        //找到目标实现类
        String className = protocol.getClassName()+"Impl";
        String methodName = protocol.getMethodName();
        Class<?>[] paramerTypes = protocol.getParamerTypes();
        Object[] paramValues = protocol.getParamValues();

        //通过反射创建实现类实例
        Class<?> clazz = Class.forName(className);
        Object bean = clazz.newInstance();
        Method method = clazz.getDeclaredMethod(methodName, paramerTypes);

        Object result = method.invoke(bean, paramValues);
        channelHandlerContext.writeAndFlush(result);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    }
}
