package com.yrk.netty.rpc.registry;

import com.yrk.netty.rpc.protocol.InvokerProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcRegistryHandler extends ChannelInboundHandlerAdapter {

    private List<String> classNames = new ArrayList<String>();
    private Map<String, Object> registryMap = new ConcurrentHashMap<String, Object>();

    public RpcRegistryHandler() {
        // 根据一个包名将所有符合条件的class全部扫描出来，放到一个容器中(简化版)
        scannerClass("com.yrk.netty.rpc.provider");
        // 给每一个对应的class起一个唯一的名字，作为服务名称，保存到一个容器中
        doRegistry();
    }

    private void doRegistry() {
        if (classNames.isEmpty()) {
            return;
        } else {
            for (String className: classNames) {
                try {
                    Class<?> clazz = Class.forName(className);
                    Class<?> i = clazz.getInterfaces()[0];
                    String serviceName = i.getName();
                    // 这里应该存的是网络的路径，从配置文件中读取，在调用的时候去解析
                    registryMap.put(serviceName, clazz.newInstance());
                } catch(Exception e) {
                    e.printStackTrace();
                }

             }
        }
    }

    private void scannerClass(String packageName) {
        URL url = this.getClass().getClassLoader().getResource(packageName.replaceAll("\\.", "/"));
        File classPath = new File(url.getFile());
        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                scannerClass(packageName + "." + file.getName());
            } else {
                classNames.add(packageName + "." + file.getName().replace(".class", ""));
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        InvokerProtocol invokerProtocol = (InvokerProtocol) msg;
        Object result = null;
        if (registryMap.containsKey(invokerProtocol.getClassName())) {
            Object service = registryMap.get(invokerProtocol.getClassName());
            Method method = service.getClass().getMethod(invokerProtocol.getMethodName(), invokerProtocol.getParamTypes());
            result = method.invoke(service, invokerProtocol.getParamValues());
        }
        ctx.writeAndFlush(result);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    }
}
