package com.yrk.netty.rpc.consumer;

import com.yrk.netty.rpc.protocol.InvokerProtocol;
import com.yrk.netty.rpc.registry.RpcRegistryHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RpcProxy {

    public static <T> T create(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] {clazz}, new MethodProxy(clazz));
    }

    private static class MethodProxy implements InvocationHandler {

        private Class<?> clazz = null;

        public MethodProxy(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, args);
            } else {
                return rpcInvoker(proxy, method, args);
            }

        }

        private Object rpcInvoker(Object proxy, Method method, Object[] args) {

            InvokerProtocol invokerProtocol = new InvokerProtocol();
            invokerProtocol.setClassName(this.clazz.getName());
            invokerProtocol.setMethodName(method.getName());
            invokerProtocol.setParamTypes(method.getParameterTypes());
            invokerProtocol.setParamValues(args);
            final RpcConsumerHandler rpcConsumerHandler = new RpcConsumerHandler();
            EventLoopGroup group = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,
                                    0, 4, 0, 4));
                            ch.pipeline().addLast(new LengthFieldPrepender(4));

                            // 实参处理
                            ch.pipeline().addLast("encoder", new ObjectEncoder());
                            ch.pipeline().addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE,
                                    ClassResolvers.cacheDisabled(null)));
                            ch.pipeline().addLast(rpcConsumerHandler);
                        }
                    });
                ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 9080).sync();
                channelFuture.channel().writeAndFlush(invokerProtocol).sync();
                channelFuture.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                group.shutdownGracefully();
            }
            return rpcConsumerHandler.getResponse();
        }
    }
}
