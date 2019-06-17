package com.yrk.netty.rpc.registry;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.util.concurrent.EventExecutorGroup;

public class RpcRegistry {

    private int port;

    public RpcRegistry(int port) {
        this.port = port;
    }

    public void start() {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workGroup).option(ChannelOption.SO_BACKLOG, 100)
                    .channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    // 在Netty中把所有的业务逻辑处理归总到一个队列中
                    // 队列中包含了各种各样的处理逻辑

                    ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                    ch.pipeline().addLast(new LengthFieldPrepender(4));

                    // 实参处理
                    ch.pipeline().addLast("encoder", new ObjectEncoder());
                    ch.pipeline().addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                    ch.pipeline().addLast(new RpcRegistryHandler());
                }


            }).option(ChannelOption.SO_BACKLOG, 100).childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture channelFuture = serverBootstrap.bind(this.port).sync();
            System.out.println("RPC Registry start monitor at " + this.port);
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new RpcRegistry(9080).start();
    }

}
