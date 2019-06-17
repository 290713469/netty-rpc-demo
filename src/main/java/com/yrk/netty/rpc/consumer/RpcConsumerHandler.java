package com.yrk.netty.rpc.consumer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class RpcConsumerHandler extends ChannelInboundHandlerAdapter {

    private Object response;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        response = msg;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        System.out.println("client is exception");
    }

    public Object getResponse() {
        return response;
    }
}
