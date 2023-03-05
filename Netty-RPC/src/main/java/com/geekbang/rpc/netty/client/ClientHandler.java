package com.geekbang.rpc.netty.client;

import com.geekbang.rpc.netty.protocol.Protocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<Object> {

    private Protocol protocol;
    private Object result;
    private Channel channel;

    public ClientHandler(Protocol protocol) {
        this.protocol = protocol;
    }

    public Object getResult() {
        return result;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channel = ctx.channel();

        //在Netty中，writeAndFlush方法是一个异步操作，调用之后会直接返回
        channel.writeAndFlush(protocol);
    }

    protected synchronized void channelRead0(ChannelHandlerContext channelHandlerContext, Object obj) throws Exception {
        this.result = obj;

        System.out.println(result);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    }

}
