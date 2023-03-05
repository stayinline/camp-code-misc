package com.geekbang.rpc.netty.client;

import com.geekbang.rpc.netty.protocol.Protocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.Callable;

public class ClientProxyHandler extends SimpleChannelInboundHandler<Object> implements Callable {

    private Protocol protocol;
    private Object result;
    private Channel channel;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channel = ctx.channel();
    }

    protected synchronized void channelRead0(ChannelHandlerContext channelHandlerContext, Object obj) throws Exception {
        this.result = obj;
        notify();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public synchronized Object call() throws Exception {

        //在Netty中，writeAndFlush方法是一个异步操作，调用之后会直接返回
        channel.writeAndFlush(protocol);
        wait();
        return result;
    }
}
