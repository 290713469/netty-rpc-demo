package com.yrk.netty.rpc.consumer;

import com.yrk.netty.rpc.api.RpcHelloService;
import com.yrk.netty.rpc.api.RpcService;
import com.yrk.netty.rpc.provider.RpcHelloServiceBean;
import com.yrk.netty.rpc.provider.RpcServiceBean;

public class RpcConsumer {

    public static void main(String[] args) {
        RpcHelloService helloService = RpcProxy.create(RpcHelloService.class);
        System.out.println(helloService.hello("test"));

        RpcService rpcService = RpcProxy.create(RpcService.class);
        System.out.println("8 + 2 = " + rpcService.add(8, 2));
        System.out.println("8 - 2 = " + rpcService.sub(8, 2));
        System.out.println("8 * 2 = " + rpcService.multiple(8, 2));
        System.out.println("8 / 2 = " + rpcService.divide(8, 2));
    }
}
